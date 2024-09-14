package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private static final UserDao INSTANCE = new UserDaoJDBCImpl();

    private static final String CREATE_TABLE_USERS_SQL = """
            CREATE TABLE IF NOT EXISTS users
            (
                id SERIAL PRIMARY KEY,
                name VARCHAR(45) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                age SMALLINT NOT NULL
            )""";

    private static final String DROP_TABLE_USERS_SQL = """
            DROP TABLE IF EXISTS users;
                """;

    private static final String SAVE_USER_SQL = """
            INSERT INTO users(name, last_name, age)
            VALUES (?,?,?);
            """;

    private static final String REMOVE_USER_BY_ID_SQL = """
            DELETE FROM users
            WHERE id = ?
            """;

    private static final String GET_ALL_USERS_SQL = """
            SELECT id,
                    name,
                    last_name,
                    age
            FROM users
            """;

    private static final String CLEAN_USERS_TABLE_SQL = """
            TRUNCATE TABLE users
            """;

    public UserDaoJDBCImpl() {
    }

    public void createUsersTable() {
        try (var connection = Util.get();
             var prepareStatement = connection.prepareStatement(CREATE_TABLE_USERS_SQL)) {
            prepareStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropUsersTable() {
        executeUpdate(DROP_TABLE_USERS_SQL);
    }

    public void saveUser(String name, String lastName, byte age) {
        try (var connection = Util.get()) {
            connection.setAutoCommit(false);
            try (var preparedStatement = connection.prepareStatement(SAVE_USER_SQL)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, lastName);
                preparedStatement.setByte(3, age);
                preparedStatement.executeUpdate();
                connection.commit();
                System.out.println("User с именем – " + name + " добавлен в базу данных");
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error saving user: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }


    public void removeUserById(long id) {
        try (var connection = Util.get()) {
            connection.setAutoCommit(false);
            try (var preparedStatement = connection.prepareStatement(REMOVE_USER_BY_ID_SQL)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (var connection = Util.get();
             var preparedStatement = connection.prepareStatement(GET_ALL_USERS_SQL);
             var resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    public void cleanUsersTable() {
        executeUpdate(CLEAN_USERS_TABLE_SQL);
    }

    private static User buildUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("last_name"),
                resultSet.getByte("age")
        );
    }

    private void executeUpdate(String query) {
        try (var connection = Util.get()) {
            connection.setAutoCommit(false);
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(query);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error executing update: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
