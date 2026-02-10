package servlets.cart;

import dbaccess.CartDAO;
import servlets.util.SessionGuard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/cart/clear")
public class CartClearServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = SessionGuard.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            cartDAO.clearCart(userId);
        } catch (Exception ignore) {}

        response.sendRedirect(request.getContextPath() + "/cart/view?cleared=1");
    }
}
