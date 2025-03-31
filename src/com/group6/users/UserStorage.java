import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
* Load and save user data from files.
*/
public class UserStorage {

    /**
    * Load all users from predefined files and stores them in a map.
    * <p>
    * The method reads user data from the following files:
    * <ul>
    *     <li>{@code applicants.txt} for applicants</li>
    *     <li>{@code officers.txt} for officers</li>
    *     <li>{@code managers.txt} for managers</li>
    * </ul>
    * </p>
    *
    * @return A map containing user NRICS as keys and corresponding {@code User} objects as values.
    */
    public static Map<String, User> loadUsers(){
        Map<String, User> users = new HashMap<>();
        loadUsersFromFile(users, "applicants.txt", UserRole.APPLICANT);
        loadUsersFromFile(users, "officers.txt", UserRole.OFFICER);
        loadUsersFromFile(users, "managers.txt", UserRole.MANAGER);
        return users;
    }

    /**
    * Loads user data from a specified file and assigns them the given role.
    * <p>
    * Reads each line from the file, splits it into user attibutes,
    * and creates a corresponding {@code USer} object before adding it to the map.
    * </p>
    * 
    * @param users    The map where loaded users are stored.
    * @param filename The name of the file containing user data.
    * @param role    The role of users stored in the file.
    */
    public static void loadUsersFromFile(Map<String, User> users, String filename, UserRole role){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] parts = line.split(",");
                if (parts.length == 5){
                    User user = createUserByRole(parts, role);
                    users.put(user.getNric(), user);
                }
            }
        } catch (IOException e){
            System.out.println("Error reading from file " + filename);
        }
    }

    /**
    * Creates a {@code User} object based on the provided role.
    * @param parts The array containing user attributes(name, NRIC, age, maritalStatus, password).
    * @param role The role of the user (APPLICANT, OFFICER, or MANAGER).
    * @return A {@code User} object of the appropriate subclass.
    */
    private static User createUserByRole(String[] parts, UserRole role){
        return switch (role){
            case APPLICANT -> new Applicant(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
            case OFFICER -> new HDBOfficer(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
            case MANAGER -> new HDBManager(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
        };
    }

    /**
    * Saves a user to the corresponding role-based file.
    * <p>
    * The user is appended to one of the following files based on their role:
    * <ul>
    *     <li>{@code applicants.txt} for applicants</li>
    *     <li>{@code officers.txt} for officers</li>
    *     <li>{@code managers.txt} for managers</li>
    * </ul>
    * </p>
    *
    * @param user The {@code User} object to be saved.
    */
    public static void saveUser(User user){
        String filename = switch (user.getRole()){
            case APPLICANT -> "applicants.txt";
            case OFFICER -> "officers.txt";
            case MANAGER -> "managers.txt";
        };

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))){
            writer.write(user.tofileString());
            writer.newLine();
        } catch (Exception e){
            System.out.println("Error writing user to file " + filename);
        }
    }
}
