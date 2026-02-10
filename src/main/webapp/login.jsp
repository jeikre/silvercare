<%
    Object login_uidObj = session.getAttribute("userId");
    boolean login_alreadyLoggedIn = (login_uidObj != null);

    Object login_roleObj = request.getAttribute("navRole");
    String login_navRole = (login_roleObj == null) ? null : login_roleObj.toString();
    boolean login_isAdmin = "ADMIN".equalsIgnoreCase(login_navRole);

    String login_continueUrl = login_isAdmin
        ? request.getContextPath() + "/admin/adminDashboard.jsp"
        : request.getContextPath() + "/client/dashboard.jsp";
%>



<%@ include file="header.jsp" %>
<%@ include file="navbar.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/clientLogin.css?v=<%=System.currentTimeMillis()%>">

<div class="login-wrapper">
    <div class="login-left">
        <div class="left-content">
            <h1>Silver Care</h1>
            <h2>Welcome Back</h2>
            <p>Sign in to your account and continue your journey with us.</p>

            <div class="steps-container">
                <div class="step">
                    <div class="step-number">1</div>
                    <p>Enter your credentials</p>
                </div>
                <div class="step">
                    <div class="step-number">2</div>
                    <p>Access your dashboard</p>
                </div>
                <div class="step">
                    <div class="step-number">3</div>
                    <p>Manage your account</p>
                </div>
            </div>
        </div>
    </div>

    <div class="login-right">
        <div class="login-card">
            <h3>Sign In</h3>
            <p>Enter your credentials to continue</p>
            <% if (login_alreadyLoggedIn) { %>
  <div class="login-error" style="color:#0a7; text-align:center; margin-bottom:10px;">
    You are already logged in.
    <a href="<%= login_continueUrl %>">Continue</a>
  </div>
<% } %>
            <!-- ❗ Integrated error message (from second JSP) -->
            <%
                if (request.getParameter("error") != null) {
            %>
                <div class="login-error" style="color:red; text-align:center; margin-bottom:10px;">
                    You have entered invalid username or password.
                </div>
            <%
                }
            %>

            <form action="<%=request.getContextPath()%>/login" method="post">

    <div>
        <label for="identifier">Email</label>
        <input type="email" id="identifier" name="identifier" placeholder="Enter your email" required>

    </div>

    <div>
        <label for="password">Password</label>
        <input type="password" id="password" name="password"
               placeholder="Enter your password" required>
    </div>

    <button type="submit" class="btn-primary">Sign In</button>
    
</form>



            <div class="signup-link">
                Don't have an account? <a href="<%=request.getContextPath()%>/register.jsp">Create one</a>
            </div>
<div class="signup-link" style="margin-top: 6px;">

</div>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
