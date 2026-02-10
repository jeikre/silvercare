package dbaccess;

import models.ServiceCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCategoryDAO {

	public List<ServiceCategory> getAllCategories() throws SQLException {
        String sql = "SELECT category_id, category_name, category_description, category_image FROM service_category ORDER BY category_name";

        List<ServiceCategory> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ServiceCategory c = new ServiceCategory();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setCategoryDescription(rs.getString("category_description"));
                c.setCategoryImage(rs.getString("category_image"));
                list.add(c);
            }
        }

        return list;
    }

    public ServiceCategory getCategoryById(int categoryId) throws SQLException {
        String sql =
            "SELECT category_id, category_name, category_image, category_description " +
            "FROM service_category WHERE category_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                ServiceCategory c = new ServiceCategory();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setCategoryImage(rs.getString("category_image"));
                c.setCategoryDescription(rs.getString("category_description"));
                return c;
            }
        }
    }
}
