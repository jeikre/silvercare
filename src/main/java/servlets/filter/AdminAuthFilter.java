package servlets.filter;

import dbaccess.UserDAO;
import models.Role;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        Integer userId = (session == null) ? null : (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            Role role = userDAO.getRoleByUserId(userId);
            if (role != Role.ADMIN) {
                response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
                return;
            }

            chain.doFilter(req, res);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
