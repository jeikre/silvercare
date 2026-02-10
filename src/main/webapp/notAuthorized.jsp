<%@ include file="header.jsp" %>
<%@ include file="navbar.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/errorPages.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Access Denied</h2>

<div class="form-container">
    <p>You do not have permission to view this page.</p>
    <a href="<%=request.getContextPath()%>/index.jsp" class="btn-primary">Go Back Home</a>
</div>

<%@ include file="footer.jsp" %>
