package servlets.products;

import dbaccess.ProductDAO;
import models.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class ProductListServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Integer selectedCat = null;
        String catParam = request.getParameter("category");
        if (catParam != null && !catParam.trim().isEmpty()) {
            try { selectedCat = Integer.parseInt(catParam); } catch (Exception ignore) {}
        }

        String sort = request.getParameter("sort");
        if (sort == null) sort = "";

        try {
            List<Product> products = productDAO.getProducts(selectedCat, sort);

            request.setAttribute("products", products);
            request.setAttribute("selectedCat", selectedCat);
            request.setAttribute("sort", sort);

        } catch (Exception e) {
            request.setAttribute("productError", "Error loading products.");
        }

        request.getRequestDispatcher("/products/products.jsp").forward(request, response);
    }
}
