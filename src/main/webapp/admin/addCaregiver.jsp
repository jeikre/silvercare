<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.Service" %>

<%
    Integer uid = servlets.util.SessionGuard.getUserId(request);
    if (uid == null || !servlets.util.SessionGuard.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
        return;
    }

    String error = (String) request.getAttribute("error");
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/adminCaregivers.css?v=<%=System.currentTimeMillis()%>">

<div class="admin-hero admin-hero-sm">
  <div class="hero-content">
    <h1>Add Caregiver</h1>
    <p>Create a new caregiver profile.</p>
  </div>
</div>

<div class="adm-wrap">
  <div class="adm-topbar">
    <a class="btn-secondary" href="<%=request.getContextPath()%>/admin/caregivers">← Back</a>
  </div>

  <% if (error != null) { %>
    <div class="adm-alert error"><%= error %></div>
  <% } %>

  <div class="form-card">
    <form method="post"
      action="<%=request.getContextPath()%>/admin/caregivers/add"
      class="adm-form"
      enctype="multipart/form-data">

      
      <div class="row">
        <label>Name *</label>
        <input type="text" name="name" required placeholder="e.g., Alice Tan">
      </div>

      <div class="row">
        <label>Bio</label>
        <textarea name="bio" rows="3" placeholder="Short caregiver bio..."></textarea>
      </div>

      <div class="row">
        <label>Qualifications</label>
        <input type="text" name="qualifications" placeholder="e.g., WSQ Caregiving; CPR & AED">
      </div>

      <div class="row">
        <label>Languages</label>
        <input type="text" name="languages" placeholder="e.g., English, Chinese">
      </div>

      <div class="row two">
        <div>
          <label>Experience Years</label>
          <input type="number" name="experience_years" min="0" value="0">
        </div>
        <div>
          <label>Hourly Rate ($)</label>
          <input type="number" name="hourly_rate" min="0" step="0.01" value="0.00">
        </div>
      </div>

      <div class="row">
  <label>Photo Upload</label>
  <input type="file" name="photo" accept="image/*">
  <small class="hint">Upload JPG/PNG. If none, a default image will be used.</small>
</div>


      <div class="row">
        <label>Status</label>
        <select name="status">
          <option value="ACTIVE" selected>ACTIVE</option>
          <option value="INACTIVE">INACTIVE</option>
        </select>
      </div>
<div class="row">
        <label>Assign to Service *</label>
        <%
    List<Service> availableServices = (List<Service>) request.getAttribute("availableServices");
    if (availableServices == null) availableServices = new ArrayList<>();
%>
        
        <select name="service_id" required>
            <option value="">-- Select Service --</option>

            <% for (Service s : availableServices) { %>
                <option value="<%= s.getServiceId() %>">
                    <%= s.getServiceName() %>
                </option>
            <% } %>

            <% if (availableServices.isEmpty()) { %>
                <option disabled>No unassigned services available</option>
            <% } %>
        </select>
    </div>
      <button class="btn-primary" type="submit">Add Caregiver</button>
    </form>
  </div>
</div>

<%@ include file="../footer.jsp" %>
