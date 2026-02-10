<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String status = request.getParameter("status");
  if (status == null) status = "FAILED";
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/checkout.css?v=<%=System.currentTimeMillis()%>">

<div class="checkout-page">
  <div class="checkout-card">
    <h1>Payment Failed ❌</h1>
    <p>Status: <strong><%=status%></strong></p>

    <a class="primary-link" href="<%=request.getContextPath()%>/checkout">Try Again</a>
    <a class="back-link" href="<%=request.getContextPath()%>/cart/view">Back to Cart</a>
  </div>
</div>

<%@ include file="../footer.jsp" %>
