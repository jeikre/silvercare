<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="models.User" %>

<%
    Integer prof_uid = servlets.util.SessionGuard.getUserId(request);
    if (prof_uid == null || !servlets.util.SessionGuard.isClient(request)) {
        response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
        return;
    }

    User u = (User) request.getAttribute("user");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/error.jsp");
        return;
    }
%>




<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/clientProfile.css?v=<%=System.currentTimeMillis()%>">

<div class="profile-container">
    <!-- Hero Header -->
    <div class="profile-hero">
        <div class="hero-content">
            <h1>My Profile</h1>
            <p>View and manage your account information</p>
        </div>
    </div>

    <!-- Profile Content -->
    <div class="profile-wrapper">
        <div class="profile-card">

            <!-- Full Name -->
            <div class="profile-info-item">
                <div class="info-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                </div>
                <div class="info-content">
                    <span class="info-label">Full Name</span>
                    <p class="info-value"><%= u.getName() %></p>
                </div>
            </div>

            <!-- Email -->
            <div class="profile-info-item">
                <div class="info-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="2" y="4" width="20" height="16" rx="2"></rect>
                        <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"></path>
                    </svg>
                </div>
                <div class="info-content">
                    <span class="info-label">Email Address</span>
                    <p class="info-value"><%= u.getEmail() %></p>
                </div>
            </div>

            <!-- Phone -->
            <div class="profile-info-item">
                <div class="info-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                    </svg>
                </div>
                <div class="info-content">
                    <span class="info-label">Phone Number</span>
                    <p class="info-value"><%= u.getPhone() == null ? "-" : u.getPhone() %></p>
                </div>
            </div>

            <!-- Address -->
            <div class="profile-info-item">
                <div class="info-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                        <circle cx="12" cy="10" r="3"></circle>
                    </svg>
                </div>
                <div class="info-content">
                    <span class="info-label">Address</span>
                    <p class="info-value"><%= u.getAddress() == null ? "-" : u.getAddress() %></p>
                </div>
            </div>

        </div>

        <!-- Done Button -->
        <div class="done-button-container">
            <a href="<%=request.getContextPath()%>/client/dashboard.jsp" class="btn-primary done-button">Done</a>
        </div>

    </div>
</div>

<%@ include file="../footer.jsp" %>
