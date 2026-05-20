package tekup;

public class Authentification {

    public static boolean authentifier(String login, String mdp) {
        if (login == null || mdp == null) {
            return false;
        }

        return UserDAO.login(login, mdp);
    }
}