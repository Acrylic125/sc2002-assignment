import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    public static Map<String, User> loadUsers(){
        Map<String, User> users = new HashMap<>();
        loadUsersFromFile(users, "applicants.txt", UserRole.APPLICANT);
        loadUsersFromFile(users, "officers.txt", UserRole.OFFICER);
        loadUsersFromFile(users, "managers.txt", UserRole.MANAGER);
        return users;
    }

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

    private static User createUserByRole(String[] parts, UserRole role){
        return switch (role){
            case APPLICANT -> new Applicant(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
            case OFFICER -> new HDBOfficer(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
            case MANAGER -> new HDBManager(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
        };
    }

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
