package servlets.cart;

import dbaccess.CartDAO;
import servlets.util.SessionGuard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/cart/addProduct")
public class CartAddProductServlet extends HttpServlet {

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

        int productId, qty;
        try {
            productId = Integer.parseInt(request.getParameter("product_id"));
            qty = Integer.parseInt(request.getParameter("quantity"));
            if (qty < 1) qty = 1;
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        String returnTo = request.getParameter("returnTo");
        if (returnTo == null || returnTo.isBlank()) {
            returnTo = request.getContextPath() + "/products/details?product_id=" + productId;
        }

        try {
            cartDAO.addProduct(userId, productId, qty);

            if (returnTo.contains("?")) response.sendRedirect(returnTo + "&added=1");
            else response.sendRedirect(returnTo + "?added=1");

        } catch (Exception e) {

            if (returnTo.contains("?")) response.sendRedirect(returnTo + "&err=add");
            else response.sendRedirect(returnTo + "?err=add");
        }
    }
}
