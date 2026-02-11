package servlets.cart;

import dbaccess.CartDAO;
import dbaccess.ServiceDAO;
import models.TimeSlot;
import servlets.util.SessionGuard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/cart/addService")
public class CartAddServiceServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        Integer userId = SessionGuard.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int serviceId;
        int qty = 1;

        try {
            serviceId = Integer.parseInt(request.getParameter("service_id"));
            if (request.getParameter("quantity") != null) {
                qty = Integer.parseInt(request.getParameter("quantity"));
                if (qty < 1) qty = 1;
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/cart/view");
            return;
        }

        String bookingDate = request.getParameter("booking_date");
        String slotIdStr   = request.getParameter("slot_id");

        if (bookingDate == null || bookingDate.isBlank() || slotIdStr == null || slotIdStr.isBlank()) {
            redirectBack(request, response, serviceId, "missingBooking");
            return;
        }

        Integer slotId;
        try {
            slotId = Integer.parseInt(slotIdStr);
        } catch (Exception e) {
            redirectBack(request, response, serviceId, "badSlot");
            return;
        }

        // lookup slot details from DB
        String bookingTime = null;         // HH:mm:ss
        String bookingTimeDisplay = null;

        try {
            List<TimeSlot> slots = serviceDAO.getTimeSlotsByServiceId(serviceId, bookingDate);

            for (TimeSlot s : slots) {
                if (s.getSlotId() == slotId) {
                    bookingTime = s.getTimeValue();
                    bookingTimeDisplay = s.getDisplayLabel();
                    break;
                }
            }
        } catch (Exception ignored) {}

        if (bookingTime == null || bookingTime.isBlank()) {
            redirectBack(request, response, serviceId, "slotNotFound");
            return;
        }

        // ✅ BLOCK if FULL (capacity=1)
        try {
            Map<Integer, Integer> bookedMap = serviceDAO.getBookedCountBySlot(serviceId, bookingDate);
            int booked = bookedMap.getOrDefault(slotId, 0);
            if (booked >= 1) {
                redirectBack(request, response, serviceId, "slotFull");
                return;
            }
        } catch (Exception e) {
            redirectBack(request, response, serviceId, "slotCheckFail");
            return;
        }

        try {
            cartDAO.addServiceWithBooking(userId, serviceId, qty, bookingDate, bookingTime, bookingTimeDisplay, slotId);

            String returnTo = request.getParameter("returnTo");
            if (returnTo == null || returnTo.isBlank()) {
                returnTo = request.getContextPath() + "/services/details?service_id=" + serviceId;
            }

            response.sendRedirect(returnTo + (returnTo.contains("?") ? "&" : "?") + "added=1");

        } catch (Exception e) {
            e.printStackTrace();
            redirectBack(request, response, serviceId, "add");
        }
    }

    private void redirectBack(HttpServletRequest request, HttpServletResponse response, int serviceId, String err)
            throws IOException {

        String returnTo = request.getParameter("returnTo");
        if (returnTo == null || returnTo.isBlank()) {
            returnTo = request.getContextPath() + "/services/details?service_id=" + serviceId;
        }
        response.sendRedirect(returnTo + (returnTo.contains("?") ? "&" : "?") + "err=" + err);
    }
}
