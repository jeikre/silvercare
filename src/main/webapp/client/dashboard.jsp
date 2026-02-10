<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    Integer uid = servlets.util.SessionGuard.getUserId(request);
    if (uid == null || !servlets.util.SessionGuard.isClient(request)) {
        response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
        return;
    }
%>


<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/clientDashboard.css?v=<%=System.currentTimeMillis()%>">

<div class="dashboard-main">
    <!-- Hero Header Section -->
    <div class="dashboard-header">
        <div class="header-content">
            <h1 class="page-title">Welcome back</h1>
            <p class="header-subtitle">Manage your account, profile, and preferences</p>
        </div>
    </div>

    <!-- Quick Stats Section -->
    <div class="dashboard-content">
        <div class="stats-section">
            <!-- Completely redesigned stat cards with modern icons and better styling -->
            <div class="stat-card status-card">
                <div class="stat-icon status-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z"></svg>
                </div>
                <div class="stat-info">
                    <h4>Account Status</h4>
                    <p class="stat-value">Active</p>
                </div>
            </div>
            <div class="stat-card email-card">
                <div class="stat-icon email-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="2" y="4" width="20" height="16" rx="2"></rect>
                        <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"></path>
                    </svg>
                </div>
                <div class="stat-info">
                    <h4>Email</h4>
                    <p class="stat-value"><%= session.getAttribute("email") %></p>
                </div>
            </div>
        </div>

        <!-- Main Actions Grid -->
        <div class="actions-section">
            <h2 class="section-title">Account Management</h2>
            <div class="card-container">
                <!-- My Profile Card -->
                <div class="card">
                    <h3>My Profile</h3>
                    <p>View your complete profile information and account details</p>
                    <a href="<%=request.getContextPath()%>/client/profile" class="btn-primary">View Profile</a>
                </div>

                <!-- Edit Profile Card -->
                <div class="card">
                    <h3>Edit Profile</h3>
                    <p>Update your personal information and preferences</p>
                    <a href="<%=request.getContextPath()%>/client/profile/edit" class="btn-primary">Edit Profile</a>
                </div>

                <!-- Delete Account Card -->
                <div class="card danger-card">
                    <h3>Delete Account</h3>
                    <p>Permanently delete your account and all associated data</p>
                    <a href="<%=request.getContextPath()%>/client/account/delete" class="btn-secondary">Delete Account</a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../footer.jsp" %>
