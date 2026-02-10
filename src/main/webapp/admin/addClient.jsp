<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="servlets.admin.AdminBase" %>
<%
    if (!AdminBase.requireAdmin(request, response)) return;
%>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/adminAddClient.css?v=<%= System.currentTimeMillis() %>">
<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<div class="form-container">
  <div class="form-card">

    <h2 class="form-title">Add New Client</h2>

    <%
        String errorMsg = request.getParameter("error");
        if (errorMsg != null) {
    %>
        <div class="error-message"><%= errorMsg %></div>
    <%
        }
    %>

    <form action="<%= request.getContextPath() %>/admin/clients/add" method="post" class="form-box">
      <div class="form-section">
        <h3 class="section-title">Client Details</h3>

        <div class="form-group">
          <label for="name">Name:</label>
          <input type="text" id="name" name="name" required>
        </div>

        <div class="form-group">
          <label for="email">Email:</label>
          <input type="email" id="email" name="email" required>
        </div>

        <div class="form-group">
          <label for="password">Password:</label>
          <input type="password" id="password" name="password" required>
        </div>

        <div class="form-group">
          <label for="phone">Phone:</label>
          <input type="text" id="phone" name="phone" required>
        </div>

        <div class="form-group">
          <label for="address">Address:</label>
          <input type="text" id="address" name="address" required>
        </div>
      </div>

      <div class="button-container">
        <a href="<%= request.getContextPath() %>/admin/clients" class="btn-secondary btn-link">Cancel</a>
        <button type="submit" class="btn-primary">Add Client</button>
      </div>
    </form>
  </div>
</div>

<%@ include file="../footer.jsp" %>
