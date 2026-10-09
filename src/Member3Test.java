import chain.ApprovalManager;

import model.User;

import proxy.AuthenticationManager;

import java.util.Scanner;

public class Member3Test {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationManager authenticationManager =
                new AuthenticationManager();

        System.out.println(
                "===================================="
        );

        System.out.println(
                "       UNIVERSITY APPROVAL SYSTEM"
        );

        System.out.println(
                "===================================="
        );

        System.out.print(
                "Username: "
        );

        String username =
                scanner.nextLine();

        System.out.print(
                "Password: "
        );

        String password =
                scanner.nextLine();

        User user =
                authenticationManager.login(
                        username,
                        password
                );

        if (user == null) {

            System.out.println(
                    "Login failed."
            );

            scanner.close();
            return;
        }

        String role =
                user.getRole();

        if (!role.equals("FACULTY")
                && !role.equals("HOD")
                && !role.equals("DEAN")) {

            System.out.println(
                    "\nYou do not have approval authority."
            );

            scanner.close();
            return;
        }

        System.out.println(
                "\nLogged in as: "
                        + user.getName()
        );

        System.out.println(
                "Role: " + role
        );

        ApprovalManager manager =
                new ApprovalManager();

        manager.showPendingApprovals(
                username,
                role,
                scanner
        );

        scanner.close();
    }
}