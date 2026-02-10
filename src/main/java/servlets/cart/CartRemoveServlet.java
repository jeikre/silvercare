package servlets.cart;

import dbaccess.CartDAO;
import servlets.util.SessionGuard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/cart/remove")
public class CartRemoveServlet extends HttpServlet {

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
            int itemId = Integer.parseInt(request.getParameter("item_id"));

            // ✅ must remove only if belongs to this user
            cartDAO.removeItem(userId, itemId);

        } catch (Exception e) {
            // optional debug
            // e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/cart/view?removed=1");
    }
}
