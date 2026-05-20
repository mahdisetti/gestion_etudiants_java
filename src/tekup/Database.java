package tekup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:students.db";

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        return conn;
    }

    public static void initDatabase() {

        String sqlUsers = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL
                );
                """;

        String sqlEtudiants = """
                CREATE TABLE IF NOT EXISTS etudiants (
                    cin TEXT NOT NULL,
                    nom TEXT NOT NULL,
                    user_id INTEGER NOT NULL,
                    PRIMARY KEY (cin, user_id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                        ON DELETE CASCADE
                );
                """;

        String sqlNotes = """
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cin TEXT NOT NULL,
                    matiere TEXT NOT NULL,
                    note1 REAL NOT NULL,
                    note2 REAL NOT NULL,
                    user_id INTEGER NOT NULL,
                    FOREIGN KEY (cin, user_id) REFERENCES etudiants(cin, user_id)
                        ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                        ON DELETE CASCADE
                );
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsers);
            stmt.execute(sqlEtudiants);
            stmt.execute(sqlNotes);

            System.out.println("Database ready.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}