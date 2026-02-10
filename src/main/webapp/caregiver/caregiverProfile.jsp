<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="models.Caregiver"%>
<%@ page import="models.CaregiverServiceView"%>

<%@ include file="../header.jsp"%>
<%@ include file="../navbar.jsp"%>
<link rel="stylesheet"
	href="<%= request.getContextPath() %>/css/caregivers.css?v=<%= System.currentTimeMillis() %>">

<%
    Caregiver c = (Caregiver) request.getAttribute("caregiver");
    List<CaregiverServiceView> services = (List<CaregiverServiceView>) request.getAttribute("services");
%>

<div class="sc-wrap">

	<div class="sc-hero">
		<h1 class="sc-title"><%= c.getName() %></h1>
		<p class="sc-subtitle">Caregiver profile and the service(s) they
			provide.</p>

		<a class="sc-btn sc-btn-outline"
			href="<%= request.getContextPath() %>/caregivers"> ← Back to
			caregivers </a>
	</div>

	<div class="sc-profile">

		<div class="sc-profile-card">
			<img class="sc-profile-img"
				src="<%= request.getContextPath() %>/<%= c.getPhotoPath() %>"
				alt="Caregiver photo">

			<div class="sc-profile-body">
				<div class="sc-badges">
					<span class="sc-badge"><%= c.getExperienceYears() %> yrs exp</span>
					<span class="sc-badge">$<%= String.format("%.2f", c.getHourlyRate()) %>/hr
					</span> <span class="sc-badge"> <%= (c.getLanguages()==null || c.getLanguages().isBlank())
                                ? "Languages: -"
                                : ("Languages: " + c.getLanguages()) %>
					</span>
				</div>
			</div>
		</div>

		<div class="sc-section">
			<h2>About</h2>

			<h3>Qualifications</h3>
			<p><%= (c.getQualifications()==null || c.getQualifications().isBlank()) ? "-" : c.getQualifications() %></p>

			<h3>Bio</h3>
			<p><%= (c.getBio()==null || c.getBio().isBlank()) ? "-" : c.getBio() %></p>

			<h3>Services Provided</h3>
			<%
                if (services == null || services.isEmpty()) {
            %>
			<p>-</p>
			<%
                } else {
            %>
			<ul style="margin: 0; padding-left: 18px; line-height: 1.9;">
				<% for (CaregiverServiceView s : services) { %>
				<li><b><%= s.getCategoryName() %>:</b> <a
					href="<%= request.getContextPath() %>/services/details?service_id=<%= s.getServiceId() %>&category_id=<%= s.getCategoryId() %>">
						<%= s.getServiceName() %>
				</a></li>
				<% } %>
			</ul>
			<%
                }
            %>
		</div>

	</div>
</div>
