package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoHibernateImpl.class);

    public UserDaoHibernateImpl() {
    }

    @Override
    public void createUsersTable() {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String query = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, " +
                           "name VARCHAR(50), lastname VARCHAR(50), age SMALLINT)";
            session.createNativeQuery(query).executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при создании таблицы пользователей: ", e);
        }
    }

    @Override
    public void dropUsersTable() {
        Transaction transaction = null;
        try (var session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String dropTable = "DROP TABLE IF EXISTS users";
            session.createNativeQuery(dropTable).executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при удалении таблицы пользователей: ", e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(new User(name, lastName, age));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при сохранении пользователя: ", e);
        }
    }

    @Override
    public void removeUserById(long id) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при удалении пользователя: ", e);
            throw e;
        }
    }

    @Override
    public List<User> getAllUsers() {
        Transaction transaction = null;
        List<User> userList = new ArrayList<>();

        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            var fromUser = session.createQuery("FROM users", User.class);
            userList = fromUser.getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при получении всех пользователей: ", e);
            throw e;
        }
        return userList;
    }

    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM users").executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при очистке таблицы пользователей: ", e);
            throw e;
        }
    }
}
