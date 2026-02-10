package servlets.client;

import dbaccess.UserDAO;
import models.User;
import servlets.util.SessionGuard;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/client/profile/edit")
public class ClientEditProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Integer edit_uid = SessionGuard.getUserId(request);
        if (edit_uid == null || !SessionGuard.isClient(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        try {
            User u = userDAO.getUserById(edit_uid);
            if (u == null) {
                response.sendRedirect(request.getContextPath() + "/error.jsp");
                return;
            }

            request.setAttribute("user", u);
            request.getRequestDispatcher("/client/editProfile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer edit_uid = SessionGuard.getUserId(request);
        if (edit_uid == null || !SessionGuard.isClient(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        try {
            request.setCharacterEncoding("UTF-8");

            User u = new User();
            u.setUserId(edit_uid);
            u.setName(request.getParameter("name"));
            u.setPhone(request.getParameter("phone"));
            u.setAddress(request.getParameter("address"));

            userDAO.updateClientProfile(u);

            response.sendRedirect(request.getContextPath() + "/client/profile?updated=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/client/profile/edit?err=1");
        }
    }
}
