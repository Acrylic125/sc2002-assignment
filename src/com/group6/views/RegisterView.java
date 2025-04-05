import java.util.Scanner;
import java.util.UUID;

/**
 * Handles the user registration process for Applicants, Officers, and Managers.
 */
public class RegisterView implements View {
    private final Scanner scanner;
    private final UserManager userManager;

    /**
     * Constructs a RegisterView with the specified scanner and userManager.
     *
     * @param scanner The scanner used for reading user input.
     * @param userManager The UserManager responsible for handling user data and registration.
     */
    public RegisterView(Scanner scanner, UserManager userManager) {
        this.scanner = scanner;
        this.userManager = userManager;
    }

    /**
     * Runs the registration process, prompting the user for necessary details and creating the user.
     * It will ask the user to input their personal details, and then create the appropriate user.
     * After successful registration, it returns to the main menu.
     *
     * @return A MenuView instance, which represents the main menu after registration.
     */
    public View run() {
        System.out.println("\n--- User Registration ---");

        // 1. Choose role
        UserRole role = UserRole.APPLICANT;

        // 2. Common inputs
        String name = prompt("Enter full name: ");

        String nric;
        while (true) {
            nric = prompt("Enter NRIC (e.g. S1234567A): ").toUpperCase();
            if (!validateUtils.isValidNRIC(nric)) {
                System.out.println("❌ Invalid NRIC format.");
            } else if (userManager.getUserByNRIC(nric).isPresent()) {
                System.out.println("❌ NRIC already registered.");
            } else {
                break;
            }
        }

        int age;
        while (true) {
            try {
                age = Integer.parseInt(prompt("Enter age: "));
                if (age < 18) {
                    System.out.println("❌ Age must be 18 or above.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }

        UserMaritalStatus maritalStatus;
        while (true) {
            System.out.print("Enter marital status (Single/Married): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            if (statusInput.equals("SINGLE")) {
                maritalStatus = UserMaritalStatus.SINGLE;
                break;
            } else if (statusInput.equals("MARRIED")) {
                maritalStatus = UserMaritalStatus.MARRIED;
                break;
            } else {
                System.out.println("❌ Invalid input. Please enter 'Single' or 'Married'.");
            }
        }

        String password = prompt("Create password: ");
        String userId = UUID.randomUUID().toString();

        // 3. Create appropriate user
        User newUser = new Applicant(userId, nric, age, maritalStatus, password);

        // 4. Save
        userManager.registerUser(newUser);

        System.out.println("✅ Registration successful as applicant! You can now log in.");
        return new MenuView(scanner, userManager);
    }


    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}
