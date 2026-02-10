package servlets.util;

import dbaccess.UserDAO;
import models.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionGuard {

    private static final UserDAO userDAO = new UserDAO();

    public static Integer getUserId(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        if (s == null) return null;

        Object v = s.getAttribute("userId");
        return (v instanceof Number) ? ((Number) v).intValue() : null;
    }

    public static String getRole(HttpServletRequest request) {
        Integer uid = getUserId(request);
        if (uid == null) return null;

        try {
            Role r = userDAO.getRoleByUserId(uid);  
            return (r == null) ? null : r.name();   // "ADMIN" or "CLIENT"
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isAdmin(HttpServletRequest request) {
        String role = getRole(request);
        return "ADMIN".equalsIgnoreCase(role);
    }

    public static boolean isClient(HttpServletRequest request) {
        String role = getRole(request);
        return "CLIENT".equalsIgnoreCase(role);
    }
}
