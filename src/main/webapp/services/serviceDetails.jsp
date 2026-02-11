<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.*"%>
<%@ page import="java.util.*"%>
<%@ page import="models.Service"%>
<%@ page import="models.TimeSlot"%>
<%@ page import="models.Caregiver"%>

<%@ include file="../header.jsp"%>
<%@ include file="../navbar.jsp"%>

<link rel="stylesheet"
    href="<%=request.getContextPath()%>/css/serviceDetails.css?v=<%=System.currentTimeMillis()%>">

<%
request.setCharacterEncoding("UTF-8");

Object sObj = request.getAttribute("service");
Object slotsObj = request.getAttribute("slots");

if (sObj == null) {
    response.sendRedirect(request.getContextPath() + "/error.jsp");
    return;
}

Service service = (Service) sObj;

List<TimeSlot> slots;
if (slotsObj instanceof List) slots = (List<TimeSlot>) slotsObj;
else slots = new ArrayList<>();

String categoryName = (String) request.getAttribute("categoryName");
if (categoryName == null) categoryName = "";

String categoryId = (String) request.getAttribute("categoryId");
if (categoryId == null) categoryId = request.getParameter("category_id");
if (categoryId == null) categoryId = "";
categoryId = categoryId.trim();
if ("null".equalsIgnoreCase(categoryId)) categoryId = "";

String q = (String) request.getAttribute("q");
if (q == null) q = request.getParameter("q");
if (q == null) q = "";
q = q.trim();

// selected booking date (keeps selection when page reloads)
String selectedBookingDate = (String) request.getAttribute("selectedBookingDate");
if (selectedBookingDate == null || selectedBookingDate.isBlank()) {
    selectedBookingDate = request.getParameter("booking_date");
}
if (selectedBookingDate == null || selectedBookingDate.isBlank()) {
    selectedBookingDate = LocalDate.now().plusDays(3).toString();
}

// normalize service image path
String image = service.getServiceImage();
if (image == null) image = "";
image = image.trim();

if (image.isEmpty()) {
    image = "/images/default-service.png";
} else if (!image.startsWith("/") && !image.startsWith("http://") && !image.startsWith("https://")) {
    if (image.startsWith("images/") || image.startsWith("uploads/")) image = "/" + image;
    else image = "/images/" + image;
}

// login check
Object sd_uidObj = session.getAttribute("userId");
Integer sd_userId = (sd_uidObj instanceof Number) ? ((Number) sd_uidObj).intValue() : null;
boolean loggedIn = (sd_userId != null);

// returnTo (always keep booking_date)
String returnTo = request.getContextPath() + "/services/details?service_id=" + service.getServiceId();
if (!categoryId.isEmpty()) returnTo += "&category_id=" + java.net.URLEncoder.encode(categoryId, "UTF-8");
if (!q.isEmpty()) returnTo += "&q=" + java.net.URLEncoder.encode(q, "UTF-8");
returnTo += "&booking_date=" + java.net.URLEncoder.encode(selectedBookingDate, "UTF-8");

// back url
String backUrl;
if (!q.isEmpty()) backUrl = request.getContextPath() + "/services/list?q=" + java.net.URLEncoder.encode(q, "UTF-8");
else if (!categoryId.isEmpty()) backUrl = request.getContextPath() + "/services/list?category_id=" + java.net.URLEncoder.encode(categoryId, "UTF-8");
else backUrl = request.getContextPath() + "/services/list";

Caregiver cg = (Caregiver) request.getAttribute("caregiver");
%>

<div class="service-details-container">

    <div class="details-image-box">
        <img src="<%=request.getContextPath() + image%>" class="details-image" alt="Service Image">
    </div>

    <div class="details-info">

        <h1 class="details-title"><%=service.getServiceName()%></h1>
        <p class="details-category"><%=categoryName%></p>
        <p class="details-description"><%=service.getServiceDescription()%></p>

        <p class="details-duration">
            <strong>Duration:</strong> <%=service.getDuration()%>
        </p>

        <div class="details-caregiver-card">
            <h3>Caregiver in charge</h3>

            <% if (cg == null) { %>
                <p class="muted">To be assigned</p>
            <% } else {
                String cimg = cg.getPhotoPath();
                if (cimg == null) cimg = "";
                cimg = cimg.trim();

                if (cimg.isEmpty()) cimg = "/images/default-caregiver.png";
                else if (!cimg.startsWith("/") && !cimg.startsWith("http://") && !cimg.startsWith("https://")) {
                    if (cimg.startsWith("images/") || cimg.startsWith("uploads/")) cimg = "/" + cimg;
                    else cimg = "/images/" + cimg;
                }
            %>

            <div class="cg-row">
                <img class="cg-photo" src="<%=request.getContextPath() + cimg%>" alt="Caregiver Photo">

                <div class="cg-info">
                    <div class="cg-name"><%= cg.getName() %></div>

                    <div class="cg-meta">
                        <span><strong>Experience:</strong> <%= cg.getExperienceYears() %> years</span>
                        <span><strong>Rate:</strong> $<%= String.format("%.2f", cg.getHourlyRate()) %>/hr</span>
                    </div>

                    <div class="cg-line"><strong>Languages:</strong> <%= cg.getLanguages() %></div>
                    <div class="cg-line"><strong>Qualifications:</strong> <%= cg.getQualifications() %></div>

                    <% if (cg.getBio() != null && !cg.getBio().trim().isEmpty()) { %>
                        <div class="cg-bio"><%= cg.getBio() %></div>
                    <% } %>

                    <a class="cg-link" href="<%=request.getContextPath()%>/caregiver?id=<%=cg.getCaregiverId()%>">
                        View caregiver profile
                    </a>
                </div>
            </div>

            <% } %>
        </div>

        <div class="details-price">
            $<%=String.format("%.2f", service.getPrice().doubleValue())%>
        </div>

        <div class="booking-section">

            <div class="booking-field">
                <label for="booking_date">Booking Date</label>
                <select name="booking_date" id="booking_date" form="cartForm" required class="booking-select"
                        onchange="onBookingDateChange(this.value)">
                    <%
                    LocalDate today = LocalDate.now();
                    LocalDate start = today.plusDays(3);
                    LocalDate end = today.plusDays(30);

                    for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                        String ds = d.toString();
                        String sel = ds.equals(selectedBookingDate) ? "selected" : "";
                    %>
                        <option value="<%= ds %>" <%= sel %>><%= ds %></option>
                    <%
                    }
                    %>
                </select>
            </div>

            <div class="booking-field">
                <label for="slot_id">Booking Time</label>
                <select name="slot_id" id="slot_id" form="cartForm" required class="booking-select">
                    <option value="">-- Select Time --</option>

                    <% if (slots == null || slots.isEmpty()) { %>
                        <option value="" disabled>(No time slots available)</option>
                    <% } else {
                        for (TimeSlot slot : slots) {

                            String label = slot.getDisplayLabel();
                            if (label == null || label.trim().isEmpty()) label = slot.getTimeValue();

                            int remaining = 1;
                            try { remaining = slot.getRemaining(); } catch (Exception ignored) {}

                            boolean full = (remaining <= 0);

                            String suffix;
                            if (full) suffix = " (FULL)";
                            else if (remaining == 1) suffix = " (1 left)";
                            else suffix = " (" + remaining + " left)";

                            String disabled = full ? "disabled" : "";
                            String cls = full ? "slot-full" : "slot-ok";
                    %>
                        <option value="<%= slot.getSlotId() %>" <%= disabled %> class="<%= cls %>">
                            <%= label + suffix %>
                        </option>
                    <%  } } %>
                </select>
            </div>

            <% if (!loggedIn) { %>
                <button type="button" class="add-cart-btn"
                    onclick="alert('Please login to add items to your cart.'); window.location.href='<%=request.getContextPath()%>/login.jsp';">
                    ADD TO CART
                </button>
            <% } else { %>

                <form id="cartForm" method="post" action="<%=request.getContextPath()%>/cart/addToCart.jsp">
                    <input type="hidden" name="service_id" value="<%=service.getServiceId()%>">
                    <input type="hidden" name="quantity" value="1">

                    <input type="hidden" name="category_id" value="<%= categoryId %>">
                    <input type="hidden" name="q" value="<%= q %>">
                    <input type="hidden" name="returnTo" value="<%= returnTo %>">

                    <button type="submit" class="add-cart-btn">ADD TO CART</button>
                </form>

            <% } %>
        </div>

        <a href="<%= backUrl %>" class="back-btn">Back</a>

    </div>
</div>

<script>
function onBookingDateChange(date) {
  const url = new URL(window.location.href);
  url.searchParams.set("booking_date", date);
  url.searchParams.delete("added");
  url.searchParams.delete("err");
  window.location.href = url.toString();
}
</script>

<%
if ("1".equals(request.getParameter("added"))) {
%>
<script>
window.onload = () => setTimeout(() => alert("Added to cart successfully."), 10);
</script>
<%
}
%>

<%@ include file="../footer.jsp"%>
