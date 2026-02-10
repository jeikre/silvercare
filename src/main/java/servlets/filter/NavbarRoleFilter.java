package servlets.filter;

import dbaccess.UserDAO;
import models.Role;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class NavbarRoleFilter implements Filter {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession(false);

        String navRole = null;

        if (session != null) {
            Object uidObj = session.getAttribute("userId");
            Integer userId = (uidObj instanceof Number) ? ((Number) uidObj).intValue() : null;

            if (userId != null) {
                try {
                    Role role = userDAO.getRoleByUserId(userId);
                    navRole = (role == null) ? null : role.name();  // ⭐ THIS FIXES NAVBAR
                } catch (Exception ignored) {}
            }
        }

        request.setAttribute("navRole", navRole);
        chain.doFilter(req, res);
    }
}



