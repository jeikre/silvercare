package servlets.caregiver;

import dbaccess.CaregiverDAO;
import models.CaregiverServiceView;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/caregivers")
public class CaregiverListServlet extends HttpServlet {

    private final CaregiverDAO caregiverDAO = new CaregiverDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<CaregiverServiceView> rows = caregiverDAO.findAllWithServiceAndCategory();
            request.setAttribute("rows", rows);
            request.getRequestDispatcher("/caregiver/caregivers.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("DB error loading caregivers", e);
        }
    }
}
