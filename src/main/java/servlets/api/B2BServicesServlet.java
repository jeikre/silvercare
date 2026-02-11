package servlets.api;

import dbaccess.ServiceDAO;
import models.Service;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/b2b/services")
public class B2BServicesServlet extends HttpServlet {

    private final ServiceDAO dao = new ServiceDAO();
    private static final String API_KEY = "CHANGE_ME_SECRET";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1) API key check
        String key = req.getHeader("X-API-Key");
        if (key == null || !API_KEY.equals(key)) {
            resp.setStatus(401);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }

        // 2) validate categoryId
        String catStr = req.getParameter("categoryId");
        if (catStr == null || catStr.isBlank()) {
            resp.setStatus(400);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"categoryId is required\"}");
            return;
        }

        int categoryId;
        try {
            categoryId = Integer.parseInt(catStr.trim());
        } catch (Exception e) {
            resp.setStatus(400);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"categoryId must be a number\"}");
            return;
        }

        // 3) query existing DAO
        try {
            List<Service> services = dao.getServicesByCategory(categoryId);

            resp.setStatus(200);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(toJson(categoryId, services));

        } catch (Exception e) {
            resp.setStatus(500);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"server_error\"}");
        }
    }

    private String toJson(int categoryId, List<Service> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"categoryId\":").append(categoryId)
          .append(",\"count\":").append(list.size())
          .append(",\"services\":[");
        for (int i = 0; i < list.size(); i++) {
            Service s = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"serviceId\":").append(s.getServiceId()).append(",")
              .append("\"categoryId\":").append(s.getCategoryId() == null ? "null" : s.getCategoryId()).append(",")
              .append("\"serviceName\":").append(q(s.getServiceName())).append(",")
              .append("\"serviceDescription\":").append(q(s.getServiceDescription())).append(",")
              .append("\"price\":").append(s.getPrice() == null ? "null" : s.getPrice().toPlainString()).append(",")
              .append("\"duration\":").append(q(s.getDuration())).append(",")
              .append("\"serviceImage\":").append(q(s.getServiceImage())).append(",")
              .append("\"caregiverName\":").append(q(s.getCaregiverName()))
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
