package servlets.client;

import dbaccess.UserDAO;
import models.User;
import servlets.util.SessionGuard;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/client/profile")
public class ClientProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = SessionGuard.getUserId(request);
        if (userId == null || !SessionGuard.isClient(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        try {
            User u = userDAO.getUserById(userId);
            if (u == null) {
                response.sendRedirect(request.getContextPath() + "/error.jsp");
                return;
            }

            request.setAttribute("user", u);
            request.getRequestDispatcher("/client/profile.jsp").forward(request, response);

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}
