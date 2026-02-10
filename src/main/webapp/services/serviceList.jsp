<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.Service" %>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/categories.css?v=<%= System.currentTimeMillis() %>">

<%
Object obj = request.getAttribute("services");
if (obj == null) {
    // redirect to controller if opened directly
    String cid = request.getParameter("category_id");
    if (cid == null || cid.trim().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/error.jsp");
    } else {
        response.sendRedirect(request.getContextPath() + "/services/list?category_id=" + cid);
    }
    return;
}

List<Service> services = (List<Service>) obj;
Integer selectedCategoryId = (Integer) request.getAttribute("selectedCategoryId");
%>

<h2 class="page-title">Services</h2>

<div class="card-container">
<%
if (services == null || services.isEmpty()) {
%>
    <p style="color:#666;">No services found.</p>
<%
} else {
    for (Service s : services) {
        int sid = s.getServiceId();
        String name = s.getServiceName();
        String image = s.getServiceImage();
        String desc = s.getServiceDescription();
        String duration = s.getDuration();

        if (image == null) image = "";
        image = image.trim();

        // normalize: ensure it becomes "/images/xxx"
       // normalize: supports old /images and new /uploads
if (image.isEmpty()) {
    image = "/images/default-service.png";
} else if (!image.startsWith("/") && !image.startsWith("http://") && !image.startsWith("https://")) {

    // if DB stores "images/xxx" OR "uploads/services/xxx"
    if (image.startsWith("images/") || image.startsWith("uploads/")) {
        image = "/" + image;
    } else {
        // if DB stores just "xxx.png"
        image = "/images/" + image;
    }
}

%>
    <div class="card">
        <img src="<%= request.getContextPath() + image %>" class="service-img">
        <h3><%= name %></h3>
        <p><%= desc %></p>
        <p class="duration">Duration: <%= duration %></p>

        <a class="btn-primary"
   href="<%=request.getContextPath()%>/services/details?service_id=<%= sid %><%= (selectedCategoryId != null ? "&category_id=" + selectedCategoryId : "") %>">
   View Details
</a>

    </div>
<%
    }
}
%>
</div>

<%@ include file="../footer.jsp" %>
