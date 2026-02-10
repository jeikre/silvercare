/*package servlets.services;

import dbaccess.ServiceDAO;
import models.Service;
import models.TimeSlot;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/services/details")
public class ServiceDetailsServlet extends HttpServlet {

    private final ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- read service_id ---
        int serviceId;
        try {
            serviceId = Integer.parseInt(request.getParameter("service_id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
            return;
        }

        // --- keep category_id and q for back/returnTo ---
        String categoryIdStr = request.getParameter("category_id");
        if (categoryIdStr == null) categoryIdStr = "";
        categoryIdStr = categoryIdStr.trim();
        if ("null".equalsIgnoreCase(categoryIdStr)) categoryIdStr = "";

        String q = request.getParameter("q");
        if (q == null) q = "";
        q = q.trim();

        request.setAttribute("categoryId", categoryIdStr);
        request.setAttribute("q", q);

        try {
            Service s = serviceDAO.getServiceWithCategoryName(serviceId);
            if (s == null) {
                response.sendRedirect(request.getContextPath() + "/error.jsp");
                return;
            }

            String categoryName = serviceDAO.getCategoryNameByServiceId(serviceId);
            List<TimeSlot> slots = serviceDAO.getTimeSlotsByServiceId(serviceId);
            String caregiverName = serviceDAO.getCaregiverNameForService(serviceId); // ✅ ADD
            request.setAttribute("service", s);
            request.setAttribute("categoryName", categoryName);
            request.setAttribute("slots", slots);
            request.setAttribute("caregiverName", caregiverName); 
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("serviceError", "Unable to load service details.");
        }

        request.getRequestDispatcher("/services/serviceDetails.jsp").forward(request, response);
    }
}
*/

package servlets.services;

import dbaccess.CaregiverDAO;
import dbaccess.ServiceDAO;
import models.Caregiver;
import models.Service;
import models.TimeSlot;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/services/details")
public class ServiceDetailsServlet extends HttpServlet {

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final CaregiverDAO caregiverDAO = new CaregiverDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- read service_id ---
        int serviceId;
        try {
            serviceId = Integer.parseInt(request.getParameter("service_id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
            return;
        }

        // --- keep category_id and q for back/returnTo ---
        String categoryIdStr = request.getParameter("category_id");
        if (categoryIdStr == null) categoryIdStr = "";
        categoryIdStr = categoryIdStr.trim();
        if ("null".equalsIgnoreCase(categoryIdStr)) categoryIdStr = "";

        String q = request.getParameter("q");
        if (q == null) q = "";
        q = q.trim();

        request.setAttribute("categoryId", categoryIdStr);
        request.setAttribute("q", q);

        try {
            Service s = serviceDAO.getServiceWithCategoryName(serviceId);
            if (s == null) {
                response.sendRedirect(request.getContextPath() + "/error.jsp");
                return;
            }

            String categoryName = serviceDAO.getCategoryNameByServiceId(serviceId);
            List<TimeSlot> slots = serviceDAO.getTimeSlotsByServiceId(serviceId);

            // ✅ INQUIRY: fetch FULL caregiver details for this service (via caregiver_service join)
            Caregiver caregiver = caregiverDAO.findByServiceId(serviceId);

            request.setAttribute("service", s);
            request.setAttribute("categoryName", categoryName);
            request.setAttribute("slots", slots);

            // Instead of only caregiverName, we pass the caregiver object
            request.setAttribute("caregiver", caregiver);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("serviceError", "Unable to load service details.");
        }

        request.getRequestDispatcher("/services/serviceDetails.jsp").forward(request, response);
    }
}
