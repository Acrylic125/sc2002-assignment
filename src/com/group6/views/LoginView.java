import java.util.Scanner;

/**
 * Represents the login view where users can enter their NRIC and password to authenticate.
 * This view handles user authentication and redirects to the appropriate home view
 * based on the user's role.
 */
public class LoginView implements View {
    private static final int MAX_ATTEMPTS = 3; // maximum allowed login attempts
    private UserAuthenticator authenticator; // handles user authentication

    public LoginView(UserAuthenticator authenticator) {
        this.authenticator = authenticator;
    }


    /**
     * Renders the login interface, allowing users to enter their credentials.
     *
     * @param ctx The view context containing necessary dependencies (e.g., scanner for input).
     * @return The next view based on the login result (user's home view or main menu).
     */
    @Override
    public View render(ViewContext ctx) {
        Scanner scanner = ctx.getScanner();
        System.out.println("\n--- User Login ---");
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Enter NRIC: ");
            String nric = scanner.nextLine().toUpperCase();

            if (!ValidateNRIC.isValidNRIC(nric)) {
                System.out.println("Invalid NRIC format! Try again.");
                attempts++;
                continue;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User loggedInUser = authenticator.authenticate(nric, password);
            if (loggedInUser != null) {
                System.out.println("Login successful!");
                return getHomeView(loggedInUser);
            }

            attempts++;
            System.out.println("Incorrect NRIC or password. Attempts left: " + (MAX_ATTEMPTS - attempts));
        }

        System.out.println("Too many failed attempts. Returning to main menu.");
        return null;
    }

    /**
     * Determines the appropriate home view based on the logged-in user's role.
     *
     * @param user The authenticated user.
     * @return The home view corresponding to the user's role.
     */
    private View getHomeView(User user) {
        private View getHomeView(User user) {
            return switch (user.getRole()) {
                case "Applicant" -> new ApplicantHomeView();
                case "Officer" -> new OfficerHomeView();
                case "Manager" -> new ManagerHomeView();
                default -> new MainMenuView();
            };
        }

    }
}
