package view;

import java.util.Scanner;

/** Single terminal I/O boundary for the interactive MVC application.
 *  All messages and user input remain unchanged from the original program.
 *  The Scanner is shared by the application and passed to workflows; never close
 *  it in an individual menu or service.
 */
public final class ConsoleView {
    private ConsoleView() { }
    public static void print(String message) { System.out.print(message); }
    public static void println(String message) { System.out.println(message); }
    public static String readLine(Scanner scanner) { return scanner.nextLine(); }
}
