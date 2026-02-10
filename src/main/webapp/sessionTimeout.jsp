<%@ include file="header.jsp" %>
<%@ include file="navbar.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/errorPages.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Session Expired</h2>

<div class="form-container">
    <p>Your session has expired. Please login again.</p>
    <a href="<%=request.getContextPath()%>/login.jsp" class="btn-primary">Login</a>
</div>

<%@ include file="footer.jsp" %>
