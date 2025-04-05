import java.util.Scanner;

/**
 * Represents the initial authentication menu.
 * Allows users to login, register, or exit the application.
 */
public class MenuView implements View {
    private final Scanner scanner;
    private final UserManager userManager;

    public MenuView(Scanner scanner, UserManager userManager) {
        this.scanner = scanner;
        this.userManager = userManager;
    }

    public View render(ViewContext ctx) {
        while (true) {
            System.out.println("\n=== Welcome to HDB System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return new LoginView(new UserAuthenticator(userManager));
                case "2":
                    return new RegisterView(scanner, userManager);
                case "3":
                    System.out.println("👋 Goodbye!");
                    return null;
                default:
                    System.out.println("❌ Invalid option. Please choose 1, 2, or 3.");
            }
        }
    }
}
