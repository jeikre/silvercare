package servlets.admin;

import dbaccess.AdminDAO;
import models.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/clients/add")
public class AdminAddClientServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    // ✅ SHOW FORM
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        request.getRequestDispatcher("/admin/addClient.jsp").forward(request, response);
    }

    // ✅ SUBMIT FORM
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!AdminBase.requireAdmin(request, response)) return;
        request.setCharacterEncoding("UTF-8");

        try {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");

            if (name == null || name.trim().isEmpty()
                    || email == null || email.trim().isEmpty()
                    || password == null || password.trim().isEmpty()) {

                response.sendRedirect(request.getContextPath() + "/admin/clients/add?error=Missing required fields");
                return;
            }

            User u = new User();
            u.setName(name.trim());
            u.setEmail(email.trim());
            u.setPassword(password);
            u.setPhone(phone);
            u.setAddress(address);

            adminDAO.createClient(u);

            response.sendRedirect(request.getContextPath() + "/admin/clients?created=1");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/clients/add?error=Failed to create client");
        }
    }
}
