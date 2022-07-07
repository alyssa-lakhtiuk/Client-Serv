package lab04.entity;

import jakarta.persistence.*;
import org.json.JSONObject;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int productId;
    private int categoryId;
    private String name;
    private int quantity;
    private double price;
    private String description;

    public Product(int productId, int categoryId, String name, int quantity, double price, String description) {
        this.categoryId = categoryId;
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }

    public Product() {

    }

    public int getCategory() {
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



    @Override
    public String toString() {
        return "Product : \n id = " + productId +
                ", \n name = " + name +
                ", \n price = " + price +
                ", \n quantity = " + quantity +
                ", \n description = " + description +
                ", \n category = " + categoryId;
    }

    public JSONObject toJSON(){

        JSONObject json = new JSONObject("{"+"\"id\":"+productId+", \"name\":\""+name+
                "\", \"price\":"+ quantity+", \"quantity\":"+quantity+
                ", \"description\":\""+description+"\", \"category_id\":"+categoryId+"}");

        return json;
    }
}
