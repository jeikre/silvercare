package servlets.api;

import dbaccess.ServiceCategoryDAO;
import models.ServiceCategory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/b2b/categories")
public class B2BCategoriesServlet extends HttpServlet {

    private final ServiceCategoryDAO dao = new ServiceCategoryDAO();

    // simple “B2B key” (for assignment)
    private static final String API_KEY = "CHANGE_ME_SECRET";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1) API key check (optional but good for B2B)
        String key = req.getHeader("X-API-Key");
        if (key == null || !API_KEY.equals(key)) {
            resp.setStatus(401);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }

        try {
            List<ServiceCategory> cats = dao.getAllCategories();

            resp.setStatus(200);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(toJson(cats));

        } catch (Exception e) {
            resp.setStatus(500);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"server_error\"}");
        }
    }

    private String toJson(List<ServiceCategory> cats) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"count\":").append(cats.size()).append(",\"categories\":[");
        for (int i = 0; i < cats.size(); i++) {
            ServiceCategory c = cats.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"categoryId\":").append(c.getCategoryId()).append(",")
              .append("\"categoryName\":").append(q(c.getCategoryName())).append(",")
              .append("\"categoryDescription\":").append(q(c.getCategoryDescription())).append(",")
              .append("\"categoryImage\":").append(q(c.getCategoryImage()))
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String q(String x) {
        if (x == null) return "null";
        String e = x.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
        return "\"" + e + "\"";
    }
}
