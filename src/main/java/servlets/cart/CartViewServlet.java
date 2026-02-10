package servlets.cart;

import dbaccess.CartDAO;
import models.CartItem;
import servlets.util.SessionGuard;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/cart/view")
public class CartViewServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = SessionGuard.getUserId(request);

        // Allow admin dummy browsing if you want (optional):
        // If you still want: admin uses dummy userId = 1001 or create a demo client.
        // Otherwise, keep strict login.
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<CartItem> items = cartDAO.getCartItems(userId);
            request.setAttribute("cartItems", items);
            request.setAttribute("cartCount", cartDAO.getCartCount(userId));
        } catch (Exception e) {
            request.setAttribute("cartError", "Unable to load cart.");
        }

        request.getRequestDispatcher("/cart/cart.jsp").forward(request, response);
    }
}
