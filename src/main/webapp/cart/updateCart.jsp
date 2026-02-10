<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setCharacterEncoding("UTF-8");

Integer userId = (Integer) session.getAttribute("userId");
if (userId == null) {
    response.sendRedirect("../login.jsp");
    return;
}
%>
<jsp:forward page="/cart/update"/>
