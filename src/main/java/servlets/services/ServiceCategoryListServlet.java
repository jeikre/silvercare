package servlets.services;

import dbaccess.ServiceCategoryDAO;
import models.ServiceCategory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/services/categories")
public class ServiceCategoryListServlet extends HttpServlet {

    private final ServiceCategoryDAO categoryDAO = new ServiceCategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<ServiceCategory> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
        } catch (Exception e) {
            request.setAttribute("serviceError", "Unable to load service categories.");
        }

        request.getRequestDispatcher("/services/categories.jsp").forward(request, response);
    }
}
