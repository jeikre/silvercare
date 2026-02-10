package servlets.paypal;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletContext;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PayPalClient {

    private final HttpClient http = HttpClient.newHttpClient();
    private final ServletContext app;

    public PayPalClient(ServletContext app) {
        this.app = app;
    }

    private String accessToken() throws Exception {
        String base = PayPalConfig.baseUrl(app);
        String clientId = PayPalConfig.clientId(app);
        String secret = PayPalConfig.secret(app);

        String basic = Base64.getEncoder().encodeToString(
                (clientId + ":" + secret).getBytes(StandardCharsets.UTF_8)
        );

        String body = "grant_type=" + URLEncoder.encode("client_credentials", StandardCharsets.UTF_8);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + "/v1/oauth2/token"))
                .header("Authorization", "Basic " + basic)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException("PayPal token failed: " + res.statusCode() + " " + res.body());
        }

        try (JsonReader r = Json.createReader(new StringReader(res.body()))) {
            return r.readObject().getString("access_token");
        }
    }

    public String createOrder(BigDecimal total, String currency) throws Exception {
        String base = PayPalConfig.baseUrl(app);
        String token = accessToken();

        // PayPal expects 2dp string
        String value = total.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();

        JsonObject payload = Json.createObjectBuilder()
                .add("intent", "CAPTURE")
                .add("purchase_units", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("amount", Json.createObjectBuilder()
                                        .add("currency_code", currency)
                                        .add("value", value)
                                )
                        )
                )
                .build();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + "/v2/checkout/orders"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException("PayPal create order failed: " + res.statusCode() + " " + res.body());
        }

        try (JsonReader r = Json.createReader(new StringReader(res.body()))) {
            return r.readObject().getString("id"); // order id
        }
    }

    public JsonObject captureOrder(String orderId) throws Exception {
        String base = PayPalConfig.baseUrl(app);
        String token = accessToken();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + "/v2/checkout/orders/" + orderId + "/capture"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException("PayPal capture failed: " + res.statusCode() + " " + res.body());
        }

        try (JsonReader r = Json.createReader(new StringReader(res.body()))) {
            return r.readObject();
        }
    }
}
