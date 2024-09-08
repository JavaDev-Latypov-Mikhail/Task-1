package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDaoJDBCImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoJDBCImpl.class);

    public UserDaoJDBCImpl() {
    }

    public void createUsersTable() {
        String createTable = "CREATE TABLE IF NOT EXISTS users ("
                       + "id SERIAL PRIMARY KEY, "
                       + "name VARCHAR(50), "
                       + "last_name VARCHAR(50), "
                       + "age SMALLINT)";
        executeUpdate(createTable);
    }

    public void dropUsersTable() {
        String dropTableUsers = "DROP TABLE IF EXISTS users";
        executeUpdate(dropTableUsers);
    }

    public void saveUser(String name, String lastName, byte age) {
        String save = "INSERT INTO users (name, last_name, age) VALUES (?, ?, ?)";
        try (Connection connection = Util.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(save)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.executeUpdate();
            connection.commit();
            logger.info("User с именем " + name + " добавлен в базу данных");
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении пользователя: ", e);
        }
    }


    public void removeUserById(long id) {
        String removeUser = "DELETE FROM users\n" +
                            "WHERE id = ?";
        try (var connection = Util.getConnection()) {
            connection.setAutoCommit(false);
            try (var preparedStatement = connection.prepareStatement(removeUser)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Ошибка при выполнении запроса: " + removeUser, e);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении пользователя по id: ", e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String getUsers = "SELECT * FROM users";
        try (Connection connection = Util.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(getUsers)) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String lastName = resultSet.getString("last_name");
                Byte age = resultSet.getByte("age");
                User user = new User(name, lastName, age);
                user.setId(id);
                users.add(user);
            }
            logger.info("Все пользователи извлечены из БД");
        } catch (SQLException e) {
            logger.error("Ошибка при извлечении пользователей: ", e);
        }
        return users;
    }


    @Override
    public void cleanUsersTable() {
        String truncateTable = "TRUNCATE TABLE users;";
        executeUpdate(truncateTable);
    }

    public void executeUpdate(String query) {
        try (Connection connection = Util.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            connection.commit();
            logger.info("Выполнен запрос " + query);
        } catch (SQLException e) {
            logger.error("Ошибка при выполнении запроса: " + query, e);
        }
    }
}
