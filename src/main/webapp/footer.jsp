<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="java.util.*,java.sql.*" %>
         
 <link rel="stylesheet" href="<%= request.getContextPath() %>/css/home.css">

<footer>
    <div class="footer-content">
        <div class="footer-section">
            <div class="footer-logo">
                <img src="<%= request.getContextPath() %>/images/logo.png" alt="Silver Care Logo">
                <span>Silver Care</span>
            </div>
            <p>Providing compassionate, professional elderly care services with dignity and respect. Your loved ones deserve the best care.</p>
        </div>
        
        <div class="footer-section">
            <h3>Quick Links</h3>
            <ul>
                <li><a href="<%= request.getContextPath() %>/index.jsp">Home</a></li>
                <li><a href="<%= request.getContextPath() %>/services/serviceList.jsp">All Services</a></li>
                <li><a href="<%= request.getContextPath() %>/products.jsp">Products</a></li>
                <li><a href="<%= request.getContextPath() %>/about.jsp">About Us</a></li>
            </ul>
        </div>
        
        <div class="footer-section">
            <h3>Services</h3>
            <ul>
                <li><a href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=1">In-Home Personal Care</a></li>
                <li><a href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=2">Daily Living & Home Support</a></li>
                <li><a href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=3">Dementia & Memory Care</a></li>
                <li><a href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=4">Health Monitoring & Wellness</a></li>
                <li><a href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=5">Respite & Overnight Care</a></li>
                <li><a href="<%= request.getContextPath() %>/services/serviceList.jsp?category_id=6">Transport & Community Support</a></li>
            </ul>
        </div>
    </div>
    
    <div class="footer-bottom">
        <p>&copy; 2025 Silver Care SG. All Rights Reserved. | Providing compassionate care with dignity and respect.</p>
    </div>
</footer>
</body>
</html>
