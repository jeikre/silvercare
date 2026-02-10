package servlets.admin;

import dbaccess.CaregiverDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/caregivers/delete")
public class AdminDeleteCaregiverServlet extends HttpServlet {

    private final CaregiverDAO caregiverDAO = new CaregiverDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer uid = servlets.util.SessionGuard.getUserId(request);
        if (uid == null || !servlets.util.SessionGuard.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        try {
            int caregiverId = Integer.parseInt(request.getParameter("id"));

            boolean ok = caregiverDAO.deleteCaregiver(caregiverId);

            if (ok) {
                response.sendRedirect(request.getContextPath() + "/admin/caregivers?deleted=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/caregivers?error=delete");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/caregivers?error=delete");
        }
    }
}
