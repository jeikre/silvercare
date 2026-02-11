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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/services/details")
public class ServiceDetailsServlet extends HttpServlet {

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final CaregiverDAO caregiverDAO = new CaregiverDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) service_id
        int serviceId;
        try {
            serviceId = Integer.parseInt(request.getParameter("service_id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
            return;
        }

        // 2) keep category_id and q for back/returnTo
        String categoryIdStr = request.getParameter("category_id");
        if (categoryIdStr == null) categoryIdStr = "";
        categoryIdStr = categoryIdStr.trim();
        if ("null".equalsIgnoreCase(categoryIdStr)) categoryIdStr = "";

        String q = request.getParameter("q");
        if (q == null) q = "";
        q = q.trim();

        request.setAttribute("categoryId", categoryIdStr);
        request.setAttribute("q", q);

        // 3) selected booking date (used to compute full/remaining)
        String selectedDate = request.getParameter("booking_date");
        if (selectedDate == null || selectedDate.isBlank()) {
            selectedDate = LocalDate.now().plusDays(3).toString();
        }

        // (optional) store for JSP to highlight/select it
        request.setAttribute("selectedBookingDate", selectedDate);

        try {
            // 4) load service
            Service s = serviceDAO.getServiceWithCategoryName(serviceId);
            if (s == null) {
                response.sendRedirect(request.getContextPath() + "/error.jsp");
                return;
            }

            // 5) load category name
            String categoryName = serviceDAO.getCategoryNameByServiceId(serviceId);

            // 6) load time slots (and include availability date)
            List<TimeSlot> slots;
            try {
                // ✅ FIX: use selectedDate (NOT bookingDate)
                slots = serviceDAO.getTimeSlotsByServiceId(serviceId, selectedDate);
            } catch (Exception ex) {
                slots = new ArrayList<>();
            }

            // 7) load caregiver
            Caregiver caregiver = caregiverDAO.findByServiceId(serviceId);

            // 8) compute availability (capacity = 1 per slot per date)
            int capacityPerSlot = 1;
            try {
                Map<Integer, Integer> bookedMap = serviceDAO.getBookedCountBySlot(serviceId, selectedDate);

                for (TimeSlot t : slots) {
                    int booked = bookedMap.getOrDefault(t.getSlotId(), 0);
                    int remaining = Math.max(0, capacityPerSlot - booked);

                    t.setBookedCount(booked);
                    t.setRemaining(remaining);
                }
            } catch (Exception ignored) {
                // If availability query fails, we still show slots (without remaining/full logic)
            }

            // 9) set attributes
            request.setAttribute("service", s);
            request.setAttribute("categoryName", categoryName);
            request.setAttribute("slots", slots);
            request.setAttribute("caregiver", caregiver);

            request.getRequestDispatcher("/services/serviceDetails.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}
