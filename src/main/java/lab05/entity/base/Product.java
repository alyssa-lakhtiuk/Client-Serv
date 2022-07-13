package lab05.entity.base;

import jakarta.persistence.*;
import org.json.JSONObject;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int productId;
    private int categoryId;

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    private String name;
    private int quantity;
    private double price;
    private String description;

    private String maker;

    public Product(int productId, int categoryId, String name, int quantity, double price, String description, String maker) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
        this.maker = maker;
    }

    public Product(int categoryId, String name, int quantity, double price, String description, String maker) {
        this.categoryId = categoryId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
        this.maker = maker;
    }

    public Product() {

    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getMaker() {
        return maker;
    }



    @Override
    public String toString() {
        return "Product : \n id = " + productId +
                ", \n name = " + name +
                ", \n price = " + price +
                ", \n quantity = " + quantity +
                ", \n description = " + description +
                ", \n category = " + categoryId +
                ", \n maker = " + maker;
    }

    public JSONObject toJSON(){

        JSONObject json = new JSONObject("{"+"\"id\":"+productId+", \"name\":\""+name+
                "\", \"price\":"+ price+", \"quantity\":"+quantity+
                ", \"description\":\""+description+"\", \"category_id\":"+categoryId+"\", \"maker\":"+maker+"}");

        return json;
    }
}
