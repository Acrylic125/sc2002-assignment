import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the storage and retrieval of user data from text files.
 * <p>
 * This class loads users from role-specific files and provides methods
 * to save user data persistently. It supports Applicants, Officers, and Managers.
 * </p>
 */
public class UserStorage {
    private final String applicantsFilepath;
    private final String officersFilepath;
    private final String managersFilepath;

    /**
     * Constructs a UserStorage instance with specified file paths.
     *
     * @param applicantsFilepath The file path for storing applicant data.
     * @param officersFilepath   The file path for storing officer data.
     * @param managersFilepath   The file path for storing manager data.
     */
    public UserStorage(String applicantsFilepath, String officersFilepath, String managersFilepath) {
        this.applicantsFilepath = applicantsFilepath;
        this.officersFilepath = officersFilepath;
        this.managersFilepath = managersFilepath;
    }

    /**
     * Loads all users from the respective files.
     *
     * @return A map of users keyed by NRIC.
     */
    public Map<String, User> loadAllUsers() {
        Map<String, User> users = new HashMap<>();
        loadUsersFromFile(users, applicantsFilepath, UserRole.APPLICANT);
        loadUsersFromFile(users, officersFilepath, UserRole.OFFICER);
        loadUsersFromFile(users, managersFilepath, UserRole.MANAGER);
        return users;
    }

    /**
     * Reads user data from the specified file and adds them to the provided map.
     *
     * @param users    The map where loaded users will be stored.
     * @param filename The file from which users will be loaded.
     * @param role     The role of the users being loaded (Applicant, Officer, or Manager).
     */
    private void loadUsersFromFile(Map<String, User> users, String filename, UserRole role) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 5) {
                    System.out.printf("⚠️ Skipping malformed line %d in %s: Incorrect number of fields%n", lineNumber, filename);
                    lineNumber++;
                    continue;
                }

                try {
                    String name = parts[0].trim();
                    String nric = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    UserMaritalStatus maritalStatus = UserMaritalStatus.valueOf(parts[3].trim().toUpperCase());
                    String password = parts[4].trim();

                    User user = createUserByRole(name, nric, age, maritalStatus, password, role);
                    users.put(nric, user);
                } catch (IllegalArgumentException e) {
                    System.out.printf("⚠️ Skipping line %d in %s: Invalid data - %s%n", lineNumber, filename, e.getMessage());
                }

                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading from file: " + filename);
        }
    }

    /**
     * Creates a User object based on the given role and attributes.
     *
     * @param name          Name of the user.
     * @param nric          NRIC of the user.
     * @param age           Age of the user.
     * @param maritalStatus Marital status as an enum.
     * @param password      Password of the user.
     * @param role          User role (APPLICANT, OFFICER, MANAGER).
     * @return Corresponding User object.
     */
    private static User createUserByRole(String name, String nric, int age, UserMaritalStatus maritalStatus, String password, UserRole role) {
        return switch (role) {
            case APPLICANT -> new Applicant(name, nric, age, maritalStatus, password);
            case OFFICER -> new HDBOfficer(name, nric, age, maritalStatus, password);
            case MANAGER -> new HDBManager(name, nric, age, maritalStatus, password);
        };
    }

    /**
     * Saves all users to their respective files.
     *
     * @param users The map of users to be saved, keyed by NRIC.
     */
    public void saveAllUsers(Map<String, User> users) {
        saveUsersToFile(users, applicantsFilepath, UserRole.APPLICANT);
        saveUsersToFile(users, officersFilepath, UserRole.OFFICER);
        saveUsersToFile(users, managersFilepath, UserRole.MANAGER);
    }

    /**
     * Saves users to the specified file based on their role.
     *
     * @param users    The map of users to be saved.
     * @param filename The file where the user data should be stored.
     * @param role     The role of the users being saved (Applicant, Officer, or Manager).
     */
    private void saveUsersToFile(Map<String, User> users, String filename, UserRole role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for (User user : users.values()) {
                if (user.getRole().equals(role)) {
                    writer.write(user.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + filename);
        }
    }

    /**
     * Saves a single user to the appropriate role-specific file.
     *
     * @param user The user to save.
     */
    public void saveUser(User user) {
        String filename = switch (user.getRole()) {
            case APPLICANT -> applicantsFilepath;
            case OFFICER -> officersFilepath;
            case MANAGER -> managersFilepath;
        };

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(user.toFileString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + filename);
        }
    }
}
