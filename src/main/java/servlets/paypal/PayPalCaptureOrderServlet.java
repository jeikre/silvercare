package servlets.paypal;

import dbaccess.CartDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import servlets.util.SessionGuard;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import dbaccess.OrderDAO;
import models.CartItem;
import java.util.List;

@WebServlet("/paypal/capture-order")
public class PayPalCaptureOrderServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // ✅ Must be logged in (so we can clear the correct user's cart)
        Integer userId = SessionGuard.getUserId(request);
        if (userId == null) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"not_logged_in\"}");
            return;
        }

        // ✅ Read request body JSON
        String raw = readAll(request.getInputStream());

        String paypalOrderId = extractJsonString(raw, "orderID");
        String dbOrderIdStr  = extractJsonString(raw, "dbOrderId");

        if (paypalOrderId == null || paypalOrderId.isBlank()) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"missing_orderID\"}");
            return;
        }

        if (dbOrderIdStr == null || dbOrderIdStr.isBlank()) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"missing_dbOrderId\"}");
            return;
        }

        int dbOrderId;
        try {
            dbOrderId = Integer.parseInt(dbOrderIdStr);
        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"invalid_dbOrderId\"}");
            return;
        }

        // ✅ Capture payment in PayPal
        String token = PayPalConfig.getAccessToken(getServletContext());

        URL url = new URL(PayPalConfig.baseUrl(getServletContext()) + "/v2/checkout/orders/" + paypalOrderId + "/capture");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + token);

        try (OutputStream os = con.getOutputStream()) {
            os.write("{}".getBytes(StandardCharsets.UTF_8));
        }

        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
        String paypalJson = readAll(is);

        if (code < 200 || code >= 300) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"paypal_capture_failed\",\"details\":" + safeJson(paypalJson) + "}");
            return;
        }

        // ✅ Extract status from PayPal response
        String status = extractJsonString(paypalJson, "status");
        if (status == null) status = "UNKNOWN";

        // ✅ If payment completed, update DB + clear cart
        if ("COMPLETED".equalsIgnoreCase(status)) {
            try {
                // 1) Read cart items BEFORE clearing cart
                List<CartItem> items = cartDAO.getCartItems(userId);

                // 2) Insert into order_items (only if not inserted yet)
                OrderDAO orderDAO = new OrderDAO();
                if (!orderDAO.hasAnyOrderItems(dbOrderId)) {
                    orderDAO.insertOrderItemsFromCart(dbOrderId, items);
                }

                // 3) Mark paid + clear cart
                cartDAO.markOrderPaid(dbOrderId, paypalOrderId);
                cartDAO.clearCart(userId);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(500);
                response.getWriter().write("{\"error\":\"db_update_failed\"}");
                return;
            }
        }


        // ✅ Return to frontend
        response.getWriter().write("{\"status\":\"" + status + "\"}");
    }

    private static String readAll(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static String extractJsonString(String json, String key) {
        String needle = "\"" + key + "\":";
        int i = json.indexOf(needle);
        if (i < 0) return null;

        int start = i + needle.length();

        // skip whitespace
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;

        // if it's quoted string
        if (start < json.length() && json.charAt(start) == '"') {
            start++;
            int end = json.indexOf("\"", start);
            if (end < 0) return null;
            return json.substring(start, end);
        }

        // else read number token (for dbOrderId if sent as number)
        int end = start;
        while (end < json.length() && "0123456789".indexOf(json.charAt(end)) >= 0) end++;
        return json.substring(start, end);
    }

    private static String safeJson(String s) {
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
}
