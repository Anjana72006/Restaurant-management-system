package model;

public class MenuItem {
    private int itemId;
    private String name;
    private int categoryId;
    private String categoryName;
    private double price;
    private int availableQty;
    private String description;

    public MenuItem() {}

    public MenuItem(int itemId, String name, int categoryId, double price, int availableQty, String description) {
        this.itemId = itemId;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.availableQty = availableQty;
        this.description = description;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getAvailableQty() { return availableQty; }
    public void setAvailableQty(int availableQty) { this.availableQty = availableQty; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
