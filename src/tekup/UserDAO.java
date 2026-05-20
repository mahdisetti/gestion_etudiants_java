package tekup;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static boolean register(String username, String password) {
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, hashPassword(password));

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erreur inscription: " + e.getMessage());
            return false;
        }
    }

    public static boolean login(String username, String password) {
        String sql = "SELECT id, password_hash FROM users WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String savedHash = rs.getString("password_hash");
                    String enteredHash = hashPassword(password);

                    if (savedHash.equals(enteredHash)) {
                        Session.login(userId, username.trim());
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur login: " + e.getMessage());
        }

        return false;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur hash password", e);
        }
    }
}