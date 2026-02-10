<%@ page import="servlets.admin.AdminBase" %>
<%
    if (!AdminBase.requireAdmin(request, response)) return;
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/adminDeleteService.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Delete Service</h2>

<div class="form-container">
    <div class="delete-card">

        <p>Are you sure you want to delete this service?</p>

        <form action="<%=request.getContextPath()%>/admin/services/delete" method="post">
            <input type="hidden" name="service_id" value="<%= request.getParameter("service_id") %>">
            <button type="submit" class="btn-primary">Yes, delete</button>
        </form>

        <a class="btn-secondary" href="<%=request.getContextPath()%>/admin/services">Cancel</a>

    </div>
</div>

<%@ include file="../footer.jsp" %>
