<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/checkout.css?v=<%=System.currentTimeMillis()%>">

<div class="checkout-page">
  <div class="checkout-card">
    <h1>Payment Successful ✅</h1>
    <p>Your payment is completed and your cart has been cleared.</p>

    <a class="primary-link" href="<%=request.getContextPath()%>/products">Continue Shopping</a>
    <a class="back-link" href="<%=request.getContextPath()%>/cart/view">View Cart</a>
  </div>
</div>

<%@ include file="../footer.jsp" %>
