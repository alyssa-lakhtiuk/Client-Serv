package lab04.entity;

import org.json.JSONObject;

public class Category {
    private final int categoryId;
    private final String categoryName;

    private final String description;

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getDescription() {
        return description;
    }

    public Category(int categoryId, String categoryName, String description) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
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
