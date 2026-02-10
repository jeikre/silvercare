package servlets.paypal;

import dbaccess.CartDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import servlets.util.SessionGuard;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@WebServlet("/paypal/create-order")
public class PayPalCreateOrderServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer userId = SessionGuard.getUserId(request);
        if (userId == null) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"not_logged_in\"}");
            return;
        }

        try {
            BigDecimal subtotal = cartDAO.getCartTotalByUserId(userId);
            if (subtotal == null) subtotal = BigDecimal.ZERO;

            BigDecimal gst = subtotal.multiply(new BigDecimal("0.09"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            BigDecimal total = subtotal.add(gst)
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            // ✅ 1) Create DB order first
            int dbOrderId = cartDAO.createOrder(userId, "SGD", subtotal, gst, total);

            // ✅ 2) Create PayPal order
            String token = PayPalConfig.getAccessToken(getServletContext());

            String body = """
            {
              "intent": "CAPTURE",
              "purchase_units": [{
                "amount": {
                  "currency_code": "SGD",
                  "value": "%s"
                }
              }]
            }
            """.formatted(total.toPlainString());

            URL url = new URL(PayPalConfig.baseUrl(getServletContext()) + "/v2/checkout/orders");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + token);

            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int code = con.getResponseCode();
            InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
            String json = readAll(is);

            if (code < 200 || code >= 300) {
                response.setStatus(500);
                response.getWriter().write("{\"error\":\"paypal_create_failed\",\"details\":" + safeJson(json) + "}");
                return;
            }

            String paypalOrderId = extractJsonString(json, "id");
            if (paypalOrderId == null) {
                response.setStatus(500);
                response.getWriter().write("{\"error\":\"missing_id_from_paypal\",\"details\":" + safeJson(json) + "}");
                return;
            }

            // ✅ 3) Attach paypal order id to THIS db order
            cartDAO.attachPaypalOrderId(dbOrderId, paypalOrderId);

            // ✅ Return BOTH ids
            response.getWriter().write(
                    "{\"orderID\":\"" + paypalOrderId + "\",\"dbOrderId\":" + dbOrderId + "}"
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"server_exception\"}");
        }
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
        String needle = "\"" + key + "\":\"";
        int i = json.indexOf(needle);
        if (i < 0) return null;
        int start = i + needle.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return json.substring(start, end);
    }

    private static String safeJson(String s) {
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
}
