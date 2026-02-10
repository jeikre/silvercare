<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="servlets.util.SessionGuard" %>

<%
Integer userId = SessionGuard.getUserId(request);
if (userId == null || !SessionGuard.isClient(request)) {
    response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
    return;
}
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet"
      href="<%=request.getContextPath()%>/css/clientDeleteAccount.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Delete Account</h2>

<div class="form-container">
    <div class="delete-card">
        <p>This action cannot be undone. Are you sure?</p>

        <% if ("1".equals(request.getParameter("err"))) { %>
            <p style="color:red; font-weight:700;">Delete failed. Please try again.</p>
        <% } %>

        <!-- POST goes to SERVLET -->
        <form action="<%=request.getContextPath()%>/client/account/delete" method="post">
            <button type="submit" class="btn-danger"
                    onclick="return confirm('This will permanently delete your account. Continue?');">
                Delete My Account
            </button>
        </form>

        <div style="margin-top:12px;">
            <a class="btn-primary" href="<%=request.getContextPath()%>/client/dashboard.jsp">Cancel</a>
        </div>
    </div>
</div>

<%@ include file="../footer.jsp" %>
