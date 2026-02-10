<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="models.Product" %>

<%@ include file="../header.jsp"%>
<%@ include file="../navbar.jsp"%>

<link rel="stylesheet"
      href="<%=request.getContextPath()%>/css/productDetails.css?v=<%=System.currentTimeMillis()%>">

<%
request.setCharacterEncoding("UTF-8");

// must come from servlet
Object pObj = request.getAttribute("product");
if (pObj == null) {
    String pid = request.getParameter("product_id");
    if (pid == null) {
        response.sendRedirect(request.getContextPath() + "/products");
    } else {
        response.sendRedirect(request.getContextPath() + "/products/details?product_id=" + pid);
    }
    return;
}

Product p = (Product) pObj;

String img = p.getImagePath();
if (img == null || img.trim().isEmpty()) img = "/images/default-product.jpg";
String image = request.getContextPath() + img;

// login check (NEW)
Object pd_uidObj = session.getAttribute("userId");
Integer pd_userId = (pd_uidObj instanceof Number) ? ((Number) pd_uidObj).intValue() : null;
boolean loggedIn = (pd_userId != null);

%>

<div class="product-details-container">

    <img src="<%=image%>" class="product-details-image">

    <div class="product-details-info">

        <h1><%=p.getProductName()%></h1>
        <div class="product-details-category"><%=p.getCategoryName()%></div>

        <p><%=p.getProductDescription()%></p>

        <div class="product-details-price">
            $<%=String.format("%.2f", p.getPrice().doubleValue())%>
        </div>

        <div class="qty-row">
            <div class="qty-box">
                <button type="button" class="qty-btn minus">−</button>
                <input type="text" class="qty-input" value="1" readonly>
                <button type="button" class="qty-btn plus">+</button>
            </div>

            <%
            if (!loggedIn) {
            %>
                <button type="button" class="add-cart-btn"
                    onclick="alert('Please login to add items to your cart.');
                             window.location.href='<%=request.getContextPath()%>/login.jsp';">
                    ADD TO CART
                </button>
            <%
            } else {
            %>

                <form method="post"
      action="<%=request.getContextPath()%>/cart/addToCart.jsp"
      onsubmit="document.getElementById('qty-hidden').value = document.querySelector('.qty-input').value;">

    <input type="hidden" name="product_id" value="<%=p.getProductId()%>">
    <input type="hidden" id="qty-hidden" name="quantity">

    <input type="hidden" name="returnTo"
           value="<%= request.getContextPath() + "/products/details?product_id=" + p.getProductId() %>">

    <button type="submit" class="add-cart-btn">ADD TO CART</button>
</form>


            <%
            }
            %>
        </div>

        <a href="<%=request.getContextPath()%>/products" class="back-btn">Back</a>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    const minus = document.querySelector(".minus");
    const plus = document.querySelector(".plus");
    const qty = document.querySelector(".qty-input");

    if (minus) minus.addEventListener("click", () => {
        let v = parseInt(qty.value);
        if (v > 1) qty.value = v - 1;
    });

    if (plus) plus.addEventListener("click", () => {
        qty.value = parseInt(qty.value) + 1;
    });
});
</script>

<%
if ("1".equals(request.getParameter("added"))) {
%>
<script>
window.onload = () => setTimeout(() => alert("Added to cart successfully."), 10);
</script>
<%
}
%>

<%@ include file="../footer.jsp"%>
