package lab05.entity.base;

import jakarta.persistence.*;
import org.json.JSONObject;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int categoryId;
    private String categoryName;

    private String description;

    public Category() {

    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category(int categoryId, String categoryName, String description) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.description = description;
    }

    public Category(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product : \n id = " + categoryId +
                ", \n name = " + categoryName +
                ", \n description = " + description;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject("{"+"\"id\":"+categoryId+", " +
                "\"name\":\""+categoryName+"\", " +
                "\"description\":\""+description+"\"}");
        return json;
    }
}
