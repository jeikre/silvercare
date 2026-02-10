package servlets.cart;

import dbaccess.CartDAO;
import servlets.util.SessionGuard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/cart/update")
public class CartUpdateServlet extends HttpServlet {

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

        int itemId;
        int qty;

        try {
            itemId = Integer.parseInt(request.getParameter("item_id"));
            qty = Integer.parseInt(request.getParameter("quantity"));
            if (qty < 1) qty = 1;
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/cart/view?err=qty");
            return;
        }

        try {
            // ✅ IMPORTANT: your DAO must update by item_id AND user_id
            cartDAO.updateProductQuantity(userId, itemId, qty);

            response.sendRedirect(request.getContextPath() + "/cart/view?updated=1");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart/view?err=update");
        }
    }
}
