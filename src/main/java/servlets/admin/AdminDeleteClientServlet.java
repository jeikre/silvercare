package servlets.admin;

import dbaccess.AdminDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/clients/delete")
public class AdminDeleteClientServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        int userId;
        try {
            userId = Integer.parseInt(request.getParameter("user_id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/clients?err=missingId");
            return;
        }

        try {
            adminDAO.deleteClient(userId);
            response.sendRedirect(request.getContextPath() + "/admin/clients?deleted=1");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/clients?err=delete");
        }
    }
}
