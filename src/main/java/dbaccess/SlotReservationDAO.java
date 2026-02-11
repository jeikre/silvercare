package dbaccess;

import java.sql.*;
import java.time.LocalDate;

public class SlotReservationDAO {

    // HOLD expires after X minutes
    private static final int HOLD_MINUTES = 15;

    public boolean tryHold(int serviceId, LocalDate bookingDate, int slotId, int userId) throws SQLException {

        // 1) Clear expired holds
        cleanupExpired();

        // 2) Try insert (unique constraint prevents double booking)
        String sql = """
            INSERT INTO service_slot_reservation (service_id, booking_date, slot_id, user_id, status)
            VALUES (?, ?, ?, ?, 'HELD')
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            ps.setDate(2, Date.valueOf(bookingDate));
            ps.setInt(3, slotId);
            ps.setInt(4, userId);

            ps.executeUpdate();
            return true; // success
        } catch (SQLIntegrityConstraintViolationException dup) {
            return false; // already held/paid by someone else
        }
    }

    public void releaseHold(int serviceId, LocalDate bookingDate, int slotId, int userId) throws SQLException {
        String sql = """
            DELETE FROM service_slot_reservation
            WHERE service_id=? AND booking_date=? AND slot_id=? AND user_id=? AND status='HELD'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            ps.setDate(2, Date.valueOf(bookingDate));
            ps.setInt(3, slotId);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public void markPaid(int serviceId, LocalDate bookingDate, int slotId, int userId) throws SQLException {
        String sql = """
            UPDATE service_slot_reservation
            SET status='PAID'
            WHERE service_id=? AND booking_date=? AND slot_id=? AND user_id=? AND status='HELD'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            ps.setDate(2, Date.valueOf(bookingDate));
            ps.setInt(3, slotId);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public void cleanupExpired() throws SQLException {
        String sql = """
            DELETE FROM service_slot_reservation
            WHERE status='HELD'
              AND created_at < (NOW() - INTERVAL ? MINUTE)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, HOLD_MINUTES);
            ps.executeUpdate();
        }
    }
}
