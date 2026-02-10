<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.CaregiverServiceView" %>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/caregivers.css?v=<%= System.currentTimeMillis() %>">

<%
    List<CaregiverServiceView> rows = (List<CaregiverServiceView>) request.getAttribute("rows");
%>

<div class="sc-wrap">

    <div class="sc-hero">
        <h1 class="sc-title">Caregivers</h1>
        <p class="sc-subtitle">Caregivers are grouped by category and linked to services.</p>
    </div>

    <%
        if (rows == null || rows.isEmpty()) {
    %>
        <div class="sc-empty">No caregivers available right now.</div>
    <%
        } else {
            Integer currentCategoryId = null;

            for (int i = 0; i < rows.size(); i++) {
                CaregiverServiceView r = rows.get(i);

                if (currentCategoryId == null || currentCategoryId.intValue() != r.getCategoryId()) {
                    if (currentCategoryId != null) {
    %>
                        </div>
    <%
                    }
                    currentCategoryId = r.getCategoryId();
    %>
                    <h2 style="margin:24px 6px 10px; font-weight:900;">
                        <%= r.getCategoryName() %>
                    </h2>
                    <div class="sc-grid">
    <%
                }
    %>

                <div class="sc-card">
                    <img class="sc-img"
                         src="<%= request.getContextPath() %>/<%= r.getPhotoPath() %>"
                         alt="Caregiver photo">

                    <div class="sc-card-body">
                        <div class="sc-name"><%= r.getCaregiverName() %></div>

                        <div class="sc-meta">
                            <b>Service:</b>
                            <a href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=<%= r.getServiceId() %>&category_id=<%= r.getCategoryId() %>">
                                <%= r.getServiceName() %>
                            </a>
                        </div>

                        <div class="sc-meta"><b>Languages:</b> <%= (r.getLanguages()==null ? "-" : r.getLanguages()) %></div>
                        <div class="sc-meta"><b>Experience:</b> <%= r.getExperienceYears() %> years</div>
                        <div class="sc-price">$<%= String.format("%.2f", r.getHourlyRate()) %>/hr</div>

                        <div style="margin-top:12px;">
                            <a class="sc-btn sc-btn-outline"
                               href="<%= request.getContextPath() %>/caregiver?id=<%= r.getCaregiverId() %>">
                                View Profile →
                            </a>
                        </div>
                    </div>
                </div>

    <%
                if (i == rows.size() - 1) {
    %>
                    </div>
    <%
                }
            }
        }
    %>

</div>
