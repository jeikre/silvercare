<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.Service" %>
<%@ page import="models.TimeSlot" %>
<%@ page import="servlets.admin.AdminBase" %>

<%
    if (!AdminBase.requireAdmin(request, response)) return;

    Service s = (Service) request.getAttribute("service");
    List<TimeSlot> slots = (List<TimeSlot>) request.getAttribute("slots");
    if (slots == null) slots = new ArrayList<>();

    if (s == null) {
        response.sendRedirect(request.getContextPath() + "/admin/services?err=notFound");
        return;
    }

    String msg = "";
    if ("1".equals(request.getParameter("slotAdded"))) msg = "✅ Slot added!";
    if ("1".equals(request.getParameter("slotDeleted"))) msg = "✅ Slot deleted!";
    if ("1".equals(request.getParameter("slotErr"))) msg = "❌ Slot action failed (maybe duplicate or invalid).";
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/adminEditService.css?v=<%=System.currentTimeMillis()%>">

<div class="edit-service-page">

  <h2 class="page-title">Edit Service</h2>

  <% if (!msg.isEmpty()) { %>
    <div class="card" style="padding:12px 14px; margin: 0 0 16px; background:#f3f5ff;">
      <%= msg %>
    </div>
  <% } %>

  <div class="edit-grid">

    <!-- =========================
         LEFT: SERVICE DETAILS FORM
         ========================= -->
    <div class="card form-card">
      <div class="form-container">
        <form action="<%=request.getContextPath()%>/admin/services/edit" method="post" enctype="multipart/form-data">

          <input type="hidden" name="action" value="save">
          <input type="hidden" name="service_id" value="<%= s.getServiceId() %>">

          <label>Service Name</label>
          <input type="text" name="service_name" value="<%= s.getServiceName() %>" required>

          <label>Description</label>
          <textarea name="service_description" required><%= s.getServiceDescription() %></textarea>

          <label>Price</label>
          <input type="number" step="0.01" name="price" value="<%= s.getPrice() %>" required>

          <label>Duration</label>
          <input type="text" name="duration" value="<%= s.getDuration() %>" required>

          <label>Category ID</label>
          <input type="number" name="category_id" value="<%= s.getCategoryId() != null ? s.getCategoryId() : "" %>" required>

          <label>Current Image</label>
          <p style="margin-top:0; margin-bottom:14px; color: rgba(0,0,0,0.65); font-size: 13px;">
            <%= (s.getServiceImage() == null || s.getServiceImage().trim().isEmpty())
                  ? "No image"
                  : s.getServiceImage() %>
          </p>

          <label>Upload New Image (optional)</label>
          <input type="file" name="service_image_file" accept="image/*">

          <input type="hidden" name="existing_image"
                 value="<%= s.getServiceImage() != null ? s.getServiceImage() : "" %>">

          <button type="submit" class="btn-primary">Save Changes</button>
        </form>
      </div>
    </div>


    <!-- =========================
         MIDDLE: TIME SLOTS LIST
         ========================= -->
    <div class="card slots-panel">
      <h2>Time Slots</h2>

      <% if (slots.isEmpty()) { %>
        <p class="muted">No time slots yet.</p>
      <% } else { %>

        <div class="slots-list">
          <% for (TimeSlot t : slots) { %>
            <div class="slot-item">
              <div class="slot-left">
                <div class="slot-title"><%= t.getDisplayLabel() %></div>

                <div class="slot-meta">
                  slot_id: <%= t.getSlotId() %>
                  &nbsp;|&nbsp; time_value: <%= t.getTimeValue() %>
                  <% if (t.getStartTime() != null) { %>
                    &nbsp;|&nbsp; start: <%= t.getStartTime() %>
                  <% } %>
                  <% if (t.getEndTime() != null) { %>
                    &nbsp; end: <%= t.getEndTime() %>
                  <% } %>
                </div>
              </div>

              <form action="<%=request.getContextPath()%>/admin/services/edit" method="post"
                    style="margin:0;"
                    onsubmit="return confirm('Delete this slot?');">
                <input type="hidden" name="action" value="deleteSlot">
                <input type="hidden" name="service_id" value="<%= s.getServiceId() %>">
                <input type="hidden" name="slot_id" value="<%= t.getSlotId() %>">
                <button type="submit" class="btn-delete">Delete</button>
              </form>
            </div>
          <% } %>
        </div>

      <% } %>
    </div>


    <!-- =========================
         RIGHT: ADD SLOT (STICKY)
         ========================= -->
    <div class="card addslot-panel">
      <h2>Add Slot</h2>

      <form action="<%=request.getContextPath()%>/admin/services/edit" method="post">
        <input type="hidden" name="action" value="addSlot">
        <input type="hidden" name="service_id" value="<%= s.getServiceId() %>">

        <label>Slot Type</label>
        <select id="slot_mode" name="slot_mode">
          <option value="single" selected>Single time (e.g. 6:00 PM)</option>
          <option value="range">Range (e.g. 1:00 PM - 6:00 PM)</option>
        </select>

        <div id="single_box">
          <label style="margin-top: 4px;">Single Time</label>
          <input type="time" name="single_time">
          <button type="submit" class="btn-add">Add Slot</button>
        </div>

        <div id="range_box" style="display:none;">
          <label style="margin-top: 4px;">Start Time</label>
          <input type="time" name="start_time">

          <label>End Time</label>
          <input type="time" name="end_time">

          <button type="submit" class="btn-add">Add Slot</button>
        </div>

        <div class="addslot-tip">
          Tip: Single slot stores display_label like "06:00 PM". Range stores "01:00 PM - 06:00 PM".
        </div>
      </form>

      <a href="<%=request.getContextPath()%>/admin/services" class="back-btn" style="display:inline-block; margin-top:14px;">
        Back
      </a>
    </div>

  </div>
</div>

<script>
(function(){
  const mode = document.getElementById("slot_mode");
  const single = document.getElementById("single_box");
  const range = document.getElementById("range_box");

  function sync(){
    const v = (mode.value || "single");
    if (v === "range") {
      single.style.display = "none";
      range.style.display = "block";
    } else {
      range.style.display = "none";
      single.style.display = "block";
    }
  }
  mode.addEventListener("change", sync);
  sync();
})();
</script>

<%@ include file="../footer.jsp" %>
