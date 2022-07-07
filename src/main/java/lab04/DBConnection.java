package lab04;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {
    Connection con;
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
    }
}
