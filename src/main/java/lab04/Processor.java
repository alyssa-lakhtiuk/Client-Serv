package lab04;

import lab04.entity.*;
import lab04.entity.daos.CategoryDao;
import lab04.entity.daos.ProductDao;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Processor {
    private static final String dbFileName = "storeDB";
    public static byte[] process(byte[] packFromUser) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Pack packet = new Pack(packFromUser);

        Message.cTypes [] values = Message.cTypes.values();
        int command = packet.getbMsq().getMessageCType();
        Message.cTypes command_CType = values[command];

        String message = new String(packet.getbMsq().getMessageBMsq(), StandardCharsets.UTF_8);

        ProductDao productDao;
        JSONObject information;
        CategoryDao categoryDao;
        String reply;
        int result;
        DBConnection db = new DBConnection(dbFileName);
        //Connection connection = db.getCon();

        switch(command_CType){
            case InsertProduct:
                information = new JSONObject(message);
                Product product = new Product(
                        information.getInt("id"),
                        information.getInt("category_id"),
                        information.getString("name"),
                        information.getInt("quantity"),
                        information.getDouble("price"),
                        information.getString("description")
                       );
                productDao = new ProductDao(db);
                result = productDao.insert(product);

                if(result == -1){
                    reply = "Invalid name of product";
                }
                else{
                    reply = "Successful insertion";
                }
                break;

            case UpdateProduct:
                information = new JSONObject(message);
                Product product2 = new Product(information.getInt("id"),
                        information.getInt("category_id"),
                        information.getString("name"),
                        information.getInt("quantity"),
                        information.getDouble("price"),
                        information.getString("description"));

                productDao = new ProductDao(db);
                result = productDao.update(product2, product2.getProductId());

                if(result == -1){
                    reply = "Invalid name of product";
                }
                else{
                    reply = "Successful insertion";
                }
                break;

            case DeleteProduct:
                int id = Integer.parseInt(message);
                productDao = new ProductDao(db);
                result = productDao.delete(id);
                if(result == -1){
                    reply = "Invalid name of product";
                }
                else{
                    reply = "Successfully deleted product with id " + result;
                }
                break;

            case GetProduct:
                int id4 = Integer.parseInt(message);
                productDao = new ProductDao(db);
                Product product4 = productDao.getById(id4);

                reply = product4.toJSON().toString();
                break;

            case GetListProducts:
                information = new JSONObject(message);

                int page = information.getInt("page");
                int size = information.getInt("size");

                JSONObject filtr = information.getJSONObject("productFilter");
                ProductCriteriaFilter filter = new ProductCriteriaFilter();
                JSONArray array = filtr.getJSONArray("ids");

                if(!filtr.isNull("ids")){
                    List<Integer> arrayList = new ArrayList<>();
                    for(int i = 0; i < array.length(); i++){
                        arrayList.add((Integer)(array.get(i)));
                    }
                    filter.setIds(arrayList);
                }
                if(!filtr.isNull("category_id")){
                    filter.setCategory_id(filtr.getInt("category_id"));
                }
                if(!filtr.isNull("toPrice")){
                    filter.setToPrice(filtr.getDouble("toPrice"));
                }
                if(!filtr.isNull("fromPrice")){
                    filter.setFromPrice(filtr.getDouble("fromPrice"));
                }
                if(!filtr.isNull("query")){
                    filter.setQuery("query");
                }
                productDao = new ProductDao(db);

                reply = productDao.toJSONObject(productDao.listByCriteria(page, size, filter)).toString();
                break;

            case InsertCategory:
                information = new JSONObject(message);
                Category group = new Category(information.getInt("id"),
                        information.getString("name"),
                        information.getString("description"));
                categoryDao = new CategoryDao(db);
                result = categoryDao.insert(group);
                if(result == -1){
                    reply = "Invalid name of group";
                }
                else{
                    reply = "Successfully inserted group";
                }
                break;

            case DeleteCategory:
                int group_id = Integer.parseInt(message);
                categoryDao = new CategoryDao(db);
                result = categoryDao.delete(group_id);
                if(result == -1){
                    reply = "Can't delete group";
                }
                else{
                    reply = "Successfully deleted group";
                }
                break;

            case UpdateCategory:
                information = new JSONObject(message);
                Category category1 = new Category( information.getInt("id"),
                        information.getString("name"), information.getString("description"));

                categoryDao = new CategoryDao(db);
                result = categoryDao.update(category1, category1.getCategoryId());

                if(result == -1){
                    reply = "Invalid name of group";
                }
                else{
                    reply = "Successfully updated category";
                }
                break;

            case GetCategory:
                int group_id1 = Integer.parseInt(message);
                categoryDao = new CategoryDao(db);
                Category resGroup = categoryDao.getById(group_id1);

                reply = resGroup.toJSON().toString();
                break;

            case GetListCat:
                categoryDao = new CategoryDao(db);
                reply = categoryDao.toJSONObject(categoryDao.getAll()).toString();
                break;

            default:
                reply = "invalid message";
        }

        System.out.println("Message from client: " + new String(packet.getbMsq().getMessageBMsq(), StandardCharsets.UTF_8)
                + "\t\t\t with user ID: " + packet.getbSrc()
                + "\t\t\t and with packet ID: " + packet.getbPktId());


        Message answerMessage = new Message(packet.getbMsq().getMessageCType(),
                packet.getbMsq().getMessageBUserId(),
                reply.getBytes(StandardCharsets.UTF_8));
        Pack answerPacket = new Pack(packet.getbSrc(),
                packet.getbPktId(),
                answerMessage.getMessageBMsq().length,
                answerMessage);

        return answerPacket.packToBytes(); //returns encoded response for USER
    }

}
