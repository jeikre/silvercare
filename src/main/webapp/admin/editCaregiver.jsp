<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="models.Caregiver" %>
<%@ page import="servlets.admin.AdminBase" %>
<%
    if (!AdminBase.requireAdmin(request, response)) return;
    Caregiver c = (Caregiver) request.getAttribute("caregiver");
    if (c == null) {
        response.sendRedirect(request.getContextPath() + "/admin/caregivers?err=notfound");
        return;
    }
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/adminCaregiverForm.css?v=<%=System.currentTimeMillis()%>">

<h2 class="page-title">Edit Caregiver</h2>

<div class="form-container">
<form action="<%=request.getContextPath()%>/admin/caregivers/edit" method="post" enctype="multipart/form-data">

    <input type="hidden" name="caregiver_id" value="<%=c.getCaregiverId()%>">
    <input type="hidden" name="existing_photo" value="<%=c.getPhotoPath()%>">

    <label>Name</label>
    <input type="text" name="name" value="<%=c.getName()%>" required>

    <label>Bio</label>
    <textarea name="bio" rows="4"><%=c.getBio()==null?"":c.getBio()%></textarea>

    <label>Qualifications</label>
    <input type="text" name="qualifications" value="<%=c.getQualifications()==null?"":c.getQualifications()%>">

    <label>Languages (comma separated)</label>
    <input type="text" name="languages" value="<%=c.getLanguages()==null?"":c.getLanguages()%>">

    <label>Experience (Years)</label>
    <input type="number" name="experience_years" min="0" value="<%=c.getExperienceYears()%>" required>

    <label>Hourly Rate</label>
    <input type="number" step="0.01" name="hourly_rate" value="<%=c.getHourlyRate()%>" required>

    <label>Status</label>
    <select name="status" required>
        <option value="ACTIVE" <%= "ACTIVE".equalsIgnoreCase(c.getStatus()) ? "selected" : "" %>>ACTIVE</option>
        <option value="INACTIVE" <%= "INACTIVE".equalsIgnoreCase(c.getStatus()) ? "selected" : "" %>>INACTIVE</option>
    </select>

    <label>Photo (optional - upload to replace)</label>
    <input type="file" name="photo_file" accept="image/*">

    <div style="margin-top:10px;">
        <small>Current photo:</small><br>
        <img src="<%=request.getContextPath()%>/<%=c.getPhotoPath()%>" style="width:110px;height:110px;object-fit:cover;border-radius:12px;border:1px solid #ddd;">
    </div>

    <div style="margin-top:16px; display:flex; gap:10px;">
        <button type="submit" class="btn-primary">Save Changes</button>
        <a class="btn-secondary" href="<%=request.getContextPath()%>/admin/caregivers">Cancel</a>
    </div>
</form>
</div>

<%@ include file="../footer.jsp" %>
