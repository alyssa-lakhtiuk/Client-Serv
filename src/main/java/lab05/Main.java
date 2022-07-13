package lab05;

import static lab05.utils.ParseConfig.YamlParser;

public class Main {
    private static final int Port = 8001;
    public static void main(String[] args) throws Exception {
        YamlParser();
        new Server(Port);

//        String dbFileName = "storeDB";
//        DBConnection db = new DBConnection(dbFileName);

//        User user = new User(1, "admin01", "abcdsecret01", "admin");
//        YamlParser();
//        String token = JWTgenerator.createJWT(user);
//        System.out.println(token);
//        Claims claims = JWTgenerator.parseJWT(token);
//        System.out.println(claims.getSubject());
//

    }
}
