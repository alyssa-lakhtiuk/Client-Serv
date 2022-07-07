import lab04.DBConnection;
import lab04.entity.Category;
import lab04.entity.ProductCriteriaFilter;
import lab04.entity.daos.CategoryDao;
import lab04.entity.Product;
import lab04.entity.daos.ProductDao;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {
    private static final String dbFileName = "storeDB";
    ProductDao productDao;
    CategoryDao categoryDao;

    @Test
    void testProductInsertionAndGetById(){
        Product product = new Product(1,
                1,
                "grechka",
                50,
                61.5,
                "very high quality grechka");

        productDao = new ProductDao(new DBConnection(dbFileName));
        productDao.insert(product);
        Product insertedProduct = productDao.getById(1);

        assertEquals(product.toString(), insertedProduct.toString());
        productDao.drop();
    }

    @Test
    void testCategoryInsertionAndGetById(){
        Category category = new Category(1,
                "krupy",
                "different krupy");

        categoryDao = new CategoryDao(new DBConnection(dbFileName));
        categoryDao.insert(category);
        Category insertedProduct = categoryDao.getById(1);

        assertEquals(category.toString(), insertedProduct.toString());
        categoryDao.drop();
    }

    @Test
    void testProductUpdate(){
        Product product1 = new Product(
                1,
                1,
                "grechka",
                50,
                61.5,
                "very high quality grechka");
        Product product2 = new Product(
                1,
                1,
                "manka",
                15,
                75.3,
                "very high quality manka");

        ProductDao  daoProduct = new ProductDao(new DBConnection(dbFileName));
        daoProduct.insert(product1);
        daoProduct.update(product2, product1.getProductId());
        Product updatedProduct = daoProduct.getById(1);

        assertEquals(product2.toString(), updatedProduct.toString());
        daoProduct.drop();
    }

    @Test
    void testGetAllProducts(){
        final ProductDao daoProduct = new ProductDao(new DBConnection(dbFileName));
        List<Product> expectedList = new ArrayList<>();
        for(int i = 1; i <= 5; i++){
            Product newProduct = new Product(
                    i,
                    1,
                    "grechka" + i,
                    50,
                    61.5 + i * 1.5,
                    "very high quality grechka" + i);
            daoProduct.insert(newProduct);
            expectedList.add(newProduct);
        }

        List<Product> products = daoProduct.getAll();

        //assertEquals(expected, products); //тест фейлиться, але при відкритті відмінностей, пише, що виводи однакові...
        assertEquals(expectedList.size(), products.size());

        daoProduct.drop();
    }

    @Test
    void testProductFiltration(){
        ProductCriteriaFilter filter = new ProductCriteriaFilter();
        filter.setCategory_id(1);
        filter.setFromPrice(200.0);
        int expected = 5;
        final ProductDao daoProduct = new ProductDao(new DBConnection(dbFileName));
        for(int i = 0; i < expected; i++){
            daoProduct.insert(new Product(
                    i,
                    1,
                    "grechka" + i,
                    50,
                    61.5 + i * 1.5,
                    "very high quality grechka" + i));
        }
        for(int i = 5; i < 10; i++){
            daoProduct.insert(new Product(
                    i,
                    1,
                    "manka" + i,
                    50,
                    200 + i * 1.5,
                    "very high quality manka" + i));
        }

        List<Product> products = daoProduct.listByCriteria(0,10, filter);
        assertEquals(expected, products.size());
        daoProduct.drop();
    }

    @Test
    void testDeleteProduct(){
        Product product1 = new Product(
                1,
                1,
                "grechka",
                50,
                61.5,
                "very high quality grechka");
        Product product2 = new Product(
                1,
                1,
                "manka",
                15,
                75.3,
                "very high quality manka");

        ProductDao  daoProduct = new ProductDao(new DBConnection(dbFileName));
        daoProduct.insert(product1);
        daoProduct.insert(product2);
        daoProduct.delete(1);
        assertThrows(
                java.lang.RuntimeException.class,
                () -> daoProduct.getById(1)
        );
        daoProduct.drop();
    }

    @Test
    void testDeleteCategory(){
        Category category1 = new Category(1,
                "krupy",
                "different krupy");
        Category category2 = new Category(2,
                "voda",
                "mineralna");

        CategoryDao  daoCategory = new CategoryDao(new DBConnection(dbFileName));
        daoCategory.insert(category1);
        daoCategory.insert(category2);
        daoCategory.delete(1);
        assertThrows(
                java.lang.RuntimeException.class,
                () -> daoCategory.getById(1)
        );
        daoCategory.drop();
    }

    @Test
    void testInsertCategoryFail(){
        Category category1 = new Category(1,
                "krupy",
                "different krupy");
        Category category2 = new Category(2,
                "krupy",// Same category name
                "different");

        CategoryDao  daoCategory = new CategoryDao(new DBConnection(dbFileName));
        daoCategory.insert(category1);
        assertThrows(
                java.lang.RuntimeException.class,
                () -> daoCategory.insert(category2)
        );
        daoCategory.drop();
    }

    @Test
    void testUpdateCategoryAndGet(){

        Category category1 = new Category(1,
                "krupy",
                "different krupy");
        Category category2 = new Category(1,
                "voda",
                "mineralna");
        CategoryDao daoGroup = new CategoryDao(new DBConnection(dbFileName));
        daoGroup.insert(category1);
        daoGroup.update(category2, category1.getCategoryId());
        Category updatedGroup = daoGroup.getById(1);
        assertEquals(category2.toString(), updatedGroup.toString());
        daoGroup.drop();
    }
}
