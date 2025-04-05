import java.util.Map;
import java.util.Optional;

/**
 * Manages user authentication and retrieval.
 * <p>
 * This class is responsible for loading, storing, and managing users.
 * It interacts with {@code UserStorage} to retrieve and validate users.
 * </p>
 */
public class UserManager {
    private final Map<String, User> users;
    private final UserStorage userStorage; // Inject UserStorage instance

    /**
     * Constructs a UserManager instance with a given UserStorage.
     * <p>
     * This constructor initializes the user map by loading users from storage.
     * </p>
     *
     * @param userStorage The storage handler responsible for user data retrieval.
     */
    public UserManager(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.users = userStorage.loadAllUsers(); // Load users from files
    }

    /**
     * Retrieves all users currently managed by the system.
     *
     * @return A map of users keyed by their NRIC.
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Finds a user by NRIC.
     * <p>
     * This method searches for a user based on their unique NRIC identifier.
     * </p>
     *
     * @param nric The NRIC to look up.
     * @return An {@code Optional<User>} containing the user if found, or empty otherwise.
     */
    public Optional<User> getUserByNRIC(String nric) {
        return Optional.ofNullable(users.get(nric));
    }

    /**
     * Validates if the given NRIC and password match a registered user.
     * <p>
     * This method checks if the provided credentials match an existing user
     * stored in the system.
     * </p>
     *
     * @param nric     The NRIC entered by the user.
     * @param password The password entered by the user.
     * @return {@code true} if the credentials are valid, {@code false} otherwise.
     */
    public boolean isValidLogin(String nric, String password) {
        return getUserByNRIC(nric)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    /**
     * Registers a new user and saves it to storage.
     *
     * @param user The user to register.
     * @return true if registration is successful, false if NRIC is already taken.
     */
    public boolean registerUser(User user) {
        if (users.containsKey(user.getNric())) {
            return false; // NRIC already exists
        }
        users.put(user.getNric(), user);
        userStorage.saveUser(user);
        return true;
    }

}
