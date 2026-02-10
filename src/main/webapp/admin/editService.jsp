<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.Service" %>
<%@ page import="models.ServiceTimeSlot" %>
<%@ page import="servlets.admin.AdminBase" %>

<%
    if (!AdminBase.requireAdmin(request, response)) return;

    Service s = (Service) request.getAttribute("service");
    if (s == null) {
        String sid = request.getParameter("service_id");
        if (sid != null) {
            response.sendRedirect(request.getContextPath() + "/admin/services/edit?service_id=" + sid);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/services");
        }
        return;
    }

    List<ServiceTimeSlot> timeSlots = (List<ServiceTimeSlot>) request.getAttribute("timeSlots");
    if (timeSlots == null) timeSlots = new ArrayList<>();

    String updated = request.getParameter("updated");
    String err = request.getParameter("err");
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/adminEditService.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Edit Service</h2>

<div class="form-container">

  <% if ("1".equals(updated)) { %>
      <p style="color:green; margin-bottom:16px;">Updated successfully.</p>
  <% } %>

  <% if (err != null) {
        String msg = "Update failed.";
        if ("edit".equals(err)) msg = "Error: Unable to save changes.";
        else if ("slotAdd".equals(err)) msg = "Error: Please fill the required time slot fields.";
  %>
      <p style="color:red; margin-bottom:16px;"><%= msg %></p>
  <% } %>

  <form action="<%=request.getContextPath()%>/admin/services/edit" method="post" enctype="multipart/form-data">

    <label>Service Name</label>
    <input type="text" name="service_name" value="<%= s.getServiceName() %>" required>

    <label>Description</label>
    <textarea name="service_description" required><%= s.getServiceDescription() %></textarea>

    <label>Price</label>
    <input type="number" step="0.01" name="price" value="<%= s.getPrice() %>" required>

    <label>Duration</label>
    <input type="text" name="duration" value="<%= s.getDuration() %>" required>

    <label>Category ID</label>
    <input type="number" name="category_id"
           value="<%= s.getCategoryId() != null ? s.getCategoryId() : "" %>" required>

    <label>Current Image</label>
    <p style="margin-top:0;">
      <%= (s.getServiceImage() == null || s.getServiceImage().trim().isEmpty())
            ? "No image"
            : s.getServiceImage() %>
    </p>

    <label>Upload New Image (optional)</label>
    <input type="file" name="service_image_file" accept="image/*">

    <input type="hidden" name="existing_image"
           value="<%= s.getServiceImage() != null ? s.getServiceImage() : "" %>">

    <input type="hidden" name="service_id" value="<%= s.getServiceId() %>">

    <hr style="margin:22px 0;">

    <h3 style="margin:0 0 12px;">Time Slots</h3>

    <% if (timeSlots.isEmpty()) { %>
      <p style="color:#666;">No time slots yet.</p>
    <% } else { %>
      <% for (ServiceTimeSlot ts : timeSlots) { %>
        <div style="display:flex; gap:10px; align-items:center; margin-bottom:10px;">
          <input type="hidden" name="slot_id" value="<%= ts.getSlotId() %>">

          <!-- slot_time can be "6pm" OR "1pm-6pm" -->
          <input type="text" name="slot_time"
                 value="<%= ts.getSlotTime() != null ? ts.getSlotTime() : "" %>"
                 placeholder="e.g. 6pm  OR  1pm-6pm  OR  1-6"
                 style="flex:1;" required>

          <button type="submit"
                  name="delete_slot_id"
                  value="<%= ts.getSlotId() %>"
                  onclick="return confirm('Delete this time slot?');"
                  style="background:#c62828; color:#fff; border:none; padding:8px 12px; border-radius:10px;">
            Delete
          </button>
        </div>
      <% } %>
      <small style="color:#666;">Tip: You can type either a single time (6pm) or a range (1pm-6pm / 1-6).</small>
    <% } %>

    <!-- Add new slot: choose single or range -->
    <div style="margin-top:14px; padding:12px; border:1px solid #ddd; border-radius:12px;">
      <div style="display:flex; gap:14px; align-items:center; margin-bottom:10px;">
        <label style="display:flex; gap:6px; align-items:center;">
          <input type="radio" name="slot_type" value="single" checked>
          Single time
        </label>

        <label style="display:flex; gap:6px; align-items:center;">
          <input type="radio" name="slot_type" value="range">
          Range
        </label>
      </div>

      <div style="display:flex; gap:10px; align-items:center;">
        <input type="text" name="new_slot_start" placeholder="Start (e.g. 6pm / 1pm / 1)" style="flex:1;">
        <input type="text" name="new_slot_end" placeholder="End (only if range) (e.g. 6pm / 6)" style="flex:1;">
        <button type="submit" name="add_slot" value="1"
                style="background:#1f8b4c; color:#fff; border:none; padding:8px 12px; border-radius:10px;">
          Add Slot
        </button>
      </div>

      <small style="color:#666;">Single: fill Start only. Range: fill Start + End.</small>
    </div>

    <div style="margin-top:18px;">
      <button type="submit" class="btn-primary">Save Changes</button>
      <a class="btn-secondary"
         href="<%=request.getContextPath()%>/admin/services"
         style="margin-left:10px; text-decoration:none;">
        Back
      </a>
    </div>

  </form>

</div>

<%@ include file="../footer.jsp" %>
