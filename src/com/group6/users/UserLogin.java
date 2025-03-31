import java.util.Map;
import java.util.Scanner;

public class UserLogin {
    private Map<String, User> users;
    private static final int MAX_ATTEMPTS = 3;

    public UserLogin(Map<String, User> users){this.users = users;}

    public User login(Scanner in){
        System.out.println("\n---User Login---");
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS){
            System.out.print("Enter NRIC: ");
            String nric = in.nextLine().toUpperCase();

            System.out.print("Enter password: ");
            String password = in.nextLine();

            User user = users.get(nric);
            if (user != null && user.getPassword().equals(password)){
                System.out.println("Login successful!");
                return user;
            }

            attempts++;
            System.out.println("Incorrect NRIC or password. Attempts left: " + (MAX_ATTEMPTS - attempts));
        }


        System.out.println("Too many attempts. Exiting now.");
        return null;
    }
}

