package lab04.entity;

import lab04.DBConnection;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao implements IDao<Category> {

    private final String initializeQuery = "create table if not exists 'category'" +
            " ('id' INTEGER PRIMARY KEY, " +
            "'name' text not null," +
            " 'description' text not null, unique(name))";
    private final String selectByIdQuery = "select * from 'category' where id = %s";
    private final String selectAllQuery = "select * from 'category'";
    private final String insertQuery = "insert into 'category' ('id', 'name', 'description') values (?, ?, ?);";
    private final String updateQuery = "update 'category' set name = ?, description = ?  where id = ?";
    private final String deleteQuery = "delete from 'category' where id = ?";

    private final Connection connection;

    public CategoryDao(DBConnection con) {
        this.connection = con.getCon();
        initTable();
    }

    private void initTable() {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(initializeQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create category table", e);
        }
    }

    @Override
    public Category getById(int id) {
        try (final Statement statement = connection.createStatement()) {
            final String sql = String.format(selectByIdQuery, id);
            final ResultSet resultSet = statement.executeQuery(sql);
            Category category = new Category(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"));
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get category", e);
        }
    }

    @Override
    public List<Category> getAll() {
        try (final Statement statement = connection.createStatement()) {

            final String sql = String.format(selectAllQuery);
            final ResultSet resultSet = statement.executeQuery(sql);

            final List<Category> categories = new ArrayList<>();
            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description")));
            }
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get list of categories", e);
        }
    }

    @Override
    public int insert(Category category) {
        try (final PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            insertStatement.setInt(1, category.getCategoryId());
            insertStatement.setString(2, category.getCategoryName());
            insertStatement.setString(3, category.getDescription());

            insertStatement.execute();

            return category.getCategoryId();
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert category", e);
        }
    }

    @Override
    public int update(Category category, int id) {
        try (final PreparedStatement preparedStatement =
                     connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Can't update category", e);
        }
    }

    @Override
    public int delete(int id) {
        try(final PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete category", e);
        }
    }

    public JSONObject toJSONObject(List<Category> groups){
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{\"list\":[");

        for (Category g: groups) {
            stringBuffer.append(g.toJSON().toString() + ", ");
        }
        if(stringBuffer.length()>9){
            stringBuffer.delete(stringBuffer.length()-2, stringBuffer.length()-1);
        }
        stringBuffer.append("]}");

        return new JSONObject(stringBuffer.toString());
    }
}
