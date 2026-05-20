package tekup;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class GestionEtudiants {

    // Map principale : CIN -> Etudiant
    private static final Map<String, Etudiant> etudiants = new LinkedHashMap<>();

    static {
        Etudiant e1 = new Etudiant("44332211", "Ahmed");
        e1.ajouterNotes("Maths",        14.5f, 12.0f);
        e1.ajouterNotes("Informatique", 16.0f, 15.5f);
        e1.ajouterNotes("Anglais",      11.0f, 13.0f);
        etudiants.put(e1.getCin(), e1);

        Etudiant e2 = new Etudiant("11223344", "Eya");
        e2.ajouterNotes("Maths",        18.0f, 17.5f);
        e2.ajouterNotes("Informatique", 19.0f, 18.0f);
        e2.ajouterNotes("Anglais",      16.5f, 17.0f);
        etudiants.put(e2.getCin(), e2);

        Etudiant e3 = new Etudiant("12345678", "Oussema");
        e3.ajouterNotes("Maths",         9.0f, 10.5f);
        e3.ajouterNotes("Informatique", 11.0f, 12.0f);
        e3.ajouterNotes("Anglais",       8.5f,  9.0f);
        etudiants.put(e3.getCin(), e3);
    }

    public static void ajouterEtudiant(Etudiant e) {
        etudiants.put(e.getCin(), e);
    }

    public static Optional<Etudiant> findByCin(String cin) {
        return Optional.ofNullable(etudiants.get(cin));
    }

    public static boolean cinExiste(String cin) {
        return etudiants.containsKey(cin);
    }

    public static boolean supprimer(String cin) {
        return etudiants.remove(cin) != null;
    }

    public static Collection<Etudiant> getListe() {
        return etudiants.values();
    }
}