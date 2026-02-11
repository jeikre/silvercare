package models;

public class AdminInquiry {
	public static class BookingRow {
	    public int orderId;
	    public String orderDate; // keep String for JSP simplicity
	    public int memberId;
	    public String memberName;
	    public String serviceName;
	    public int qty;
	    public double unitPrice;
	    public double lineTotal;

	    public BookingRow(int orderId, String orderDate, int memberId, String memberName,
	                      String serviceName, int qty, double unitPrice) {
	        this.orderId = orderId;
	        this.orderDate = orderDate;
	        this.memberId = memberId;
	        this.memberName = memberName;
	        this.serviceName = serviceName;
	        this.qty = qty;
	        this.unitPrice = unitPrice;
	        this.lineTotal = unitPrice * qty;
	    }
	}

	public static class TopClientRow {
	    public int memberId;
	    public String memberName;
	    public double totalSpent;
	    public int paidOrders;

	    public TopClientRow(int memberId, String memberName, double totalSpent, int paidOrders) {
	        this.memberId = memberId;
	        this.memberName = memberName;
	        this.totalSpent = totalSpent;
	        this.paidOrders = paidOrders;
	    }
	}

	public static class ClientBookedServiceRow {
	    public int memberId;
	    public String memberName;
	    public String email;
	    public int totalQty;

	    public ClientBookedServiceRow(int memberId, String memberName, String email, int totalQty) {
	        this.memberId = memberId;
	        this.memberName = memberName;
	        this.email = email;
	        this.totalQty = totalQty;
	    }
	}
	  // DTO / simple row class
    public static class ServiceDemandRow {
        public int serviceId;
        public String serviceName;
        public int totalQty;
        public int ordersCount;
        public int slotsCount;

        public ServiceDemandRow(int serviceId, String serviceName, int totalQty, int ordersCount, int slotsCount) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.totalQty = totalQty;
            this.ordersCount = ordersCount;
            this.slotsCount = slotsCount;
        }
    }
}
