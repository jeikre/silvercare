<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.math.BigDecimal" %>
<%@ page import="models.CartItem" %>

<%
List<CartItem> items = (List<CartItem>) request.getAttribute("cartItems");
BigDecimal grandTotal = (BigDecimal) request.getAttribute("grandTotal");
if (items == null) items = new ArrayList<>();
if (grandTotal == null) grandTotal = BigDecimal.ZERO;

// GST if you want (example 9%)
BigDecimal gstRate = new BigDecimal("0.09");
BigDecimal gst = grandTotal.multiply(gstRate).setScale(2, java.math.RoundingMode.HALF_UP);
BigDecimal totalWithGst = grandTotal.add(gst);
%>

<%@ include file="../header.jsp"%>
<%@ include file="../navbar.jsp"%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/checkout.css?v=<%=System.currentTimeMillis()%>">

<div class="checkout-page">
  <h1>Checkout</h1>

  <div class="checkout-card">
    <h2>Order Summary</h2>

    <div class="row">
      <span>Total items</span>
      <b><%= items.size() %></b>
    </div>

    <div class="row">
      <span>Subtotal</span>
      <b>$<%= String.format("%.2f", grandTotal.doubleValue()) %></b>
    </div>

    <div class="row">
      <span>GST (9%)</span>
      <b>$<%= String.format("%.2f", gst.doubleValue()) %></b>
    </div>

    <div class="row total">
      <span>Grand Total</span>
      <b>$<%= String.format("%.2f", totalWithGst.doubleValue()) %></b>
    </div>

    <p class="hint">Payment is processed securely via PayPal</p>

    <!-- PayPal button container -->
    <div id="paypal-button-container"></div>

    <a class="back" href="<%=request.getContextPath()%>/cart/view">← Back to Cart</a>
  </div>
</div>

<script>
  // fetch client id from server (recommended)
  // OR you can hardcode it in JSP temporarily (NOT recommended).
</script>

<!-- PayPal JS SDK (client-id must be from environment/config) -->
<script src="https://www.sandbox.paypal.com/sdk/js?client-id=<%= getServletContext().getInitParameter("PAYPAL_CLIENT_ID") %>&currency=SGD&intent=capture"></script>


<script>
let dbOrderId = null;

paypal.Buttons({
  createOrder: async () => {
    const res = await fetch("<%=request.getContextPath()%>/paypal/create-order", {
      method: "POST",
      headers: { "Content-Type": "application/json" }
    });

    const data = await res.json();
    dbOrderId = data.dbOrderId;          // ✅ save it
    return data.orderID;
  },

  onApprove: async (data) => {
    const res = await fetch("<%=request.getContextPath()%>/paypal/capture-order", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ orderID: data.orderID, dbOrderId: dbOrderId }) // ✅ send it
    });

    const result = await res.json();
    if (result.status === "COMPLETED") {
      window.location.href = "<%=request.getContextPath()%>/checkout/success";
    } else {
      alert("Payment not completed. Status: " + result.status);
    }
  }
}).render("#paypal-button-container");

</script>


<%@ include file="../footer.jsp"%>
