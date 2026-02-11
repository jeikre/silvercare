package dbaccess;

import java.math.BigDecimal;

import models.CartItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // Get cart_id for a user; if none, create one.
    public int getOrCreateCartId(int userId) throws SQLException {
        String findSql = "SELECT cart_id FROM client_cart WHERE user_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(findSql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cart_id");
            }
        }

        String insertSql = "INSERT INTO client_cart (user_id) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }

        throw new SQLException("Failed to create cart for user_id=" + userId);
    }

    public void updateQuantity(int itemId, int qty) throws SQLException {
        String sql = "UPDATE client_cart_items SET quantity = ? WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }

    public boolean removeItem(int userId, int itemId) throws SQLException {
        String sql =
            "DELETE i FROM client_cart_items i " +
            "JOIN client_cart c ON c.cart_id = i.cart_id " +
            "WHERE c.user_id = ? AND i.item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, itemId);

            return ps.executeUpdate() > 0;
        }
    }



    public int clearCart(int userId) throws SQLException {
        String sql =
            "DELETE i FROM client_cart_items i " +
            "JOIN client_cart c ON c.cart_id = i.cart_id " +
            "WHERE c.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        }
    }


    public void addProduct(int userId, int productId, int qty) throws SQLException {
        int cartId = getOrCreateCartId(userId);

        String find = "SELECT item_id, quantity FROM client_cart_items WHERE cart_id=? AND product_id=? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(find)) {

            ps.setInt(1, cartId);
            ps.setInt(2, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int itemId = rs.getInt("item_id");
                    int newQty = rs.getInt("quantity") + qty;
                    updateQuantity(itemId, newQty);
                    return;
                }
            }
        }

        String insert = "INSERT INTO client_cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {

            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.executeUpdate();
        }
    }

    public void addServiceWithBooking(int userId, int serviceId, int qty,
            String bookingDate, String bookingTime, String bookingTimeDisplay,
            Integer slotId) throws SQLException {

        int cartId = getOrCreateCartId(userId);

        String find = "SELECT item_id FROM client_cart_items WHERE cart_id=? AND service_id=? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(find)) {

            ps.setInt(1, cartId);
            ps.setInt(2, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int itemId = rs.getInt("item_id");

                    String upd = """
                        UPDATE client_cart_items
                        SET quantity=?, booking_date=?, booking_time=?, booking_time_display=?, slot_id=?
                        WHERE item_id=?
                    """;

                    try (PreparedStatement ups = conn.prepareStatement(upd)) {
                        ups.setInt(1, Math.max(qty, 1));
                        ups.setString(2, bookingDate);

                        // booking_time safe insert (HH:mm or HH:mm:ss)
                        String bt = bookingTime;
                        if (bt == null || bt.isBlank() || "-".equals(bt)) {
                            ups.setNull(3, Types.TIME);
                        } else {
                            bt = bt.trim();
                            if (bt.length() == 5) bt = bt + ":00";
                            ups.setTime(3, Time.valueOf(bt));
                        }

                        ups.setString(4, bookingTimeDisplay);

                        if (slotId == null) ups.setNull(5, Types.INTEGER);
                        else ups.setInt(5, slotId);

                        ups.setInt(6, itemId);

                        ups.executeUpdate();
                    }
                    return;
                }
            }
        }

        String insert = """
            INSERT INTO client_cart_items
                (cart_id, service_id, quantity, booking_date, booking_time, booking_time_display, slot_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {

            ps.setInt(1, cartId);
            ps.setInt(2, serviceId);
            ps.setInt(3, Math.max(qty, 1));
            ps.setString(4, bookingDate);

            // booking_time safe insert (HH:mm or HH:mm:ss)
            String bt = bookingTime;
            if (bt == null || bt.isBlank() || "-".equals(bt)) {
                ps.setNull(5, Types.TIME);
            } else {
                bt = bt.trim();
                if (bt.length() == 5) bt = bt + ":00";
                ps.setTime(5, Time.valueOf(bt));
            }

            ps.setString(6, bookingTimeDisplay);

            if (slotId == null) ps.setNull(7, Types.INTEGER);
            else ps.setInt(7, slotId);

            ps.executeUpdate();
        }
    }

    public void addService(int userId, int serviceId, int qty) throws SQLException {
        int cartId = getOrCreateCartId(userId);

        String find = "SELECT item_id, quantity FROM client_cart_items WHERE cart_id=? AND service_id=? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(find)) {

            ps.setInt(1, cartId);
            ps.setInt(2, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int itemId = rs.getInt("item_id");
                    int newQty = rs.getInt("quantity") + qty;
                    updateQuantity(itemId, newQty);
                    return;
                }
            }
        }

        String insert = "INSERT INTO client_cart_items (cart_id, service_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {

            ps.setInt(1, cartId);
            ps.setInt(2, serviceId);
            ps.setInt(3, qty);
            ps.executeUpdate();
        }
    }

    // Load cart items
    public List<CartItem> getCartItems(int userId) throws SQLException {
        int cartId = getOrCreateCartId(userId);

        String sql = """
        	    SELECT i.item_id, i.cart_id, i.product_id, i.service_id, i.quantity,
        	           i.booking_date, i.booking_time, i.booking_time_display,
        	           i.slot_id,
        	           COALESCE(p.product_name, s.service_name) AS item_name,
        	           COALESCE(p.price, s.price) AS unit_price,
        	           COALESCE(p.image_path, s.service_image) AS image_path
        	    FROM client_cart_items i
        	    LEFT JOIN products p ON i.product_id = p.product_id
        	    LEFT JOIN service s ON i.service_id = s.service_id
        	    WHERE i.cart_id = ?
        	    ORDER BY i.item_id DESC
        	""";


        List<CartItem> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem it = new CartItem();
                    it.setItemId(rs.getInt("item_id"));
                    it.setCartId(rs.getInt("cart_id"));
                    it.setProductId((Integer) rs.getObject("product_id"));
                    it.setServiceId((Integer) rs.getObject("service_id"));
                    it.setQuantity(rs.getInt("quantity"));

                    it.setItemName(rs.getString("item_name"));
                    it.setUnitPrice(rs.getBigDecimal("unit_price"));
                    it.setImagePath(rs.getString("image_path"));

                    it.setBookingDate(rs.getString("booking_date"));
                    it.setBookingTime(rs.getString("booking_time"));
                    it.setBookingTimeDisplay(rs.getString("booking_time_display"));
                    it.setSlotId((Integer) rs.getObject("slot_id"));

                    list.add(it);
                }
            }
        }

        return list;
    }

    // ========= FIXED: These methods must be OUTSIDE, NOT inside another method ========

    public Integer findCartId(int userId) throws SQLException {
        String sql = "SELECT cart_id FROM client_cart WHERE user_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cart_id") : null;
            }
        }
    }

    public int getCartCountForNavbar(int userId) throws SQLException {
        Integer cartId = findCartId(userId);
        if (cartId == null) return 0;

        String sql = "SELECT COALESCE(SUM(quantity),0) AS cnt FROM client_cart_items WHERE cart_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }
    public int getCartCount(int userId) throws SQLException {
        int cartId = getOrCreateCartId(userId);

        String sql = "SELECT COALESCE(SUM(quantity),0) AS cnt FROM client_cart_items WHERE cart_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }
    public int updateProductQuantity(int userId, int itemId, int qty) throws SQLException {
        int cartId = getOrCreateCartId(userId);

        String sql = """
            UPDATE client_cart_items
            SET quantity = ?
            WHERE item_id = ?
              AND cart_id = ?
              AND product_id IS NOT NULL
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setInt(2, itemId);
            ps.setInt(3, cartId);

            return ps.executeUpdate(); // returns rows affected
        }
    }
    public Integer getCartIdByUserId(int userId) throws SQLException {
        String sql = "SELECT cart_id FROM client_cart WHERE user_id=? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cart_id");
                return null;
            }
        }
    }
    public BigDecimal getCartTotalByUserId(int userId) throws SQLException {
        String sql =
            "SELECT COALESCE(SUM(ci.quantity * COALESCE(p.price, s.price)), 0) AS total " +
            "FROM client_cart c " +
            "JOIN client_cart_items ci ON ci.cart_id = c.cart_id " +
            "LEFT JOIN products p ON p.product_id = ci.product_id " +   // ✅ products
            "LEFT JOIN service s ON s.service_id = ci.service_id " +
            "WHERE c.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return BigDecimal.ZERO;
                BigDecimal total = rs.getBigDecimal("total");
                return (total == null) ? BigDecimal.ZERO : total;
            }
        }
    }

 // ===================== ORDERS (CLEAN) =====================

    public int createOrder(int userId, String currency, BigDecimal subtotal, BigDecimal gst, BigDecimal total)
            throws SQLException {

        String sql = """
            INSERT INTO orders (user_id, currency, subtotal, gst, total, status)
            VALUES (?, ?, ?, ?, ?, 'CREATED')
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setString(2, currency);
            ps.setBigDecimal(3, subtotal);
            ps.setBigDecimal(4, gst);
            ps.setBigDecimal(5, total);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }

        throw new SQLException("Failed to create order for user_id=" + userId);
    }

    public void attachPaypalOrderId(int orderId, String paypalOrderId) throws SQLException {
        String sql = "UPDATE orders SET paypal_order_id=? WHERE order_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, paypalOrderId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public void markOrderPaid(int orderId, String paypalOrderId) throws SQLException {
        String sql = "UPDATE orders SET status='PAID', paypal_order_id=? WHERE order_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, paypalOrderId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

}
