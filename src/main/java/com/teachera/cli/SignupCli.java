package com.teachera.cli;

import com.teachera.dao.impl.UserDAOImpl;
import com.teachera.model.User;
import com.teachera.service.ServiceException;
import com.teachera.service.impl.AuthServiceImpl;

import java.io.Console;
import java.util.Optional;
import java.util.Scanner;


public class SignupCli {

    public static void main(String[] args) {
        UserDAOImpl userDao = new UserDAOImpl();
        AuthServiceImpl auth = new AuthServiceImpl(userDao);

        Console console = System.console();
        Scanner scanner = null;
        if (console == null) scanner = new Scanner(System.in);

        String username;
        String password;
        String confirm;

        if (console != null) {
            username = console.readLine("Username: ").trim();
            char[] p1 = console.readPassword("Password: ");
            char[] p2 = console.readPassword("Confirm: ");
            password = new String(p1);
            confirm = new String(p2);
        } else {
            System.out.print("Username: ");
            username = scanner.nextLine().trim();
            System.out.print("Password: ");
            password = scanner.nextLine();
            System.out.print("Confirm: ");
            confirm = scanner.nextLine();
        }

        if (!password.equals(confirm)) {
            System.err.println("Passwords do not match.");
            System.exit(2);
        }

        try {
            Optional<User> created = auth.register(username, password);
            if (created.isPresent()) {
                System.out.println("Registration successful. id=" + created.get().getId());
            } else {
                System.out.println("Registration failed.");
            }
        } catch (ServiceException se) {
            System.err.println("Service error: " + se.getMessage());
            se.printStackTrace(System.err);
            System.exit(3);
        } catch (RuntimeException re) {
            System.err.println("Unexpected error: " + re.getMessage());
            re.printStackTrace(System.err);
            System.exit(4);
        }
    }
}
