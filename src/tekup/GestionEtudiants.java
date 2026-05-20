package tekup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class GestionEtudiants {

    private static final Map<String, Etudiant> etudiants = new LinkedHashMap<>();

    public static void chargerDepuisDatabase() {
        etudiants.clear();

        String sqlEtudiants = "SELECT cin, nom FROM etudiants";
        String sqlNotes = "SELECT cin, matiere, note1, note2 FROM notes";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sqlEtudiants);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String cin = rs.getString("cin");
                String nom = rs.getString("nom");

                Etudiant e = new Etudiant(cin, nom);
                etudiants.put(cin, e);
            }

        } catch (SQLException e) {
            System.out.println("Erreur chargement etudiants: " + e.getMessage());
        }

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sqlNotes);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String cin = rs.getString("cin");
                String matiere = rs.getString("matiere");
                float note1 = rs.getFloat("note1");
                float note2 = rs.getFloat("note2");

                Etudiant e = etudiants.get(cin);
                if (e != null) {
                    e.ajouterNotes(matiere, note1, note2);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur chargement notes: " + e.getMessage());
        }
    }

    public static void ajouterEtudiant(Etudiant e) {
        String sql = "INSERT INTO etudiants(cin, nom) VALUES(?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getCin());
            ps.setString(2, e.getNom());
            ps.executeUpdate();

            etudiants.put(e.getCin(), e);

        } catch (SQLException ex) {
            System.out.println("Erreur ajout etudiant: " + ex.getMessage());
        }
    }

    public static void ajouterNotes(String cin, String matiere, float note1, float note2) {
        String sql = "INSERT INTO notes(cin, matiere, note1, note2) VALUES(?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cin);
            ps.setString(2, matiere);
            ps.setFloat(3, note1);
            ps.setFloat(4, note2);
            ps.executeUpdate();

            Etudiant e = etudiants.get(cin);
            if (e != null) {
                e.ajouterNotes(matiere, note1, note2);
            }

        } catch (SQLException ex) {
            System.out.println("Erreur ajout notes: " + ex.getMessage());
        }
    }

    public static Optional<Etudiant> findByCin(String cin) {
        return Optional.ofNullable(etudiants.get(cin));
    }

    public static boolean cinExiste(String cin) {
        return etudiants.containsKey(cin);
    }

    public static boolean supprimer(String cin) {
        String sql = "DELETE FROM etudiants WHERE cin = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cin);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                etudiants.remove(cin);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Erreur suppression: " + e.getMessage());
        }

        return false;
    }

    public static Collection<Etudiant> getListe() {
        return etudiants.values();
    }
}