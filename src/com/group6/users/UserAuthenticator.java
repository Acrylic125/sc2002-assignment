public class UserAuthenticator {
    private UserManager userManager;

    public UserAuthenticator(UserManager userManager) {
        this.userManager = userManager;
    }

    public User authenticate(String nric, String password) {
        if (userManager.isValidLogin(nric, password)) {
            return userManager.getUserByNRIC(nric).orElse(null);
        }
        return null;
    }
}
