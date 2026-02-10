<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="models.User" %>

<%@ page import="servlets.admin.AdminBase" %>
<%
    if (!AdminBase.requireAdmin(request, response)) return;

    Object obj = request.getAttribute("client");
    if (obj == null) {
        String id = request.getParameter("user_id");
        if (id != null) {
            response.sendRedirect(request.getContextPath() + "/admin/clients/edit?user_id=" + id);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/clients");
        }
        return;
    }

    User u = (User) obj;
%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/adminEditClient.css?v=<%=System.currentTimeMillis()%>">

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<h2>Edit Client</h2>

<form action="<%=request.getContextPath()%>/admin/clients/edit" method="post" class="form-box">
    <input type="hidden" name="user_id" value="<%= u.getUserId() %>">

    Name: <input type="text" name="name" value="<%= u.getName() %>" required><br>
    Email: <input type="email" name="email" value="<%= u.getEmail() %>" required><br>
    Password: <input type="password" name="password" value="<%= u.getPassword() %>" required><br>
    Phone: <input type="text" name="phone" value="<%= u.getPhone() %>" required><br>
    Address: <input type="text" name="address" value="<%= u.getAddress() %>" required><br>

    <button type="submit" class="btn-primary">Update Client</button>
</form>

<%@ include file="../footer.jsp" %>
