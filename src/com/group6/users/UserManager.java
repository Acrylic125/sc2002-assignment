import java.util.Map;
import java.util.Optional;

public class UserManager {
    private Map<String, User> users;

    public UserManager() {
        this.users = UserStorage.loadUsers();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Finds a user by NRIC.
     * @param nric The NRIC to look up.
     * @return Optional<User> if found, empty otherwise.
     */
    public Optional<User> getUserByNRIC(String nric) {
        return Optional.ofNullable(users.get(nric));
    }

    /**
     * Validates if the given NRIC and password match a user.
     * @param nric The NRIC entered.
     * @param password The password entered.
     * @return true if login is valid, false otherwise.
     */
    public boolean isValidLogin(String nric, String password) {
        return getUserByNRIC(nric)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }
}
