import java.util.Scanner;
import com.group6.views.View;
import com.group6.utils.validateNRIC;

public class LoginView implements View {
    private static final int MAX_ATTEMPTS = 3;
    private UserAuthenticator authenticator;

    public LoginView(UserAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

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
        return new MainMenuView();
    }

    private View getHomeView(User user) {
        if (user instanceof Applicant) {
            return new ApplicantHomeView();
        } else if (user instanceof HDBOfficer) {
            return new OfficerHomeView();
        } else if (user instanceof HDBManager) {
            return new ManagerHomeView();
        }
        return new MainMenuView();
    }
}
