package models;

public class ServiceCategory {
	private int categoryId;
	private String categoryName;
	private String categoryImage;
	private String categoryDescription;

	public ServiceCategory() {
	}

	public ServiceCategory(int categoryId, String categoryName, String categoryImage, String categoryDescription) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.categoryImage = categoryImage;
		this.categoryDescription = categoryDescription;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryImage() {
		return categoryImage;
	}

	public void setCategoryImage(String categoryImage) {
		this.categoryImage = categoryImage;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}
}
