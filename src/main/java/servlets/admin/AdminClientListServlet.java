package servlets.admin;

import dbaccess.AdminDAO;
import models.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/clients")
public class AdminClientListServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        try {
            List<User> clients = adminDAO.getAllClients();
            request.setAttribute("clients", clients);
        } catch (Exception e) {
            request.setAttribute("clientError", "Error loading client list.");
            request.setAttribute("clients", java.util.Collections.emptyList()); // ✅ prevents redirect loop
        }

        request.getRequestDispatcher("/admin/clientList.jsp").forward(request, response);
    }
}
