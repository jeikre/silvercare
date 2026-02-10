<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.User" %>

<%@ page import="servlets.admin.AdminBase" %>
<%
    if (!AdminBase.requireAdmin(request, response)) return;

    Object obj = request.getAttribute("clients");
    List<User> clients = (obj instanceof List) ? (List<User>) obj : java.util.Collections.emptyList();

%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet"
      href="<%=request.getContextPath()%>/css/adminClientList.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Client List</h2>

<a href="<%=request.getContextPath()%>/admin/clients/add" class="btn-primary"
   style="padding:10px 20px; margin: 10px 0; display:inline-block;">
    + Add New Client
</a>

<table class="client-table">
    <thead>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Action</th>
        </tr>
    </thead>

    <tbody>
    <%
    String err = (String) request.getAttribute("clientError");
    if (err != null) {
    %>
        <tr><td colspan="5" style="color:red;"><%=err%></td></tr>
    <%
    } else if (clients == null || clients.isEmpty()) {
    %>
        <tr><td colspan="5" style="color:#666;">No clients found.</td></tr>
    <%
    } else {
        for (User u : clients) {
    %>
        <tr>
            <td><%= u.getName() %></td>
            <td><%= u.getEmail() %></td>
            <td><%= u.getPhone() %></td>
            <td><%= u.getAddress() %></td>
            <td>
                <a class="btn-edit"
                   href="<%=request.getContextPath()%>/admin/clients/edit?user_id=<%= u.getUserId() %>">Edit</a>

                <!-- keep confirm, but do DELETE as POST -->
                <form method="post"
      action="<%=request.getContextPath()%>/admin/clients/delete"
      style="display:inline;"
      onsubmit="return confirm('Delete this client?');">
    <input type="hidden" name="user_id" value="<%= u.getUserId() %>">
    <button type="submit" class="btn-delete">Delete</button>
</form>

            </td>
        </tr>
    <%
        }
    }
    %>
    </tbody>
</table>

<%@ include file="../footer.jsp" %>
