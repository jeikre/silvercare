<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.ServiceCategory" %>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/categories.css?v=<%= System.currentTimeMillis() %>">

<h2 class="page-title">Service Categories</h2>

<div class="card-container">
<%
Object obj = request.getAttribute("categories");
if (obj == null) {
    // if someone directly open this JSP, redirect to controller
    response.sendRedirect(request.getContextPath() + "/services/categories");
    return;
}
List<ServiceCategory> categories = (List<ServiceCategory>) obj;

if (categories == null || categories.isEmpty()) {
%>
    <p style="color:#666;">No categories found.</p>
<%
} else {
    for (ServiceCategory c : categories) {
        int id = c.getCategoryId();
        String name = c.getCategoryName();
        String desc = c.getCategoryDescription();
        String img = c.getCategoryImage();

        if (img == null || img.trim().equals("")) img = "/images/default-category.png";
%>
    <div class="card">
        <img src="<%= request.getContextPath() + img %>" class="service-img">
        <h3><%= name %></h3>
        <p><%= desc %></p>
        <a class="btn-primary" href="<%=request.getContextPath()%>/services/list?category_id=<%= id %>">View Services</a>
    </div>
<%
    }
}
%>
</div>

<%@ include file="../footer.jsp" %>
