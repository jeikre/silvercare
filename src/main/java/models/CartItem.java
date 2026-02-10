package models;

import java.math.BigDecimal;

public class CartItem {
	private int itemId;
	private int cartId;

	private Integer productId; // null if service
	private Integer serviceId; // null if product
	private int quantity;

	private String itemName;
	private BigDecimal unitPrice;
	private String imagePath;

	// ✅ booking fields (for services mostly)
	private String bookingDate; // e.g. "2026-02-06"
	private String bookingTime; // e.g. "14:00:00"
	private String bookingTimeDisplay; // e.g. "2:00 PM"

	// ===== getters/setters =====
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getCartId() {
		return cartId;
	}

	public void setCartId(int cartId) {
		this.cartId = cartId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(String bookingTime) {
		this.bookingTime = bookingTime;
	}

	public String getBookingTimeDisplay() {
		return bookingTimeDisplay;
	}

	public void setBookingTimeDisplay(String bookingTimeDisplay) {
		this.bookingTimeDisplay = bookingTimeDisplay;
	}
}
