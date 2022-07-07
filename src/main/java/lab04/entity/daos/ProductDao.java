package lab04.entity.daos;

import lab04.DBConnection;
import lab04.entity.Product;
import lab04.entity.ProductCriteriaFilter;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ProductDao implements IDao<Product> {
    private final String initializeQuery = "create table if not exists 'product' " +
            "('id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "'name' text not null, " +
            "'price' decimal(10, 4) not null, " +
            "'quantity' decimal not null, " +
            "'description' text not null, " +
            "'category_id' integer not null, foreign key(category_id) references category(id) " +
            "ON UPDATE CASCADE ON DELETE CASCADE);";
    private final String insertQuery = "insert into 'product'" + " ('name', 'price', 'quantity', 'description', 'category_id') values (?, ?, ?, ?, ?);";
    private final String updateQuery = "update 'product' set name = ?, price = ?, quantity = ?, description = ?, category_id = ?  where id = ?";
    private final String deleteQuery = "delete from 'product' where id = ?";
    private final String selectByIdQuery = "select * from 'product' where id = %s";
    private final String selectAllQuery = "select * from 'product'";
    private final String dropQuery = "drop table 'product'";
    private final Connection connection;

    public ProductDao(DBConnection con) {
        this.connection = con.getCon();
        initTable();
    }

    private void initTable() {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(initializeQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create product table", e);
        }
    }

    @Override
    public Product getById(int id) {
        try (final Statement statement = connection.createStatement()) {

            final String sql = String.format(selectByIdQuery, id);
            final ResultSet resultSet = statement.executeQuery(sql);

            Product product = new Product(
                    resultSet.getInt("id"),
                    resultSet.getInt("category_id"),
                    resultSet.getString("name"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("price"),
                    resultSet.getString("description")
            );
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get product", e);
        }
    }

    @Override
    public List<Product> getAll() {
        try (final Statement statement = connection.createStatement()) {

            final String sql = String.format(selectAllQuery);
            final ResultSet resultSet = statement.executeQuery(sql);
            final List<Product> products = new ArrayList<>();
            while (resultSet.next()) {
                products.add(new Product(
                        resultSet.getInt("id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price"),
                        resultSet.getString("description")));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get list of product", e);
        }
    }

    @Override
    public int insert(Product product) {
        try (final PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            insertStatement.setString(1, product.getName());
            insertStatement.setDouble(2, product.getPrice());
            insertStatement.setDouble(3, product.getQuantity());
            insertStatement.setString(4, product.getDescription());
            insertStatement.setInt(5, product.getCategory());

            insertStatement.execute();
            final ResultSet result = insertStatement.getGeneratedKeys();
            return result.getInt("last_insert_rowid()");
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert product", e);
        }
    }

    @Override
    public int update(Product product, int id) {
        try (final PreparedStatement preparedStatement =
                     connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setDouble(3, product.getQuantity());
            preparedStatement.setString(4, product.getDescription());
            preparedStatement.setInt(5, product.getCategory());
            preparedStatement.setDouble(6, id);
            preparedStatement.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Can't update product", e);
        }
    }

    @Override
    public int delete(int id) {
        try(final PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete product", e);
        }
    }

    public List<Product> listByCriteria(final int p, final int s, final ProductCriteriaFilter filter){
        try(final Statement statement = connection.createStatement()){
            final String query = Stream.of(
                            in("id", filter.getIds()),
                            gte("price", filter.getFromPrice()),
                            lte("price", filter.getToPrice()),
                            category("category_id", filter.getCategory_id())
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" AND "));

            final String where = query.isEmpty() ? "" : " where "+query;
            final String sql = String.format("select * from 'product' %s limit %s offset %s", where, s, p * s);
            final ResultSet resultSet = statement.executeQuery(sql);

            final List<Product> products = new ArrayList<>();
            while(resultSet.next()){
                products.add(new Product(
                        resultSet.getInt("id"),
                        resultSet.getInt("category_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price"),
                        resultSet.getString("description")));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Can't create table", e);
        }
    }

    private static String in(final String fieldName, final Collection<?> collection){
        if(collection == null || collection.isEmpty()){
            return null;
        }
        return fieldName + " IN (" + collection.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }

    private static String gte(final String fieldName, final Double value){
        if(value == null){
            return null;
        }
        return fieldName + " >= " + value;
    }

    private static String category(final String fieldName, final Integer value){
        if(value == null){
            return null;
        }
        return fieldName + " = " + value;
    }

    private static String lte(final String fieldName, final Double value){
        if(value == null){
            return null;
        }
        return fieldName + " <= " + value;
    }

    @Override
    public void drop() {
        try(final Statement statement = connection.createStatement()){
            statement.execute(dropQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Can't drop table", e);
        }
    }

    public JSONObject toJSONObject(List<Product> products){
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{\"list\":[");

        for (Product g: products) {
            stringBuffer.append(g.toJSON().toString() + ", ");
        }
        stringBuffer.delete(stringBuffer.length()-2, stringBuffer.length()-1);
        stringBuffer.append("]}");

        return new JSONObject(stringBuffer.toString());
    }

//    private void executeInsideTransaction(Consumer<EntityManager> action) {
//        EntityTransaction tx = entityManager.getTransaction();
//        try {
//            tx.begin();
//            action.accept(entityManager);
//            tx.commit();
//        } catch (RuntimeException e) {
//            tx.rollback();
//            throw e;
//        }
//    }
}
