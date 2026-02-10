package servlets.admin;

import dbaccess.AdminDAO;
import models.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/clients/edit")
public class AdminEditClientServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    // ✅ GET: open edit page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        int userId;
        try {
            userId = Integer.parseInt(request.getParameter("user_id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/clients?err=missingId");
            return;
        }

        try {
            User u = adminDAO.getClientById(userId);
            if (u == null) {
                response.sendRedirect(request.getContextPath() + "/admin/clients?err=notFound");
                return;
            }

            request.setAttribute("client", u);

        } catch (Exception e) {
            request.setAttribute("editError", "Error loading client.");
        }

        request.getRequestDispatcher("/admin/editClient.jsp").forward(request, response);
    }

    // ✅ POST: submit edit form (replaces editClientProcess.jsp)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!AdminBase.requireAdmin(request, response)) return;
        request.setCharacterEncoding("UTF-8");

        try {
            int userId = Integer.parseInt(request.getParameter("user_id"));

            User u = new User();
            u.setUserId(userId);
            u.setName(request.getParameter("name"));
            u.setEmail(request.getParameter("email"));
            u.setPassword(request.getParameter("password"));
            u.setPhone(request.getParameter("phone"));
            u.setAddress(request.getParameter("address"));

            adminDAO.updateClient(u);

            response.sendRedirect(request.getContextPath() + "/admin/clients?updated=1");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/clients?err=edit");
        }
    }
}
