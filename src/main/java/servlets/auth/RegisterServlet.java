package servlets.auth;

import dbaccess.UserDAO;
import models.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    // ✅ Show register page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    // ✅ Submit register form
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // basic validation
        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            request.setAttribute("errorMsg", "Please fill in all required fields.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            // duplicate email
            if (userDAO.emailExists(email.trim())) {
                request.setAttribute("errorMsg", "Email already exists. Please use another email.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // duplicate phone (only if provided)
            if (phone != null && !phone.trim().isEmpty()) {
                if (userDAO.phoneExists(phone.trim())) {
                    request.setAttribute("errorMsg", "Phone number already exists. Please use another number.");
                    request.getRequestDispatcher("/register.jsp").forward(request, response);
                    return;
                }
            }

            User u = new User();
            u.setName(name.trim());
            u.setEmail(email.trim());
            u.setPassword(password);     // DAO will hash
            u.setPhone(phone);
            u.setAddress(address);

            userDAO.registerClient(u);

            // redirect to login page
            response.sendRedirect(request.getContextPath() + "/login.jsp?registered=1");

        } catch (Exception e) {
            request.setAttribute("errorMsg", "Registration failed. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
