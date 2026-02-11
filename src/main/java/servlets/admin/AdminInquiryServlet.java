package servlets.admin;

import dbaccess.AdminInquiryDAO;
import models.AdminInquiry.ServiceDemandRow;
import models.AdminInquiry.BookingRow;
import models.AdminInquiry.TopClientRow;
import models.AdminInquiry.ClientBookedServiceRow;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/inquiry")
public class AdminInquiryServlet extends HttpServlet {

    private final AdminInquiryDAO dao = new AdminInquiryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ admin guard
        if (!AdminBase.requireAdmin(request, response)) return;

        // ===================== BILLING FILTER PARAMS =====================
        String startStr = request.getParameter("start");
        String endStr   = request.getParameter("end");
        String monthStr = request.getParameter("month");
        String serviceIdStr = request.getParameter("serviceId");

        int topClientsN = 5;
        try {
            String tc = request.getParameter("topClients");
            if (tc != null && !tc.isBlank()) topClientsN = Integer.parseInt(tc);
        } catch (Exception ignored) {}

        List<BookingRow> bookings = new ArrayList<>();
        List<TopClientRow> topClients = new ArrayList<>();
        List<ClientBookedServiceRow> clientsByService = new ArrayList<>();

        // ---- 1) BOOKINGS by date range OR month ----
        try {
            if (startStr != null && !startStr.isBlank() && endStr != null && !endStr.isBlank()) {
                java.sql.Date start = java.sql.Date.valueOf(startStr);
                java.sql.Date end = java.sql.Date.valueOf(endStr);

                bookings = dao.getBookingsByDateRange(start, end);
                request.setAttribute("start", startStr);
                request.setAttribute("end", endStr);

            } else if (monthStr != null && !monthStr.isBlank()) {
                // monthStr format: YYYY-MM
                String[] parts = monthStr.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);

                bookings = dao.getBookingsByMonth(year, month);
                request.setAttribute("month", monthStr);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Invalid date/month input.");
        }

        // ---- 2) TOP CLIENTS ----
        try {
            topClients = dao.getTopClientsByValue(topClientsN);
            request.setAttribute("topClientsN", topClientsN);
        } catch (Exception ignored) {}

        // ---- 3) CLIENTS WHO BOOKED SERVICE ----
        try {
            if (serviceIdStr != null && !serviceIdStr.isBlank()) {
                int sid = Integer.parseInt(serviceIdStr);
                clientsByService = dao.getClientsWhoBookedService(sid);
                request.setAttribute("selectedServiceId", serviceIdStr);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Invalid Service ID.");
        }

        request.setAttribute("bookings", bookings);
        request.setAttribute("topClients", topClients);
        request.setAttribute("clientsByService", clientsByService);

        // ===================== DEMAND FILTER PARAMS =====================
        int topN = 5;
        int lowSlotsThreshold = 3;

        try {
            String t = request.getParameter("top");
            if (t != null && !t.isBlank()) topN = Integer.parseInt(t);

            String th = request.getParameter("slotsThreshold");
            if (th != null && !th.isBlank()) lowSlotsThreshold = Integer.parseInt(th);
        } catch (Exception ignored) {}

        // ===================== DEMAND TABLES =====================
        try {
            List<ServiceDemandRow> summary = dao.getServiceDemandSummary();
            List<ServiceDemandRow> top = dao.getTopDemanded(topN);
            List<ServiceDemandRow> low = dao.getLowestDemanded(topN);
            List<ServiceDemandRow> lowSlots = dao.getLowSlotServices(lowSlotsThreshold);

            request.setAttribute("summary", summary);
            request.setAttribute("top", top);
            request.setAttribute("low", low);
            request.setAttribute("lowSlots", lowSlots);

            request.setAttribute("topN", topN);
            request.setAttribute("slotsThreshold", lowSlotsThreshold);

        } catch (SQLException e) {
            throw new ServletException("DB error loading admin inquiry demand tables", e);
        }

        // ✅ finally show JSP
        request.getRequestDispatcher("/admin/adminInquiry.jsp").forward(request, response);
    }
}


