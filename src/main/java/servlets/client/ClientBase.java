package servlets.client;

import servlets.util.SessionGuard;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClientBase {

    public static boolean requireClient(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = SessionGuard.getUserId(request);

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/sessionTimeout.jsp");
            return false;
        }

        if (!SessionGuard.isClient(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return false;
        }

        return true;
    }
}
