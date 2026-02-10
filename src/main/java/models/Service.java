package models;

import java.math.BigDecimal;

public class Service {
	private int serviceId;
	private Integer categoryId;
	private String serviceName;
	private String serviceDescription;
	private BigDecimal price;
	private String duration;
	private String serviceImage;
	private String caregiverName;
	
	public Service() {
	}

	public Service(int serviceId, Integer categoryId, String serviceName, String serviceDescription, BigDecimal price,
			String duration, String serviceImage) {
		this.serviceId = serviceId;
		this.categoryId = categoryId;
		this.serviceName = serviceName;
		this.serviceDescription = serviceDescription;
		this.price = price;
		this.duration = duration;
		this.serviceImage = serviceImage;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getServiceImage() {
		return serviceImage;
	}

	public void setServiceImage(String serviceImage) {
		this.serviceImage = serviceImage;
	}

	public String getCaregiverName() { return caregiverName; }
	
	public void setCaregiverName(String caregiverName) { 
		this.caregiverName = caregiverName; }

}
