package dbaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CaregiverServiceDAO {

	public boolean assignCaregiverToService(int caregiverId, int serviceId) throws SQLException {
	    String sql =
	        "INSERT INTO caregiver_service (caregiver_id, service_id) " +
	        "SELECT ?, ? FROM DUAL " +
	        "WHERE NOT EXISTS (SELECT 1 FROM caregiver_service WHERE service_id = ?)";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, caregiverId);
	        ps.setInt(2, serviceId);
	        ps.setInt(3, serviceId);
	        return ps.executeUpdate() > 0;
	    }
	}

}
