<%@ include file="header.jsp" %>
<%@ include file="navbar.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/errorPages.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Error</h2>

<div class="form-container">
    <p>Something went wrong. Please try again later.</p>
</div>

<%@ include file="footer.jsp" %>
