package tekup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:students.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        String sqlEtudiants = """
                CREATE TABLE IF NOT EXISTS etudiants (
                    cin TEXT PRIMARY KEY,
                    nom TEXT NOT NULL
                );
                """;

        String sqlNotes = """
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cin TEXT NOT NULL,
                    matiere TEXT NOT NULL,
                    note1 REAL NOT NULL,
                    note2 REAL NOT NULL,
                    FOREIGN KEY (cin) REFERENCES etudiants(cin)
                        ON DELETE CASCADE
                );
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlEtudiants);
            stmt.execute(sqlNotes);

            System.out.println("Database ready.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}