package servlets.admin;

import dbaccess.CaregiverDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Caregiver;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/caregivers")
public class AdminCaregiversServlet extends HttpServlet {

    private final CaregiverDAO caregiverDAO = new CaregiverDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer uid = servlets.util.SessionGuard.getUserId(request);
        if (uid == null || !servlets.util.SessionGuard.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        try {
            List<Caregiver> caregivers = caregiverDAO.getAllCaregivers();
            request.setAttribute("caregivers", caregivers);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to load caregivers.");
        }

        request.getRequestDispatcher("/admin/caregiverList.jsp").forward(request, response);
    }
}
