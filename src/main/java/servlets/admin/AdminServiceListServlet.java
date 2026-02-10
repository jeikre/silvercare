package servlets.admin;

import dbaccess.ServiceDAO;
import models.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import servlets.util.SessionGuard;

@WebServlet("/admin/services")
public class AdminServiceListServlet extends HttpServlet {

    private final ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // admin auth
    	if (SessionGuard.getUserId(request) == null || !SessionGuard.isAdmin(request)) {
    	    response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
    	    return;
    	}

        try {
            List<Service> services = serviceDAO.getAllServices();
            request.setAttribute("services", services);
        } catch (Exception e) {
            request.setAttribute("serviceError", "Error loading services.");
        }

        request.getRequestDispatcher("/admin/serviceList.jsp").forward(request, response);
    }
}
