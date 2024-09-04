package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        UserService userService = new UserServiceImpl();

        userService.createUsersTable();

        userService.saveUser("Bill", "Geits", (byte) 55);
        userService.saveUser("Yuri", "Nikulin", (byte) 40);
        userService.saveUser("Anton", "Chekhov", (byte) 25);
        userService.saveUser("Yuri", "Gagarin", (byte) 30);

        userService.removeUserById(2);

        userService.getAllUsers().forEach(System.out::println);
        userService.cleanUsersTable();
        userService.dropUsersTable();
    }
}
