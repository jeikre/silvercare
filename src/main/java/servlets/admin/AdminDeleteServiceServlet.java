package servlets.admin;

import dbaccess.AdminDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/services/delete")
public class AdminDeleteServiceServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        int serviceId;
        try {
            serviceId = Integer.parseInt(request.getParameter("service_id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/services?err=missingId");
            return;
        }

        try {
            boolean ok = adminDAO.deleteService(serviceId);

            if (ok) {
                response.sendRedirect(request.getContextPath() + "/admin/services?deleted=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/services?err=notFound");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/services?err=delete");
        }
    }
}

