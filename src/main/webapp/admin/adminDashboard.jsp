<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    Integer adm_uid = servlets.util.SessionGuard.getUserId(request);
    if (adm_uid == null || !servlets.util.SessionGuard.isAdmin(request)) {
        response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
        return;
    }
%>



<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/adminDashboard.css?v=<%=System.currentTimeMillis()%>">

<div class="admin-hero">
    <div class="hero-content">
        <h1>Admin Dashboard</h1>
        <p>Manage services, clients, and key platform operations</p>
    </div>
</div>

<div class="admin-dashboard-wrapper">
    <div class="card-container">

        <div class="card">
            <div class="card-icon services-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M9 11l3 3L22 4"></path>
                    <path d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
            </div>
            <h3>Manage Services</h3>
            <p>View, Add, edit, or remove services offered.</p>
            <a href="<%=request.getContextPath()%>/admin/services" class="btn-primary">View Services</a>
        </div>

        <div class="card">
            <div class="card-icon add-service-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
            </div>
            <h3>Add New Service</h3>
            <p>Create a brand new service offering.</p>
            <a href="<%=request.getContextPath()%>/admin/services/add" class="btn-primary">Add Service</a>
        </div>

        <div class="card">
            <div class="card-icon clients-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                </svg>
            </div>
            <h3>Client List</h3>
            <p>View, Add, edit, or remove registered clients.</p>
            <a href="<%=request.getContextPath()%>/admin/clients" class="btn-primary">View Clients</a>
        </div>

<div class="card">
    <div class="card-icon caregivers-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24"
             fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
        </svg>
    </div>
    <h3>Manage Caregivers</h3>
    <p>View and add caregivers (profiles, rates, and availability).</p>
    <a href="<%=request.getContextPath()%>/admin/caregivers" class="btn-primary">View Caregivers</a>
</div>

    </div>
</div>

<%@ include file="../footer.jsp" %>
