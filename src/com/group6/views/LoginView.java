import java.util.Scanner;

/**
 * Represents the login view where users can enter their NRIC and password to authenticate.
 * This view handles user authentication and redirects to the appropriate home view
 * based on the user's role.
 */
public class LoginView implements View {
    private static final int MAX_ATTEMPTS = 3;
    private final UserAuthenticator authenticator;

    public LoginView(UserAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * Renders the login interface, allowing users to enter their credentials.
     *
     * @param ctx The view context containing dependencies like Scanner.
     * @return The next view based on login outcome (user's home view or main menu).
     */
    @Override
    public View render(ViewContext ctx) {
        Scanner scanner = ctx.getScanner();
        System.out.println("\n--- User Login ---");
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Enter NRIC: ");
            String nric = scanner.nextLine().toUpperCase();

            if (!validateUtils.isValidNRIC(nric)) {
                System.out.println("❌ Invalid NRIC format.");
                attempts++;
                continue;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User user = authenticator.authenticate(nric, password);
            if (user != null) {
                System.out.println("✅ Login successful!");
                return getHomeView(user);
            }

            attempts++;
            System.out.println("❌ Incorrect NRIC or password. Attempts left: " + (MAX_ATTEMPTS - attempts));
        }

        System.out.println("🚫 Too many failed attempts. Returning to main menu.");
        return new MenuView(ctx.getScanner(), authenticator.getUserManager());
    }

    /**
     * Determines and returns the appropriate home view based on user role.
     *
     * @param user The logged-in user.
     * @return A role-specific home view or the main menu as fallback.
     */
    private View getHomeView(User user) {
        return switch (user.getRole()) {
            case "Applicant" -> new ApplicantHomeView(user);
            case "Officer" -> new OfficerHomeView(user);
            case "Manager" -> new ManagerHomeView(user);
            default -> {
                System.out.println("⚠ Unknown user role. Returning to main menu.");
                yield new MenuView(new Scanner(System.in), authenticator.getUserManager());
            }
        };
    }
}
