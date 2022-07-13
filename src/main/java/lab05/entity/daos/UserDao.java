package lab05.entity.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import lab05.entity.base.User;

public class UserDao implements IDao<User>{
    private final String initializeQuery = "create table if not exists 'user'" +
            " ('id' INTEGER PRIMARY KEY, " +
            "'login' text not null," +
            "'password' text not null," +
            " 'role' text not null, unique(login))";
    private final String selectByIdQuery = "select * from 'user' where id = %s";
    private final String selectByLoginQuery = "select * from 'user' where login = \"%s\"";
    final String selectAllQuery = "select * from 'user'";
    private final String insertWithOutIdQuery = "insert into 'user' ('login', 'password', 'role') values (?, ?, ?);";
    private final String insertQuery = "insert into 'user' ('id', 'login', 'password', 'role') values (?, ?, ?, ?);";
    private final String updateQuery = "update 'user' set login = ?, password = ?, role = ?  where id = ?";
    private final String deleteQuery = "delete from 'user' where id = ?";
    private final String dropQuery = "drop table 'user'";
    private final Connection connection;

    public UserDao(Connection con) {
        this.connection = con;
        initTable();
    }

    private void initTable() {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(initializeQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create user table", e);
        }
    }

    @Override
    public User getById(int id) {
        try (final Statement statement = connection.createStatement()) {
            final String sql = String.format(selectByIdQuery, id);
            final ResultSet resultSet = statement.executeQuery(sql);
            User user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    resultSet.getString("role"));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get user", e);
        }
    }


    public User getByLogin(String login) {
        try (final Statement statement = connection.createStatement()) {
            final String sql = String.format(selectByLoginQuery, login);
            final ResultSet resultSet = statement.executeQuery(sql);
            User user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    resultSet.getString("role"));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get user", e);
        }
    }

        @Override
    public List<User> getAll() {
        try (final Statement statement = connection.createStatement()) {

            final String sql = String.format(selectAllQuery);
            final ResultSet resultSet = statement.executeQuery(sql);

            final List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("role")));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get list of users", e);
        }
    }

    @Override
    public int insert(User user) {
        if (user.getId() == 0) {
            return insertWithOutId(user);
        }
        try (final PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            insertStatement.setInt(1, user.getId());
            insertStatement.setString(2, user.getLogin());
            insertStatement.setString(3, user.getPassword());
            insertStatement.setString(4, user.getRole());

            insertStatement.execute();

            return user.getId();
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert user", e);
        }
    }

    public int insertWithOutId(User user) {
        try (final PreparedStatement insertStatement = connection.prepareStatement(insertWithOutIdQuery)) {
            insertStatement.setString(1, user.getLogin());
            insertStatement.setString(2, user.getPassword());
            insertStatement.setString(3, user.getRole());
            insertStatement.execute();

            return user.getId();
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert user", e);
        }
    }

    @Override
    public int update(User user, int id) {
        try (final PreparedStatement preparedStatement =
                     connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole());
            preparedStatement.setInt(4, id);
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

    @Override
    public void drop() {
        try(final Statement statement = connection.createStatement()){
            statement.execute(dropQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Can't drop table", e);
        }
    }
}
