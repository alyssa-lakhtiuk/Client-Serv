package lab05;

import lab05.entity.daos.CategoryDao;
import lab05.entity.daos.ProductDao;
import lab05.entity.daos.UserDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    Connection con;

    private CategoryDao categoryDao;

    private ProductDao productDao;

    private UserDao userDao;

    private static final String dbFileName = "storeDB";

    private static volatile DBConnection instance;

    public Connection getCon() {
        return con;
    }

    public DBConnection(String DBfile){
        try{
            Class.forName("org.sqlite.JDBC");
            this.con = DriverManager.getConnection("jdbc:sqlite:" + DBfile);
//            PreparedStatement st = con.prepareStatement(
//                    "create table if not exists 'test' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text);");
//            int result = st.executeUpdate();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Driver JDBC not found");
            e.printStackTrace();
            System.exit(0);
        }
        initTables();
    }

    public DBConnection(){
        try{
            Class.forName("org.sqlite.JDBC");
            this.con = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Driver JDBC not found");
            e.printStackTrace();
            System.exit(0);
        }
        initTables();
    }

    public static DBConnection getInstance(final String fileName) {
        DBConnection localInstance = instance;
        if (localInstance == null) {
            synchronized (DBConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DBConnection(fileName);
                }
            }
        }
        return localInstance;
    }

    public static DBConnection getInstance() {
        DBConnection localInstance = instance;
        if (localInstance == null) {
            synchronized (DBConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DBConnection();
                }
            }
        }
        return localInstance;
    }
    public void initTables(){
        this.categoryDao = new CategoryDao(con);
        this.productDao = new ProductDao(con);
        this.userDao = new UserDao(con);
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }
}
