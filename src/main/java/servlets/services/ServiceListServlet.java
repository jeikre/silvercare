package servlets.services;

import dbaccess.ServiceDAO;
import dbaccess.ServiceCategoryDAO;
import models.Service;
import models.ServiceCategory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/*@WebServlet("/services/list")
public class ServiceListServlet extends HttpServlet {

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ServiceCategoryDAO categoryDAO = new ServiceCategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String cat = request.getParameter("category_id");

            if (cat != null && !cat.trim().isEmpty()) {
                int categoryId = Integer.parseInt(cat);

                List<Service> services = serviceDAO.getServicesByCategory(categoryId);
                ServiceCategory category = categoryDAO.getCategoryById(categoryId);

                request.setAttribute("services", services);
                request.setAttribute("category", category);
                request.setAttribute("selectedCategoryId", categoryId);

            } else {
                List<Service> services = serviceDAO.getAllServices();
                request.setAttribute("services", services);
            }

        } catch (Exception e) {
            request.setAttribute("serviceError", "Unable to load services.");
        }

        request.getRequestDispatcher("/services/serviceList.jsp").forward(request, response);
    }

}*/

@WebServlet("/services/list")
public class ServiceListServlet extends HttpServlet {

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ServiceCategoryDAO categoryDAO = new ServiceCategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // read params
            String cat = request.getParameter("category_id");
            String q = request.getParameter("q");

            if (q != null) q = q.trim();
            boolean hasQ = (q != null && !q.isEmpty());

            Integer categoryId = null;
            if (cat != null) cat = cat.trim();

            // treat "null" as empty (prevents category_id=null bugs)
            if (cat != null && !cat.isEmpty() && !"null".equalsIgnoreCase(cat)) {
                categoryId = Integer.parseInt(cat);
            }

            // CASE 1: category + search
            if (categoryId != null && hasQ) {
                List<Service> services = serviceDAO.searchServicesInCategory(categoryId, q);
                ServiceCategory category = categoryDAO.getCategoryById(categoryId);

                request.setAttribute("services", services);
                request.setAttribute("category", category);
                request.setAttribute("selectedCategoryId", categoryId);
                request.setAttribute("q", q);
            }
            // CASE 2: category only
            else if (categoryId != null) {
                List<Service> services = serviceDAO.getServicesByCategory(categoryId);
                ServiceCategory category = categoryDAO.getCategoryById(categoryId);

                request.setAttribute("services", services);
                request.setAttribute("category", category);
                request.setAttribute("selectedCategoryId", categoryId);
            }
            // CASE 3: search only
            else if (hasQ) {
                List<Service> services = serviceDAO.searchServices(q);

                request.setAttribute("services", services);
                request.setAttribute("selectedCategoryId", null); // important: no "null" in URL
                request.setAttribute("q", q);
            }
            // CASE 4: all services
            else {
                List<Service> services = serviceDAO.getAllServices();
                request.setAttribute("services", services);
                request.setAttribute("selectedCategoryId", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("serviceError", "Unable to load services.");
        }

        request.getRequestDispatcher("/services/serviceList.jsp").forward(request, response);
    }
}
