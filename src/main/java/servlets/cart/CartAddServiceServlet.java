package servlets.cart;

import dbaccess.CartDAO;
import servlets.util.SessionGuard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/cart/addService")
public class CartAddServiceServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();

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
        int qty = 1; // service default
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

        // ✅ booking fields from your service booking form
        String bookingDate = request.getParameter("booking_date"); // ex: 2026-02-06
        String bookingTime = request.getParameter("booking_time"); // ex: 14:00:00 or 14:00
        String bookingTimeDisplay = request.getParameter("booking_time_display"); // ex: 2:00 PM

        try {
        	cartDAO.addServiceWithBooking(userId, serviceId, qty, bookingDate, bookingTime, bookingTimeDisplay);

        	// Prefer redirect back to service details (stay on same page)
        	String returnTo = request.getParameter("returnTo");
        	if (returnTo == null || returnTo.isBlank()) {
        	    // fallback if not provided
        	    returnTo = request.getContextPath() + "/services/details?service_id=" + serviceId;
        	}

        	if (returnTo.contains("?")) {
        	    response.sendRedirect(returnTo + "&added=1");
        	} else {
        	    response.sendRedirect(returnTo + "?added=1");
        	}

        } catch (Exception e) {
        	String returnTo = request.getParameter("returnTo");
        	if (returnTo == null || returnTo.isBlank()) {
        	    returnTo = request.getContextPath() + "/services/details?service_id=" + serviceId;
        	}
        	if (returnTo.contains("?")) {
        	    response.sendRedirect(returnTo + "&err=add");
        	} else {
        	    response.sendRedirect(returnTo + "?err=add");
        	}

        }
    }
}
