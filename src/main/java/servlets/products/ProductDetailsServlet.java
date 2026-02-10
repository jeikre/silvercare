package servlets.products;

import dbaccess.ProductDAO;
import models.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/products/details")
public class ProductDetailsServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int productId;
        try {
            productId = Integer.parseInt(request.getParameter("product_id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        try {
            Product p = productDAO.getProductById(productId);
            if (p == null) {
                response.sendRedirect(request.getContextPath() + "/error.jsp");
                return;
            }

            request.setAttribute("product", p);

        } catch (Exception e) {
            request.setAttribute("productError", "Unable to load product details.");
        }

        request.getRequestDispatcher("/products/productDetails.jsp").forward(request, response);
    }
}
