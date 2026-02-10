<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="dbaccess.AdminInquiryDAO.ServiceDemandRow" %>

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
%>

<div class="ai-wrap">
  <div class="ai-hero">
    <h1>Admin Inquiry: Services Demand & Availability</h1>
    <p>Inquiry answers: most booked services, least booked services, and services with low configured slots.</p>
  </div>

  <div class="ai-filters">
    <form method="get" action="<%= request.getContextPath() %>/admin/inquiry">
      <label>Top N:
        <input type="number" name="top" value="<%= topN %>" min="1" max="50">
      </label>

      <label>Low Slots Threshold (≤):
        <input type="number" name="slotsThreshold" value="<%= slotsThreshold %>" min="0" max="50">
      </label>

      <button type="submit">Apply</button>
    </form>
  </div>

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
      <p class="muted">This detects “low availability setup” (admin forgot to set enough time slots).</p>

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
