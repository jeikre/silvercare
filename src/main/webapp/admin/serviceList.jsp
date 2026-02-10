<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.Service" %>

<%@ page import="servlets.admin.AdminBase" %>
<%
    if (!AdminBase.requireAdmin(request, response)) return;

    Object obj = request.getAttribute("services");
    if (obj == null) {
        response.sendRedirect(request.getContextPath() + "/admin/services");
        return;
    }

    List<Service> services = (List<Service>) obj;
%>


<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet"
      href="<%=request.getContextPath()%>/css/adminServiceList.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Service List (Admin)</h2>

<div class="card-container">

<%
    // Continue with your original service list logic
    if (services == null || services.isEmpty()) {
%>
        <p style="color:#666;">No services found.</p>
<%
    } else {
        for (Service s : services) {
%>


    <div class="card">
        <h3><%= s.getServiceName() %></h3>
        <p><strong>Price:</strong> $<%= String.format("%.2f", s.getPrice().doubleValue()) %></p>
        <p><strong>Duration:</strong> <%= s.getDuration() %></p>

        <!-- ✅ MVC: go to servlets -->
        <a href="<%=request.getContextPath()%>/admin/services/edit?service_id=<%= s.getServiceId() %>" class="btn-primary">Edit</a>

        <form method="post" action="<%=request.getContextPath()%>/admin/services/delete" style="display:inline;"
              onsubmit="return confirm('Delete this service?');">
            <input type="hidden" name="service_id" value="<%= s.getServiceId() %>">
            <button type="submit" class="btn-secondary">Delete</button>
        </form>
    </div>

<%
    }
}
%>

</div>
<script>
  const params = new URLSearchParams(window.location.search);
  const deleted = params.get("deleted");
  const err = params.get("err");

  if (deleted === "1") {
    alert("Service deleted successfully.");
  }

  if (err) {
    if (err === "missingId") alert("Error: Missing service ID.");
    else if (err === "notFound") alert("Error: Service not found.");
    else if (err === "delete") alert("Error: Cannot delete service because it is linked to caregiver_service / client_cart_items / service_time_slot.");
    else alert("Error occurred.");
  }

  // remove query params so refreshing won't popup again
  if (deleted || err) {
    window.history.replaceState({}, document.title, window.location.pathname);
  }
</script>

<%@ include file="../footer.jsp" %>
