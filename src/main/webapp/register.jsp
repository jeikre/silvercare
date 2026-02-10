<%@ include file="header.jsp" %>
<%@ include file="navbar.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/clientRegister.css?v=<%=System.currentTimeMillis()%>">

<div class="register-wrapper">
    <div class="register-left">
        <div class="left-content">
            <h1>Silver Care</h1>
            <h2>Get Started with Us</h2>
            <p>Complete these easy steps to register your account.</p>

            <div class="steps-container">
                <div class="step">
                    <div class="step-number">1</div>
                    <p>Sign up your account</p>
                </div>
                <div class="step">
                    <div class="step-number">2</div>
                    <p>Complete your profile</p>
                </div>
                <div class="step">
                    <div class="step-number">3</div>
                    <p>Start exploring</p>
                </div>
            </div>
        </div>
    </div>

    <div class="register-right">
        <div class="register-card">

            <h3>Create an Account</h3>
            <p>Join us today and get started</p>

            <!-- 🔴 Integrated error message from second JSP -->
            <% if (request.getAttribute("errorMsg") != null) { %>
                <div class="register-error"
                     style="color:red; text-align:center; margin-bottom:12px; font-weight:bold;">
                    <%= request.getAttribute("errorMsg") %>
                </div>
            <% } %>

            <form action="<%=request.getContextPath()%>/register" method="post">

                <div>
                    <label for="name">Full Name</label>
                    <input type="text" id="name" name="name" placeholder="Enter your full name" required>
                </div>

                <div>
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" placeholder="Enter your email" required>
                </div>

                <div>
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Create a strong password" required>
                </div>

                <div>
                    <label for="phone">Phone Number</label>
                    <input type="text" id="phone" name="phone" placeholder="Enter your phone number">
                </div>

                <div>
                    <label for="address">Address</label>
                    <textarea id="address" name="address" placeholder="Enter your address"></textarea>
                </div>

                <button type="submit" class="btn-primary">Register</button>
            </form>

            <div class="login-link">
                Already have an account? <a href="login.jsp">Sign in here</a>
            </div>

        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
