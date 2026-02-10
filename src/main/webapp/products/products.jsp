<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="models.Product" %>

<%
request.setCharacterEncoding("UTF-8");

// must come from servlet
Object listObj = request.getAttribute("products");
if (listObj == null) {
    response.sendRedirect(request.getContextPath() + "/products");
    return;
}

List<Product> products = (List<Product>) listObj;
Integer selectedCat = (Integer) request.getAttribute("selectedCat");
String sort = (String) request.getAttribute("sort");
if (sort == null) sort = "";
%>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/product.css?v=<%= System.currentTimeMillis() %>">

<div class="products-container">
    <!-- Hero Section -->
    <div class="products-hero">
        <div class="hero-content">
            <h1 class="hero-title">Products</h1>
            <p class="hero-subtitle">Premium care solutions designed with excellence in mind</p>
        </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
        <div class="filter-wrapper">
            <div class="category-filter">
                <h3 class="filter-label">Browse by Category</h3>
                <div class="category-buttons">
                    <a href="<%=request.getContextPath()%>/products?sort=<%=sort%>" class="category-btn <%= (selectedCat == null ? "active" : "") %>">
                        <span class="btn-icon">★</span>
                        All Products
                    </a>
                    <a href="<%=request.getContextPath()%>/products?category=1&sort=<%= sort %>" class="category-btn <%= (selectedCat != null && selectedCat == 1 ? "active" : "") %>">
                        <span class="btn-icon">👤</span>
                        In-Home Personal Care
                    </a>
                    <a href="<%=request.getContextPath()%>/products?category=2&sort=<%= sort %>" class="category-btn <%= (selectedCat != null && selectedCat == 2 ? "active" : "") %>">
                        <span class="btn-icon">🏠</span>
                        Daily Living & Home Support
                    </a>
                    <a href="<%=request.getContextPath()%>/products?category=3&sort=<%= sort %>" class="category-btn <%= (selectedCat != null && selectedCat == 3 ? "active" : "") %>">
                        <span class="btn-icon">🧠</span>
                        Dementia & Memory Care
                    </a>
                    <a href="<%=request.getContextPath()%>/products?category=4&sort=<%= sort %>" class="category-btn <%= (selectedCat != null && selectedCat == 4 ? "active" : "") %>">
                        <span class="btn-icon">❤️</span>
                        Health Monitoring & Wellness
                    </a>
                    <a href="<%=request.getContextPath()%>/products?category=5&sort=<%= sort %>" class="category-btn <%= (selectedCat != null && selectedCat == 5 ? "active" : "") %>">
                        <span class="btn-icon">🌙</span>
                        Respite & Overnight Care
                    </a>
                    <a href="<%=request.getContextPath()%>/products?category=6&sort=<%= sort %>" class="category-btn <%= (selectedCat != null && selectedCat == 6 ? "active" : "") %>">
                        <span class="btn-icon">🚗</span>
                        Transport & Community Support
                    </a>
                </div>
            </div>

            <div class="sort-filter">
                <form class="sort-form" method="get" action="<%=request.getContextPath()%>/products">
                    <% if (selectedCat != null) { %>
                        <input type="hidden" name="category" value="<%= selectedCat %>">
                    <% } %>
                    <label for="sort-select">Sort:</label>
                    <select id="sort-select" name="sort" onchange="this.form.submit()">
                        <option value="">Default</option>
                        <option value="name" <%= "name".equals(sort) ? "selected" : "" %>>Name (A-Z)</option>
                        <option value="price" <%= "price".equals(sort) ? "selected" : "" %>>Price (Low-High)</option>
                        <option value="category" <%= "category".equals(sort) ? "selected" : "" %>>Category</option>
                    </select>
                </form>
            </div>
        </div>
    </div>

    <!-- Products Grid -->
    <div class="products-grid">

<%
if (products == null || products.isEmpty()) {
%>
        <div class="no-products-message">
            <p>No products found in this category. Please try a different selection.</p>
        </div>
<%
} else {
    for (Product p : products) {
        String img = p.getImagePath();
        if (img == null || img.trim().isEmpty()) img = "/images/default-product.jpg";
        String fullImg = request.getContextPath() + img;
%>

        <div class="product-card">
            <div class="product-image-wrapper">
                <img src="<%= fullImg %>" class="product-image" alt="<%= p.getProductName() %>">
                <div class="product-overlay">
                    <a href="<%= request.getContextPath() %>/products/details?product_id=<%= p.getProductId() %>" class="explore-btn">Explore Product</a>
                </div>
            </div>

            <div class="product-details">
                <span class="product-category"><%= p.getCategoryName() %></span>
                <h3 class="product-title"><%= p.getProductName() %></h3>
                <p class="product-price"><%= String.format("$%.2f", p.getPrice().doubleValue()) %></p>
                <a href="<%= request.getContextPath() %>/products/details?product_id=<%= p.getProductId() %>" class="view-details">View Details →</a>
            </div>
        </div>

<%
    }
}
%>

    </div>
</div>

<%@ include file="../footer.jsp" %>
