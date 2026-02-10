package servlets.admin;

import servlets.util.SessionGuard;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminBase {

    public static boolean requireAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = SessionGuard.getUserId(request);

        // ✅ not logged in
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/sessionTimeout.jsp");
            return false;
        }

        // ✅ logged in but wrong role
        if (!SessionGuard.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return false;
        }

        return true;
    }
}
