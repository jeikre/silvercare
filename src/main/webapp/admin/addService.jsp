<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="models.ServiceCategory"%>

<%@ page import="servlets.admin.AdminBase"%>
<%
if (!AdminBase.requireAdmin(request, response))
	return;

Object obj = request.getAttribute("categories");
if (obj == null) {
	// open via controller so categories exist
	response.sendRedirect(request.getContextPath() + "/admin/services/add");
	return;
}
List<ServiceCategory> categories = (List<ServiceCategory>) obj;
%>

<%@ include file="../header.jsp"%>
<%@ include file="../navbar.jsp"%>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/adminAddService.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Add New Service</h2>

<div class="form-container">
	<form action="<%=request.getContextPath()%>/admin/services/add"
		method="post" enctype="multipart/form-data">

		<label>Category:</label> <select name="category_id" required>
			<%
			for (ServiceCategory c : categories) {
			%>
			<option value="<%=c.getCategoryId()%>"><%=c.getCategoryName()%></option>
			<%
			}
			%>
		</select> <input type="text" name="service_name" placeholder="Service Name"
			required>

		<textarea name="service_description" placeholder="Service Description"></textarea>

		<input type="number" step="0.01" name="price" placeholder="Price"
			required> <input type="text" name="duration"
			placeholder="Duration (e.g. 60 mins)" required>

		<!-- ✅ match servlet parameter name -->
		<label>Service Image</label> <input type="file"
			name="service_image_file" accept="image/*">
		<!-- ✅ Time Slots -->
		<label style="margin-top: 12px; display: block;">Time Slots
			(at least 1):</label>

		<div id="slotWrap">
			<div class="slot-row"
				style="display: flex; gap: 10px; margin: 8px 0;">
				<input type="text" name="slot_label"
					placeholder="e.g. 06:00 AM – 07:00 AM" style="flex: 2;"> <input
					type="time" name="slot_time" style="flex: 1;">
				<button type="button" onclick="removeSlot(this)"
					style="padding: 6px 10px;">X</button>
			</div>
		</div>


		<button type="button" onclick="addSlot()" style="margin: 6px 0 12px;">+
			Add another slot</button>

		<script>
  function addSlot() {
    const wrap = document.getElementById("slotWrap");
    const div = document.createElement("div");
    div.className = "slot-row";
    div.style.cssText = "display:flex; gap:10px; margin:8px 0;";

    div.innerHTML = `
      <input type="text" name="slot_label" placeholder="e.g. 07:00 AM – 08:00 AM" required style="flex:2;">
      <input type="time" name="slot_time" required style="flex:1;">
      <button type="button" onclick="removeSlot(this)" style="padding:6px 10px;">X</button>
    `;
    wrap.appendChild(div);
  }

  function removeSlot(btn) {
    const wrap = document.getElementById("slotWrap");
    if (wrap.querySelectorAll(".slot-row").length <= 1) return; // keep at least 1 row
    btn.closest(".slot-row").remove();
  }
</script>

		<button type="submit" class="btn-primary">Save</button>

	</form>
</div>

<%@ include file="../footer.jsp"%>
