import java.util.Map;
import java.util.Scanner;

public class UserManager {
    private Map<String, User> users;

    public UserManager(){
        this.users = UserStorage.loadUsers();
    }

    public Map<String, User> getUsers(){ return users;}

    public void addUser(User user){
        users.put(user.getNric(), user);
        UserStorage.saveUser(user);
        System.out.println("User added successfully.");
    }
}
