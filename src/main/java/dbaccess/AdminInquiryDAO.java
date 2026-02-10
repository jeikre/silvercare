package dbaccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminInquiryDAO {

    // DTO / simple row class
    public static class ServiceDemandRow {
        public int serviceId;
        public String serviceName;
        public int totalQty;
        public int ordersCount;
        public int slotsCount;

        public ServiceDemandRow(int serviceId, String serviceName, int totalQty, int ordersCount, int slotsCount) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.totalQty = totalQty;
            this.ordersCount = ordersCount;
            this.slotsCount = slotsCount;
        }
    }

    /**
     * Demand per service (only PAID orders), includes:
     * - total booked quantity
     * - number of paid orders that include the service
     * - number of configured time slots (service_time_slot count)
     */
    public List<ServiceDemandRow> getServiceDemandSummary() throws SQLException {

        String sql =
            "SELECT " +
            "  s.service_id, s.service_name, " +
            "  COALESCE(SUM(oi.quantity), 0) AS total_qty, " +
            "  COUNT(DISTINCT o.order_id) AS orders_count, " +
            "  (SELECT COUNT(*) FROM service_time_slot sts WHERE sts.service_id = s.service_id) AS slots_count " +
            "FROM service s " +
            "LEFT JOIN order_items oi ON oi.service_id = s.service_id " +
            "LEFT JOIN orders o ON o.order_id = oi.order_id AND o.status = 'PAID' " +
            "GROUP BY s.service_id, s.service_name " +
            "ORDER BY total_qty DESC, orders_count DESC, s.service_name ASC";

        List<ServiceDemandRow> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ServiceDemandRow(
                    rs.getInt("service_id"),
                    rs.getString("service_name"),
                    rs.getInt("total_qty"),
                    rs.getInt("orders_count"),
                    rs.getInt("slots_count")
                ));
            }
        }

        return list;
    }

    // Top N demanded services
    public List<ServiceDemandRow> getTopDemanded(int limit) throws SQLException {
        List<ServiceDemandRow> all = getServiceDemandSummary();
        return all.subList(0, Math.min(limit, all.size()));
    }

    // Bottom N demanded services (least booked)
    public List<ServiceDemandRow> getLowestDemanded(int limit) throws SQLException {
        List<ServiceDemandRow> all = getServiceDemandSummary();
        all.sort((a,b) -> {
            if (a.totalQty != b.totalQty) return Integer.compare(a.totalQty, b.totalQty);
            if (a.ordersCount != b.ordersCount) return Integer.compare(a.ordersCount, b.ordersCount);
            return a.serviceName.compareToIgnoreCase(b.serviceName);
        });
        return all.subList(0, Math.min(limit, all.size()));
    }

    /**
     * Services with low configured availability (few time slots).
     * Example: slots_count <= threshold.
     */
    public List<ServiceDemandRow> getLowSlotServices(int threshold) throws SQLException {

        String sql =
            "SELECT " +
            "  s.service_id, s.service_name, " +
            "  (SELECT COUNT(*) FROM service_time_slot sts WHERE sts.service_id = s.service_id) AS slots_count, " +
            "  COALESCE((SELECT SUM(oi.quantity) " +
            "            FROM order_items oi JOIN orders o ON o.order_id=oi.order_id " +
            "            WHERE oi.service_id=s.service_id AND o.status='PAID'), 0) AS total_qty, " +
            "  COALESCE((SELECT COUNT(DISTINCT o2.order_id) " +
            "            FROM order_items oi2 JOIN orders o2 ON o2.order_id=oi2.order_id " +
            "            WHERE oi2.service_id=s.service_id AND o2.status='PAID'), 0) AS orders_count " +
            "FROM service s " +
            "HAVING slots_count <= ? " +
            "ORDER BY slots_count ASC, total_qty DESC, s.service_name ASC";

        List<ServiceDemandRow> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, threshold);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ServiceDemandRow(
                        rs.getInt("service_id"),
                        rs.getString("service_name"),
                        rs.getInt("total_qty"),
                        rs.getInt("orders_count"),
                        rs.getInt("slots_count")
                    ));
                }
            }
        }

        return list;
    }
}
