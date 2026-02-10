package servlets.checkout;

import servlets.util.SessionGuard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/checkout/success")
public class CheckoutSuccessServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = SessionGuard.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/client/checkoutSuccess.jsp");
    }
}
