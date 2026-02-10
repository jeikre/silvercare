package servlets.paypal;

import jakarta.servlet.ServletContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PayPalConfig {

    public static String baseUrl(ServletContext ctx) {
        String mode = ctx.getInitParameter("PAYPAL_MODE");
        if ("live".equalsIgnoreCase(mode)) return "https://api-m.paypal.com";
        return "https://api-m.sandbox.paypal.com";
    }

    public static String clientId(ServletContext ctx) {
        return ctx.getInitParameter("PAYPAL_CLIENT_ID");
    }

    public static String secret(ServletContext ctx) {
        return ctx.getInitParameter("PAYPAL_SECRET");
    }

    public static String getAccessToken(ServletContext ctx) throws IOException {

        // 🔥 Extra safety checks
        String cid = clientId(ctx);
        String sec = secret(ctx);

        if (cid == null || cid.isBlank() || sec == null || sec.isBlank()) {
            throw new IOException("Missing PAYPAL_CLIENT_ID or PAYPAL_SECRET in web.xml");
        }

        String basic = Base64.getEncoder().encodeToString(
                (cid + ":" + sec).getBytes(StandardCharsets.UTF_8)
        );

        URL url = new URL(baseUrl(ctx) + "/v1/oauth2/token");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Basic " + basic);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = con.getOutputStream()) {
            os.write("grant_type=client_credentials".getBytes(StandardCharsets.UTF_8));
        }

        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
        String json = readAll(is);

        if (code < 200 || code >= 300) {
            throw new IOException("PayPal token HTTP " + code + ": " + json);
        }

        String token = extractJsonString(json, "access_token");
        if (token == null) {
            throw new IOException("PayPal token parse error: " + json);
        }

        return token;
    }

    // 🔥 KEEP THIS METHOD HERE — this removes your error
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
}
