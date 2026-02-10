/*package servlets.admin;

import dbaccess.AdminInquiryDAO;
import dbaccess.AdminInquiryDAO.ServiceDemandRow;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/inquiry")
public class AdminInquiryServlet extends HttpServlet {

    private final AdminInquiryDAO dao = new AdminInquiryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int topN = 5;
        int lowSlotsThreshold = 3; // services with <=3 slots considered low availability config

        // allow override by query params (optional)
        try {
            String t = request.getParameter("top");
            if (t != null && !t.isBlank()) topN = Integer.parseInt(t);

            String th = request.getParameter("slotsThreshold");
            if (th != null && !th.isBlank()) lowSlotsThreshold = Integer.parseInt(th);
        } catch (Exception ignored) {}

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

            request.getRequestDispatcher("/admin/adminInquiry.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("DB error loading admin inquiry", e);
        }
    }
}*/

package servlets.admin;

import dbaccess.AdminInquiryDAO;
import dbaccess.AdminInquiryDAO.ServiceDemandRow;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/inquiry")
public class AdminInquiryServlet extends HttpServlet {

    private final AdminInquiryDAO dao = new AdminInquiryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* ------------------------------------------------------------------
         *  ADMIN ACCESS CHECK
         *  navRole is set by NavbarRoleFilter for every request.
         * ------------------------------------------------------------------ */
        String navRole = (String) request.getAttribute("navRole");
        boolean isAdmin = navRole != null && navRole.equalsIgnoreCase("ADMIN");

        if (!isAdmin) {
            // redirect non-admin users
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        /* ------------------------------------------------------------------
         *  READ FILTER PARAMETERS
         * ------------------------------------------------------------------ */
        int topN = 5;
        int lowSlotsThreshold = 3;

        try {
            String t = request.getParameter("top");
            if (t != null && !t.isBlank()) {
                topN = Integer.parseInt(t);
            }

            String th = request.getParameter("slotsThreshold");
            if (th != null && !th.isBlank()) {
                lowSlotsThreshold = Integer.parseInt(th);
            }
        } catch (Exception ignored) {}

        /* ------------------------------------------------------------------
         *  LOAD INQUIRY DATA
         * ------------------------------------------------------------------ */
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

            /* Forward to JSP */
            request.getRequestDispatcher("/admin/adminInquiry.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("DB error loading admin inquiry", e);
        }
    }
}

