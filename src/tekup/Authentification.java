package tekup;

public class Authentification {

    private static final String LOGIN = "admin";
    private static final String MOT_DE_PASSE = "admin";

    public static boolean authentifier(String login, String mdp) {
        if (login == null || mdp == null) return false;
        return LOGIN.equals(login.trim()) && MOT_DE_PASSE.equals(mdp);
    }
}