package tekup;

public class Session {

    private static int currentUserId = -1;
    private static String currentUsername = "";

    public static void login(int userId, String username) {
        currentUserId = userId;
        currentUsername = username;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void logout() {
        currentUserId = -1;
        currentUsername = "";
    }
}