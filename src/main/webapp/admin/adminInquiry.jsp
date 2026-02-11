<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>

<%@ page import="models.AdminInquiry.ServiceDemandRow" %>
<%@ page import="models.AdminInquiry.BookingRow" %>
<%@ page import="models.AdminInquiry.TopClientRow" %>
<%@ page import="models.AdminInquiry.ClientBookedServiceRow" %>

<%@ include file="../header.jsp" %>
<%@ include file="../navbar.jsp" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/adminInquiry.css?v=<%=System.currentTimeMillis()%>">

<%
List<ServiceDemandRow> summary = (List<ServiceDemandRow>) request.getAttribute("summary");
List<ServiceDemandRow> top = (List<ServiceDemandRow>) request.getAttribute("top");
List<ServiceDemandRow> low = (List<ServiceDemandRow>) request.getAttribute("low");
List<ServiceDemandRow> lowSlots = (List<ServiceDemandRow>) request.getAttribute("lowSlots");

Integer topN = (Integer) request.getAttribute("topN");
Integer slotsThreshold = (Integer) request.getAttribute("slotsThreshold");
if (topN == null) topN = 5;
if (slotsThreshold == null) slotsThreshold = 3;

// Billing reports
List<BookingRow> bookings = (List<BookingRow>) request.getAttribute("bookings");
List<TopClientRow> topClients = (List<TopClientRow>) request.getAttribute("topClients");
List<ClientBookedServiceRow> clientsByService = (List<ClientBookedServiceRow>) request.getAttribute("clientsByService");

Integer topClientsN = (Integer) request.getAttribute("topClientsN");
if (topClientsN == null) topClientsN = 5;

String start = (String) request.getAttribute("start");
String end = (String) request.getAttribute("end");
String month = (String) request.getAttribute("month");
String selectedServiceId = (String) request.getAttribute("selectedServiceId");

double bookingTotal = 0;
if (bookings != null) {
  for (BookingRow b : bookings) bookingTotal += b.lineTotal;
}
%>

<div class="ai-wrap">
  <div class="ai-hero">
    <h1>Admin Inquiry & Billing Reports</h1>
    <p>
      Bookings/care schedules by date/period/month,
      top clients by value, and clients who booked a service — plus service demand & availability.
    </p>
  </div>

  <!-- ===================== BILLING REPORT FILTERS ===================== -->
  <div class="ai-card ai-span">
    <h2>Billing Inquiry & Reporting</h2>
    <p class="muted">
      Use the filters below to generate billing reports. (Paid orders only.)
    </p>

    <form method="get" action="<%= request.getContextPath() %>/admin/inquiry" class="ai-billing-filters">
      <div class="ai-filter-row">
        <label>Start:
          <input type="date" name="start" value="<%= (start == null ? "" : start) %>">
        </label>

        <label>End:
          <input type="date" name="end" value="<%= (end == null ? "" : end) %>">
        </label>

        <span class="muted">OR</span>

        <label>Month:
          <input type="month" name="month" value="<%= (month == null ? "" : month) %>">
        </label>

        <button type="submit">View Bookings</button>
      </div>

      <div class="ai-filter-row">
        <label>Top Clients N:
          <input type="number" name="topClients" value="<%= topClientsN %>" min="1" max="50">
        </label>

        <label>Clients who booked Service ID:
          <input type="number" name="serviceId" value="<%= (selectedServiceId == null ? "" : selectedServiceId) %>" min="1">
        </label>

        <button type="submit">Run Client Reports</button>
      </div>

      <!-- Keep your existing demand inquiry filters -->
      <input type="hidden" name="top" value="<%= topN %>">
      <input type="hidden" name="slotsThreshold" value="<%= slotsThreshold %>">
    </form>
  </div>

  <!-- ===================== REPORT 1: BOOKINGS BY PERIOD/MONTH ===================== -->
  <div class="ai-card ai-span">
    <h2>Bookings / Care Schedules (Paid) by Date / Period / Month</h2>

    <% if (bookings == null || bookings.isEmpty()) { %>
      <p class="muted">No booking records found for selected period/month.</p>
    <% } else { %>
      <p class="muted">
        Total Revenue (selected period): <strong>$<%= String.format("%.2f", bookingTotal) %></strong>
      </p>

      <table class="ai-table">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Date</th>
            <th>Client</th>
            <th>Service</th>
            <th>Qty</th>
            <th>Unit Price</th>
            <th>Line Total</th>
          </tr>
        </thead>
        <tbody>
        <% for (BookingRow b : bookings) { %>
          <tr>
            <td><%= b.orderId %></td>
            <td><%= b.orderDate %></td>
            <td><%= b.memberName %></td>
            <td><%= b.serviceName %></td>
            <td><%= b.qty %></td>
            <td>$<%= String.format("%.2f", b.unitPrice) %></td>
            <td>$<%= String.format("%.2f", b.lineTotal) %></td>
          </tr>
        <% } %>
        </tbody>
      </table>
    <% } %>
  </div>

  <!-- ===================== REPORT 2: TOP CLIENTS BY VALUE ===================== -->
  <div class="ai-grid">

    <div class="ai-card">
      <h2>Top <%= topClientsN %> Clients (by Value of Services Used)</h2>

      <table class="ai-table">
        <thead>
          <tr>
            <th>Client</th>
            <th>Paid Orders</th>
            <th>Total Spent</th>
          </tr>
        </thead>
        <tbody>
        <% if (topClients == null || topClients.isEmpty()) { %>
          <tr><td colspan="3" class="muted">No paid orders yet.</td></tr>
        <% } else {
             for (TopClientRow c : topClients) { %>
          <tr>
            <td><%= c.memberName %></td>
            <td><%= c.paidOrders %></td>
            <td>$<%= String.format("%.2f", c.totalSpent) %></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
    </div>

    <!-- ===================== REPORT 3: CLIENTS WHO BOOKED SERVICE ===================== -->
    <div class="ai-card">
      <h2>Clients Who Booked a Service</h2>
      <p class="muted">
        <% if (selectedServiceId == null || selectedServiceId.trim().isEmpty()) { %>
          Select a Service ID above to view clients.
        <% } else { %>
          Showing results for Service ID: <strong><%= selectedServiceId %></strong>
        <% } %>
      </p>

      <table class="ai-table">
        <thead>
          <tr>
            <th>Client</th>
            <th>Email</th>
            <th>Total Qty Booked</th>
          </tr>
        </thead>
        <tbody>
        <% if (clientsByService == null || clientsByService.isEmpty()) { %>
          <tr><td colspan="3" class="muted">No clients found (or no service selected).</td></tr>
        <% } else {
             for (ClientBookedServiceRow r : clientsByService) { %>
          <tr>
            <td><%= r.memberName %></td>
            <td><%= r.email %></td>
            <td><%= r.totalQty %></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
    </div>

  </div>

  <!-- ===================== EXISTING DEMAND & AVAILABILITY FILTERS ===================== -->
  <div class="ai-filters">
    <form method="get" action="<%= request.getContextPath() %>/admin/inquiry">
      <label>Top N:
        <input type="number" name="top" value="<%= topN %>" min="1" max="50">
      </label>

      <label>Low Slots Threshold (≤):
        <input type="number" name="slotsThreshold" value="<%= slotsThreshold %>" min="0" max="50">
      </label>

      <!-- keep billing filters when applying demand filters -->
      <input type="hidden" name="topClients" value="<%= topClientsN %>">
      <input type="hidden" name="start" value="<%= (start == null ? "" : start) %>">
      <input type="hidden" name="end" value="<%= (end == null ? "" : end) %>">
      <input type="hidden" name="month" value="<%= (month == null ? "" : month) %>">
      <input type="hidden" name="serviceId" value="<%= (selectedServiceId == null ? "" : selectedServiceId) %>">

      <button type="submit">Apply Demand Filters</button>
    </form>
  </div>

  <!-- ===================== DEMAND & AVAILABILITY TABLES (your original ones) ===================== -->
  <div class="ai-grid">

    <div class="ai-card">
      <h2>Top <%= topN %> Demanded Services</h2>
      <table class="ai-table">
        <thead>
          <tr>
            <th>Service</th>
            <th>Total Booked Qty</th>
            <th>Paid Orders</th>
            <th>Slots Configured</th>
          </tr>
        </thead>
        <tbody>
        <% if (top == null || top.isEmpty()) { %>
          <tr><td colspan="4" class="muted">No data.</td></tr>
        <% } else {
             for (ServiceDemandRow r : top) { %>
          <tr>
            <td>
              <a href="<%= request.getContextPath() %>/services/details?service_id=<%= r.serviceId %>">
                <%= r.serviceName %>
              </a>
            </td>
            <td><%= r.totalQty %></td>
            <td><%= r.ordersCount %></td>
            <td><%= r.slotsCount %></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
    </div>

    <div class="ai-card">
      <h2>Lowest <%= topN %> Booked Services</h2>
      <table class="ai-table">
        <thead>
          <tr>
            <th>Service</th>
            <th>Total Booked Qty</th>
            <th>Paid Orders</th>
            <th>Slots Configured</th>
          </tr>
        </thead>
        <tbody>
        <% if (low == null || low.isEmpty()) { %>
          <tr><td colspan="4" class="muted">No data.</td></tr>
        <% } else {
             for (ServiceDemandRow r : low) { %>
          <tr>
            <td>
              <a href="<%= request.getContextPath() %>/services/details?service_id=<%= r.serviceId %>">
                <%= r.serviceName %>
              </a>
            </td>
            <td><%= r.totalQty %></td>
            <td><%= r.ordersCount %></td>
            <td><%= r.slotsCount %></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
    </div>

    <div class="ai-card ai-span">
      <h2>Services With Low Configured Slots (≤ <%= slotsThreshold %>)</h2>
      <p class="muted">This detects low availability setup (not enough time slots configured).</p>

      <table class="ai-table">
        <thead>
          <tr>
            <th>Service</th>
            <th>Slots Configured</th>
            <th>Total Booked Qty</th>
            <th>Paid Orders</th>
          </tr>
        </thead>
        <tbody>
        <% if (lowSlots == null || lowSlots.isEmpty()) { %>
          <tr><td colspan="4" class="muted">All services have enough time slots.</td></tr>
        <% } else {
             for (ServiceDemandRow r : lowSlots) { %>
          <tr>
            <td>
              <a href="<%= request.getContextPath() %>/services/details?service_id=<%= r.serviceId %>">
                <%= r.serviceName %>
              </a>
            </td>
            <td><%= r.slotsCount %></td>
            <td><%= r.totalQty %></td>
            <td><%= r.ordersCount %></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
    </div>

    <div class="ai-card ai-span">
      <h2>All Services Demand Summary</h2>
      <table class="ai-table">
        <thead>
          <tr>
            <th>Service</th>
            <th>Total Booked Qty</th>
            <th>Paid Orders</th>
            <th>Slots Configured</th>
          </tr>
        </thead>
        <tbody>
        <% if (summary == null || summary.isEmpty()) { %>
          <tr><td colspan="4" class="muted">No data.</td></tr>
        <% } else {
             for (ServiceDemandRow r : summary) { %>
          <tr>
            <td>
              <a href="<%= request.getContextPath() %>/services/details?service_id=<%= r.serviceId %>">
                <%= r.serviceName %>
              </a>
            </td>
            <td><%= r.totalQty %></td>
            <td><%= r.ordersCount %></td>
            <td><%= r.slotsCount %></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
    </div>

  </div>
</div>
