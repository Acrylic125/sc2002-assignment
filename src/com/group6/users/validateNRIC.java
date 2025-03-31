import java.util.regex.Pattern;

public class validateNRIC {
    private static final String nric = "^[ST]\\d{7}[A-Z]$";
    private static final Pattern nric_pattern = Pattern.compile(nric);

    public  static boolean isValidNRIC(String nric){
        return nric_pattern.matcher(nric).matches();
    }
}
