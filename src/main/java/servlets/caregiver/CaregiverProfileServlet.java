package servlets.caregiver;

import dbaccess.CaregiverDAO;
import models.Caregiver;
import models.CaregiverServiceView;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/caregiver")
public class CaregiverProfileServlet extends HttpServlet {

    private final CaregiverDAO caregiverDAO = new CaregiverDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/caregivers");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            Caregiver c = caregiverDAO.findById(id);
            if (c == null) {
                response.sendError(404, "Caregiver not found");
                return;
            }

            List<CaregiverServiceView> services = caregiverDAO.findServicesByCaregiver(id);

            request.setAttribute("caregiver", c);
            request.setAttribute("services", services);

            request.getRequestDispatcher("/caregiver/caregiverProfile.jsp").forward(request, response);

        } catch (NumberFormatException ex) {
            response.sendError(400, "Invalid caregiver id");
        } catch (SQLException e) {
            throw new ServletException("DB error loading caregiver profile", e);
        }
    }
}
