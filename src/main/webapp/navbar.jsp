<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<link rel="stylesheet"
	href="<%= request.getContextPath() %>/css/home.css?v=<%= System.currentTimeMillis() %>">

<%
    // cart count
    Object cc = request.getAttribute("cartCount");
    int cartCount = (cc instanceof Number) ? ((Number) cc).intValue() : 0;

    // logged in?
    Object nav_uidObj = session.getAttribute("userId");
    Integer nav_userId = (nav_uidObj instanceof Number) ? ((Number) nav_uidObj).intValue() : null;
    boolean isLoggedIn = (nav_userId != null);

    // role from filter
    Object nav_r = request.getAttribute("navRole");
    String navRole = (nav_r == null) ? null : nav_r.toString();

    boolean isAdmin  = "ADMIN".equalsIgnoreCase(navRole);
    boolean isClient = "CLIENT".equalsIgnoreCase(navRole);

    // dashboard url
    String dashboardUrl = isAdmin
        ? request.getContextPath() + "/admin/adminDashboard.jsp"
        : request.getContextPath() + "/client/dashboard.jsp";
%>




<div class="navbar-wrapper">
	<nav class="navbar">
		<!-- LEFT SIDE: LOGO + TEXT -->
		<div class="nav-left">
			<a href="<%= request.getContextPath() %>/index.jsp"> <img
				src="<%= request.getContextPath() %>/images/logo.png"
				alt="Silver Care Logo" class="logo-img">
			</a> <span class="brand-name">Silver Care</span>
		</div>

		<!-- CENTER NAV LINKS -->
		<div class="nav-links">
			<a href="<%= request.getContextPath() %>/index.jsp">Home</a> <a
				href="<%= request.getContextPath() %>/caregivers">Caregivers</a>


			<!-- SERVICES DROPDOWN -->
			<div class="dropdown">
				<a href="<%= request.getContextPath() %>/services/categories.jsp"
					class="dropbtn">Services ▾</a>
				<div class="dropdown-content">

					<div class="dropdown-column">
						<a
							href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=1"
							class="category-title">In-Home Personal Care</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=1&category_id=1"
							class="service-link">Morning Personal Care Visit</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=2&category_id=1"
							class="service-link">Evening Personal Care Visit</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=3&category_id=1"
							class="service-link">Full Day Personal Care Support</a>
					</div>

					<div class="dropdown-column">
						<a
							href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=2"
							class="category-title">Daily Living & Home Support</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=4&category_id=2"
							class="service-link">Light Housekeeping & Laundry</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=5&category_id=2"
							class="service-link">Meal Preparation & Nutrition Support</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=6&category_id=2"
							class="service-link">Home Safety Check & Setup</a>
					</div>

					<div class="dropdown-column">
						<a
							href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=3"
							class="category-title">Dementia & Memory Care</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=7&category_id=3"
							class="service-link">Memory Care Companion Visit</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=8&category_id=3"
							class="service-link">Cognitive Stimulation Session</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=9&category_id=3"
							class="service-link">Behaviour Monitoring & Family Update</a>
					</div>

					<div class="dropdown-column">
						<a
							href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=4"
							class="category-title">Health Monitoring & Wellness</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=10&category_id=4"
							class="service-link">Vital Signs Monitoring Visit</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=11&category_id=4"
							class="service-link">Post Hospital Discharge Follow Up</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=12&category_id=4"
							class="service-link">Medication Reminder & Pillbox Setup</a>
					</div>

					<div class="dropdown-column">
						<a
							href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=5"
							class="category-title">Respite & Overnight Care</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=13&category_id=5"
							class="service-link">Half Day Respite Care</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=14&category_id=5"
							class="service-link">Overnight Home Care Stay</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=15&category_id=5"
							class="service-link">Weekend Respite Package</a>
					</div>

					<div class="dropdown-column">
						<a
							href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=6"
							class="category-title">Transport & Community Support</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=16&category_id=6"
							class="service-link">Medical Appointment Transport (Return)</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=17&category_id=6"
							class="service-link">Grocery & Errand Support</a> <a
							href="<%= request.getContextPath() %>/services/serviceDetails.jsp?service_id=18&category_id=6"
							class="service-link">Day Centre Drop Off & Pick Up</a>
					</div>

				</div>
			</div>

			<a href="<%= request.getContextPath() %>/products/products.jsp">Products</a>

			<%-- AUTH LINKS --%>
			<% if (isLoggedIn) { %>

			<% if (isAdmin) { %>
			<a href="<%= request.getContextPath() %>/admin/adminDashboard.jsp">Admin
				Dashboard</a> <a href="<%= request.getContextPath() %>/admin/inquiry">Service
				Inquiry</a>
			<% } else if (isClient) { %>
			<a href="<%= request.getContextPath() %>/client/dashboard.jsp">Dashboard</a>
			<% } %>

			<a href="<%= request.getContextPath() %>/logout">Logout</a>

			<% } else { %>

			<div class="dropdown">
				<a class="dropbtn">Profile ▾</a>
				<div class="small-profile-menu">
					<a href="<%= request.getContextPath() %>/register.jsp">Register</a>
					<a href="<%= request.getContextPath() %>/login.jsp">Login</a>
				</div>
			</div>
			<% } %>

		</div>

		<!-- CART BUTTON TOP-RIGHT -->
		<a href="<%= request.getContextPath() %>/cart/cart.jsp"
			class="cart-btn"> 🛒 Cart<% if (cartCount > 0) { %> (<%= cartCount %>)<% } %>
		</a>
	</nav>
</div>
