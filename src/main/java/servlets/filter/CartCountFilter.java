package servlets.filter;

import dbaccess.CartDAO;
import servlets.util.SessionGuard;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/*")
public class CartCountFilter implements Filter {

    private final CartDAO cartDAO = new CartDAO();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        String uri = request.getRequestURI();
        if (uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png")
                || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".gif")
                || uri.endsWith(".svg") || uri.endsWith(".woff") || uri.endsWith(".woff2")) {
            chain.doFilter(req, res);
            return;
        }

        Integer userId = SessionGuard.getUserId(request);

        int cartCount = 0;
        if (userId != null) {
            try {
                cartCount = cartDAO.getCartCount(userId);
            } catch (Exception ignored) {
                cartCount = 0;
            }
        }

        request.setAttribute("cartCount", cartCount);
        chain.doFilter(req, res);
    }
}
