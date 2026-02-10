package servlets.auth;

import dbaccess.UserDAO;
import models.Role;
import models.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");

        if (identifier == null || password == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
            return;
        }

        try {
            User u = userDAO.authenticate(identifier.trim(), password);

            if (u == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
                return;
            }

            HttpSession session = request.getSession(true);

            // ✅ store ONLY stable identity
            session.setAttribute("userId", u.getUserId());
            session.setAttribute("userName", u.getName());   // optional
            session.setAttribute("email", u.getEmail());      // optional

            // (Optional) keep your old checks if needed by old JSPs:
            // These are not role itself; but if teacher strict, remove these too.
            // session.setAttribute("adminUser", u.getEmail());
            // session.setAttribute("clientEmail", u.getEmail());

            // ✅ Redirect WITHOUT relying on session role
            // Use fresh role from 'u' (just authenticated from DB)
            if (u.getRole() == Role.ADMIN) {
                response.sendRedirect(request.getContextPath() + "/admin/adminDashboard.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/client/dashboard.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
        }
    }

}
