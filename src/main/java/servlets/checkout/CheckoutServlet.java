package servlets.checkout;

import dbaccess.CartDAO;
import models.CartItem;
import servlets.util.SessionGuard;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = SessionGuard.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
        	List<CartItem> items = cartDAO.getCartItems(userId);
            if (items == null || items.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart/view?err=empty");
                return;
            }

            BigDecimal total = cartDAO.getCartTotalByUserId(userId);

            request.setAttribute("cartItems", items);
            request.setAttribute("grandTotal", total);

            request.getRequestDispatcher("/client/checkout.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart/view?err=checkout");
        }
    }
}
