<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.math.BigDecimal" %>
<%@ page import="models.CartItem" %>

<%
request.setCharacterEncoding("UTF-8");

// login required (new session key)
Integer userId = (Integer) session.getAttribute("userId");
if (userId == null) {
    response.sendRedirect("../login.jsp");
    return;
}

// must come from controller
Object cartObj = request.getAttribute("cartItems");
if (cartObj == null) {
    response.sendRedirect(request.getContextPath() + "/cart/view");
    return;
}

List<CartItem> cartItems = (List<CartItem>) cartObj;
if (cartItems == null) cartItems = new ArrayList<>();

BigDecimal grandTotal = BigDecimal.ZERO;
boolean hasItems = !cartItems.isEmpty();
%>

<%@ include file="../header.jsp"%>
<%@ include file="../navbar.jsp"%>

<link rel="stylesheet"
      href="<%=request.getContextPath()%>/css/cart.css?v=<%=System.currentTimeMillis()%>">

<div class="cart-page">
    <h1>Your Cart</h1>

    <table class="cart-table">
        <thead>
            <tr>
                <th>Item</th>
                <th>Price (each)</th>
                <th>Quantity</th>
                <th>Booking Date</th>
                <th>Booking Time</th>
                <th>Line Total</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>

        <%
        if (!hasItems) {
        %>
            <tr>
                <td colspan="7" class="cart-empty">Your cart is currently empty.</td>
            </tr>
        <%
        } else {
            for (CartItem it : cartItems) {

                String name = (it.getItemName() == null) ? "-" : it.getItemName();
                BigDecimal price = (it.getUnitPrice() == null) ? BigDecimal.ZERO : it.getUnitPrice();
                int qty = it.getQuantity();

                boolean isProduct = (it.getProductId() != null);

                BigDecimal lineTotal = price.multiply(new BigDecimal(qty));
                grandTotal = grandTotal.add(lineTotal);

                String bookingDate = (it.getBookingDate() == null || it.getBookingDate().trim().isEmpty())
                        ? "-" : it.getBookingDate();

                String bookingTimeDisplay = (it.getBookingTimeDisplay() == null || it.getBookingTimeDisplay().trim().isEmpty())
                        ? "-" : it.getBookingTimeDisplay();
        %>

            <tr>
                <td><%=name%></td>
                <td>$<%=String.format("%.2f", price.doubleValue())%></td>

                <td>
                    <%
                    if (isProduct) {
                    %>
                        <form method="post" action="<%=request.getContextPath()%>/cart/update" class="inline-form">
    <input type="hidden" name="item_id" value="<%=it.getItemId()%>">
    <input type="number" name="quantity" value="<%=qty%>" min="1">
    <button type="submit">Update</button>
</form>

                    <%
                    } else {
                    %>
                        1
                    <%
                    }
                    %>
                </td>

                <td><%=bookingDate%></td>
                <td><%=bookingTimeDisplay%></td>

                <td>$<%=String.format("%.2f", lineTotal.doubleValue())%></td>

                <td>
                    <form method="post" action="<%=request.getContextPath()%>/cart/remove" class="inline-form">
    <input type="hidden" name="item_id" value="<%=it.getItemId()%>">
    <button type="submit">Remove</button>
</form>

                </td>
            </tr>

        <%
            }
        }
        %>

        </tbody>
    </table>

    <div class="cart-summary">
        <p class="cart-total">
            Grand Total: <span>$<%=String.format("%.2f", grandTotal.doubleValue())%></span>
        </p>

 <div class="cart-actions">
        <% if (hasItems) { %>
            <!-- ✅ Checkout goes to your checkout controller -->
            <a href="<%=request.getContextPath()%>/checkout" class="checkout-btn">
                Checkout
            </a>
        <% } %>

        <form method="post" action="<%=request.getContextPath()%>/cart/clear">
            <button type="submit" class="clear-cart-btn"
                    onclick="return confirm('Clear entire cart?');">
                Clear Cart
            </button>
        </form>
    </div>
</div>

<%
if ("1".equals(request.getParameter("updated"))) {
%>
<script>
window.onload = () => setTimeout(() => alert("Cart updated successfully."), 10);
</script>
<%
}
if ("1".equals(request.getParameter("removed"))) {
%>
<script>
window.onload = () => setTimeout(() => alert("Item removed successfully."), 10);
</script>
<%
}
%>
<%
if ("1".equals(request.getParameter("cleared"))) {
%>
<script>
window.onload = () => setTimeout(() => alert("Cart cleared successfully."), 10);
</script>
<%
}
%>


<%@ include file="../footer.jsp"%>
