import java.util.Map;
import java.util.Scanner;

/**
 * User manager to manage/access users for the {@link com.group6.BTOSystem}
 *
 * See {@link User}.
 * See {@link com.group6.BTOSystem}.
 */
public class UserManager {
    // Map<User Id, User>
    private Map<String, User> users;

     /**
     * Constructor for UserManager.
     */
    public UserManager(){
        this.users = UserStorage.loadUsers();
    }

    /**
     * Users getter.
     *
     * @return {@link #users}
     */
    public Map<String, User> getUsers(){ return users;}

    /**
     * Add a user to the manager.
     *
     * @param user user to be added.
     */
    public void addUser(User user){
        users.put(user.getNric(), user);
        UserStorage.saveUser(user);
        System.out.println("User added successfully.");
    }
}
