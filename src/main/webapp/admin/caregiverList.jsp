<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.Caregiver" %>

<%
    Integer uid = servlets.util.SessionGuard.getUserId(request);
    if (uid == null || !servlets.util.SessionGuard.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
        return;
    }

    List<Caregiver> caregivers = (List<Caregiver>) request.getAttribute("caregivers");
    if (caregivers == null) caregivers = new ArrayList<>();
    String error = (String) request.getAttribute("error");
    boolean added = "1".equals(request.getParameter("added"));
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/adminCaregivers.css?v=<%=System.currentTimeMillis()%>">

<div class="admin-hero admin-hero-sm">
  <div class="hero-content">
    <h1>Manage Caregivers</h1>
    <p>View caregiver profiles, rates, and status.</p>
  </div>
</div>

<div class="adm-wrap">

  <div class="adm-topbar">
    <a class="btn-primary" href="<%=request.getContextPath()%>/admin/caregivers/add">+ Add Caregiver</a>
    <a class="btn-secondary" href="<%=request.getContextPath()%>/admin/adminDashboard.jsp">Back to Dashboard</a>
    <div style="margin-top:12px; display:flex; gap:10px; flex-wrap:wrap;">
</div>
    
  </div>

  <% if (added) { %>
    <div class="adm-alert success">✅ Caregiver added successfully.</div>
  <% } %>

  <% if (error != null) { %>
    <div class="adm-alert error"><%= error %></div>
  <% } %>

  <div class="table-card">
    <table class="adm-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Photo</th>
          <th>Name</th>
          <th>Languages</th>
          <th>Exp (Years)</th>
          <th>Hourly Rate</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
      <% if (caregivers.isEmpty()) { %>
        <tr>
          <td colspan="7" class="empty">No caregivers found.</td>
        </tr>
      <% } else { 
           for (Caregiver c : caregivers) {
             String photo = c.getPhotoPath();
             if (photo == null || photo.trim().isEmpty()) photo = "images/default.png";
             if (!photo.startsWith("/") && !photo.startsWith("http")) photo = "/" + photo;
      %>
        <tr>
          <td><%= c.getCaregiverId() %></td>
          <td>
            <img class="cg-photo" src="<%=request.getContextPath() + photo%>" alt="photo">
          </td>
          <td class="cg-name">
            <%= c.getName() %>
            <div class="cg-sub"><%= c.getQualifications() == null ? "" : c.getQualifications() %></div>
          </td>
          <td><%= c.getLanguages() == null ? "" : c.getLanguages() %></td>
          <td><%= c.getExperienceYears() %></td>
          <td>$<%= c.getHourlyRate() <= 0 ? "0.00" : String.format("%.2f", c.getHourlyRate()) %></td>

          <td>
            <span class="badge <%= "ACTIVE".equalsIgnoreCase(c.getStatus()) ? "active" : "inactive" %>">
              <%= c.getStatus() == null ? "" : c.getStatus() %>
            </span>
          </td>
         <td>
  <a class="btn-sm btn-edit"
     href="<%=request.getContextPath()%>/admin/caregivers/edit?id=<%= c.getCaregiverId() %>">
    Edit ✎
  </a>

  <a class="btn-sm btn-delete"
     onclick="return confirmDelete(<%= c.getCaregiverId() %>)"
     href="#">
    Delete 🗑
  </a>
</td>

          
        </tr>
      <%   } 
         } %>
      </tbody>
    </table>
  </div>
</div>

<script>
function confirmDelete(id){
    if (confirm("Are you sure you want to delete this caregiver? This action cannot be undone.")) {
        window.location = "<%= request.getContextPath() %>/admin/caregivers/delete?id=" + id;
    }
    return false;
}
</script>


<%@ include file="../footer.jsp" %>
