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
     * It will ask the user to input their role, personal details, and then create the appropriate user.
     * After successful registration, it returns to the main menu.
     *
     * @return A MenuView instance, which represents the main menu after registration.
     */
    public View run() {
        System.out.println("\n--- User Registration ---");

        // 1. Choose role
        UserRole role = promptRole();

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
        User newUser = switch (role) {
            case APPLICANT -> new Applicant(userId, nric, age, maritalStatus, password);
            case OFFICER -> new HDBOfficer(userId, nric, age, maritalStatus, password);
            case MANAGER -> new HDBManager(userId, nric, age, maritalStatus, password);
        };

        // 4. Save
        userManager.registerUser(newUser);

        System.out.println("✅ Registration successful as " + role + "! You can now log in.");
        return new MenuView(scanner, userManager);
    }

    /**
     * Prompts the user to select a role for registration.
     *
     * @return The UserRole selected by the user (either Applicant, Officer, or Manager).
     */
    private UserRole promptRole() {
        while (true) {
            System.out.println("Select role to register as:");
            System.out.println("1. Applicant");
            System.out.println("2. HDB Officer");
            System.out.println("3. HDB Manager");
            System.out.print("Enter choice (1-3): ");
            String choice = scanner.nextLine();

            return switch (choice) {
                case "1" -> UserRole.APPLICANT;
                case "2" -> UserRole.OFFICER;
                case "3" -> UserRole.MANAGER;
                default -> {
                    System.out.println("❌ Invalid choice.");
                    yield null;
                }
            };
        }
    }

    /**
     * Prompts the user with a message and reads their input.
     *
     * @param message The prompt message to display to the user.
     * @return The input entered by the user as a String.
     */
    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}
