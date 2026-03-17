package configDB;

public class DatabaseConfig {
    private static final String url = "jdbc:mysql://localhost:3306/clinic";
    private static final String user = "clinicUser";
    private static final String password = "clinicUser1234";

    public DatabaseConfig() {
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }


}
