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
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    User user = createUserByRole(parts, role);
                    users.put(user.getNric(), user);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + filename);
        }
    }

    /**
     * Creates a User object based on the given role and user data.
     *
     * @param parts The array containing user attributes (name, NRIC, age, marital status, password).
     * @param role  The role of the user (Applicant, Officer, or Manager).
     * @return A new User object corresponding to the provided role.
     */
    private static User createUserByRole(String[] parts, UserRole role) {
        return switch (role) {
            case APPLICANT -> new Applicant(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
            case OFFICER -> new HDBOfficer(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
            case MANAGER -> new HDBManager(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
        };
    }

    /**
     * Saves all users to their respective files.
     * <p>
     * This method overwrites the existing data files with the updated list of users.
     * </p>
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
     * <p>
     * This method overwrites the file with the latest user data.
     * </p>
     *
     * @param users    The map of users to be saved.
     * @param filename The file where the user data should be stored.
     * @param role     The role of the users being saved (Applicant, Officer, or Manager).
     */
    private void saveUsersToFile(Map<String, User> users, String filename, UserRole role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) { // Overwrite
            for (User user : users.values()) {
                if (user.getRole().equals(role.toString())) {
                    writer.write(user.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
        }
    }

}
