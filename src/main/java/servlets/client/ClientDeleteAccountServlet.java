package servlets.client;

import dbaccess.UserDAO;
import servlets.util.SessionGuard;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/client/account/delete")
public class ClientDeleteAccountServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Integer userId = SessionGuard.getUserId(request);
        if (userId == null || !SessionGuard.isClient(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        request.getRequestDispatcher("/client/deleteAccount.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = SessionGuard.getUserId(request);
        if (userId == null || !SessionGuard.isClient(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        try {
            userDAO.deleteClientAccount(userId);

            // logout after delete
            HttpSession s = request.getSession(false);
            if (s != null) s.invalidate();

            response.sendRedirect(request.getContextPath() + "/index.jsp?deleted=1");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/client/deleteAccount.jsp?err=1");
        }
    }
}
