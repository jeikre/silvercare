package dbaccess;

import models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getProducts(Integer categoryId, String sort) throws SQLException {

        StringBuilder sql = new StringBuilder(
            "SELECT p.product_id, p.category_id, p.product_name, p.product_description, p.price, p.image_path, " +
            "       c.category_name " +
            "FROM products p " +
            "JOIN service_category c ON p.category_id = c.category_id"
        );

        if (categoryId != null) sql.append(" WHERE p.category_id = ?");

        if ("name".equals(sort)) sql.append(" ORDER BY p.product_name ASC");
        else if ("price".equals(sort)) sql.append(" ORDER BY p.price ASC");
        else if ("category".equals(sort)) sql.append(" ORDER BY c.category_name ASC, p.product_name ASC");

        List<Product> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (categoryId != null) ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("product_id"));
                    p.setCategoryId((Integer) rs.getObject("category_id"));
                    p.setProductName(rs.getString("product_name"));
                    p.setProductDescription(rs.getString("product_description"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setImagePath(rs.getString("image_path"));

                    // if your Product model doesn't have categoryName, we will set request attr separately
                    p.setCategoryName(rs.getString("category_name")); // ✅ add this field if missing (see below)
                    list.add(p);
                }
            }
        }

        return list;
    }

    public Product getProductById(int productId) throws SQLException {
        String sql =
            "SELECT p.product_id, p.category_id, p.product_name, p.product_description, p.price, p.image_path, " +
            "       c.category_name " +
            "FROM products p " +
            "JOIN service_category c ON p.category_id = c.category_id " +
            "WHERE p.product_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setCategoryId((Integer) rs.getObject("category_id"));
                p.setProductName(rs.getString("product_name"));
                p.setProductDescription(rs.getString("product_description"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setImagePath(rs.getString("image_path"));
                p.setCategoryName(rs.getString("category_name")); // ✅ add this field if missing
                return p;
            }
        }
    }
}
