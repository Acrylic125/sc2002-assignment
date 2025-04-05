/**
 * Handles the authentication process for users by validating their NRIC and password.
 * It interacts with the UserManager to verify login credentials and retrieve user data.
 */
public class UserAuthenticator {
    private UserManager userManager;

    /**
     * Constructs a UserAuthenticator with the given UserManager.
     *
     * @param userManager The UserManager responsible for managing user data and validation.
     */
    public UserAuthenticator(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Gets the UserManager associated with this authenticator.
     *
     * @return The UserManager instance that this authenticator uses.
     */
    public UserManager getUserManager(){return userManager;}

    /**
     * Authenticates a user by checking their NRIC and password.
     * If the credentials are valid, it returns the corresponding User object.
     *
     * @param nric The NRIC (National Registration Identity Card) of the user attempting to log in.
     * @param password The password of the user attempting to log in.
     * @return A User object if the authentication is successful, or null if the authentication fails.
     */
    public User authenticate(String nric, String password) {
        if (userManager.isValidLogin(nric, password)) {
            return userManager.getUserByNRIC(nric).orElse(null);
        }
        return null;
    }
}
