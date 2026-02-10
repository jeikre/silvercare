package dbaccess;

import models.Caregiver;
import models.CaregiverServiceView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaregiverDAO {
    public Caregiver findById(int id) throws SQLException {
        String sql = "SELECT * FROM caregiver WHERE caregiver_id=? AND status='ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCaregiver(rs);
            }
        }
        return null;
    }

    // caregivers with service + category
    public List<CaregiverServiceView> findAllWithServiceAndCategory() throws SQLException {
        String sql =
            "SELECT c.caregiver_id, c.name AS caregiver_name, c.bio, c.qualifications, c.languages, " +
            "       c.experience_years, c.hourly_rate, c.photo_path, " +
            "       s.service_id, s.service_name, " +
            "       cat.category_id, cat.category_name " +
            "FROM caregiver c " +
            "JOIN caregiver_service cs ON cs.caregiver_id = c.caregiver_id " +
            "JOIN service s ON s.service_id = cs.service_id " +
            "JOIN service_category cat ON cat.category_id = s.category_id " +
            "WHERE c.status='ACTIVE' " +
            "ORDER BY cat.category_id, s.service_id, c.caregiver_id";

        List<CaregiverServiceView> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CaregiverServiceView v = new CaregiverServiceView();
                v.setCaregiverId(rs.getInt("caregiver_id"));
                v.setCaregiverName(rs.getString("caregiver_name"));
                v.setBio(rs.getString("bio"));
                v.setQualifications(rs.getString("qualifications"));
                v.setLanguages(rs.getString("languages"));
                v.setExperienceYears(rs.getInt("experience_years"));
                v.setHourlyRate(rs.getDouble("hourly_rate"));
                v.setPhotoPath(rs.getString("photo_path"));

                v.setServiceId(rs.getInt("service_id"));
                v.setServiceName(rs.getString("service_name"));

                v.setCategoryId(rs.getInt("category_id"));
                v.setCategoryName(rs.getString("category_name"));

                list.add(v);
            }
        }
        return list;
    }

    // services for profile page
    public List<CaregiverServiceView> findServicesByCaregiver(int caregiverId) throws SQLException {
        String sql =
            "SELECT c.caregiver_id, c.name AS caregiver_name, " +
            "       s.service_id, s.service_name, " +
            "       cat.category_id, cat.category_name " +
            "FROM caregiver c " +
            "JOIN caregiver_service cs ON cs.caregiver_id = c.caregiver_id " +
            "JOIN service s ON s.service_id = cs.service_id " +
            "JOIN service_category cat ON cat.category_id = s.category_id " +
            "WHERE c.caregiver_id=? AND c.status='ACTIVE' " +
            "ORDER BY s.service_id";

        List<CaregiverServiceView> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, caregiverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CaregiverServiceView v = new CaregiverServiceView();
                    v.setCaregiverId(rs.getInt("caregiver_id"));
                    v.setCaregiverName(rs.getString("caregiver_name"));
                    v.setServiceId(rs.getInt("service_id"));
                    v.setServiceName(rs.getString("service_name"));
                    v.setCategoryId(rs.getInt("category_id"));
                    v.setCategoryName(rs.getString("category_name"));
                    list.add(v);
                }
            }
        }
        return list;
    }

    private Caregiver mapCaregiver(ResultSet rs) throws SQLException {
        Caregiver c = new Caregiver();
        c.setCaregiverId(rs.getInt("caregiver_id"));
        c.setName(rs.getString("name"));
        c.setBio(rs.getString("bio"));
        c.setQualifications(rs.getString("qualifications"));
        c.setLanguages(rs.getString("languages"));
        c.setExperienceYears(rs.getInt("experience_years"));
        c.setHourlyRate(rs.getDouble("hourly_rate"));
        c.setPhotoPath(rs.getString("photo_path"));
        c.setStatus(rs.getString("status"));
        return c;
    }
    public List<Caregiver> getAllCaregivers() throws SQLException {
        String sql =
            "SELECT caregiver_id, name, bio, qualifications, languages, " +
            "experience_years, hourly_rate, photo_path, status " +
            "FROM caregiver ORDER BY name ASC";

        List<Caregiver> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Caregiver c = new Caregiver();
                c.setCaregiverId(rs.getInt("caregiver_id"));
                c.setName(rs.getString("name"));
                c.setBio(rs.getString("bio"));
                c.setQualifications(rs.getString("qualifications"));
                c.setLanguages(rs.getString("languages"));
                c.setExperienceYears(rs.getInt("experience_years"));
                c.setHourlyRate(rs.getDouble("hourly_rate"));  // <--- double
                c.setPhotoPath(rs.getString("photo_path"));
                c.setStatus(rs.getString("status"));

                list.add(c);
            }
        }
        return list;
    }

    public Caregiver findByServiceId(int serviceId) throws SQLException {

        String sql =
            "SELECT c.* " +
            "FROM caregiver_service cs " +
            "JOIN caregiver c ON c.caregiver_id = cs.caregiver_id " +
            "WHERE cs.service_id = ? AND c.status='ACTIVE' " +
            "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCaregiver(rs);
            }
        }
        return null;
    }

    // ============================
    // INSERT CAREGIVER
    // ============================
    public int insertCaregiver(Caregiver c) throws SQLException {
        String sql = "INSERT INTO caregiver (name, bio, qualifications, languages, experience_years, hourly_rate, photo_path, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getBio());
            ps.setString(3, c.getQualifications());
            ps.setString(4, c.getLanguages());
            ps.setInt(5, c.getExperienceYears());
            ps.setDouble(6, c.getHourlyRate());
            ps.setString(7, c.getPhotoPath());
            ps.setString(8, c.getStatus());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);  // 👍 generated caregiver_id
            }
        }

        return 0;
    }

    public int insertCaregiverReturnId(Caregiver c) throws SQLException {
        String sql =
            "INSERT INTO caregiver (name, bio, qualifications, languages, experience_years, hourly_rate, photo_path, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getBio());
            ps.setString(3, c.getQualifications());
            ps.setString(4, c.getLanguages());
            ps.setInt(5, c.getExperienceYears());
            ps.setDouble(6, c.getHourlyRate());
            ps.setString(7, c.getPhotoPath());
            ps.setString(8, c.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        }
    }
    public Caregiver getByIdAdmin(int id) throws SQLException {
        String sql = "SELECT * FROM caregiver WHERE caregiver_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCaregiver(rs);
            }
        }
        return null;
    }
    public boolean updateCaregiver(Caregiver c) throws SQLException {
        String sql =
            "UPDATE caregiver SET name=?, bio=?, qualifications=?, languages=?, " +
            "experience_years=?, hourly_rate=?, photo_path=?, status=? " +
            "WHERE caregiver_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getBio());
            ps.setString(3, c.getQualifications());
            ps.setString(4, c.getLanguages());
            ps.setInt(5, c.getExperienceYears());
            ps.setDouble(6, c.getHourlyRate());
            ps.setString(7, c.getPhotoPath());
            ps.setString(8, c.getStatus());
            ps.setInt(9, c.getCaregiverId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCaregiver(int caregiverId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {

            // delete relations first
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM caregiver_service WHERE caregiver_id=?")) {
                ps.setInt(1, caregiverId);
                ps.executeUpdate();
            }

            // delete caregiver
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM caregiver WHERE caregiver_id=?")) {
                ps.setInt(1, caregiverId);
                return ps.executeUpdate() > 0;
            }
        }
    }

}
