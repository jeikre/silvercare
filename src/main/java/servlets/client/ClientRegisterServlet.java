package servlets.client;

import dbaccess.UserDAO;
import models.Role;
import models.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/client/register")
public class ClientRegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        if (name == null || email == null || password == null ||
            name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/register.jsp?err=missing");
            return;
        }

        try {
            if (userDAO.emailExists(email.trim())) {
                response.sendRedirect(request.getContextPath() + "/register.jsp?err=email_exists");
                return;
            }

            User u = new User();
            u.setRole(Role.CLIENT);
            u.setName(name.trim());
            u.setEmail(email.trim());
            u.setPassword(password); // later: hash if needed
            u.setPhone(phone);
            u.setAddress(address);

            int newUserId = userDAO.registerClient(u);

            // ✅ set session after register
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", newUserId);
            session.setAttribute("role", "CLIENT");
            session.setAttribute("userName", u.getName());
            session.setAttribute("email", u.getEmail());

            response.sendRedirect(request.getContextPath() + "/client/profile");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/register.jsp?err=server");
        }
    }
}
