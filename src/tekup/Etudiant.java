package tekup;

import java.util.LinkedHashMap;
import java.util.Map;

public class Etudiant {
    private String cin;
    private String nom;
    private Map<String, float[]> notes; // matière -> notes

    public Etudiant(String cin, String nom) {
        this.cin = cin;
        this.nom = nom;
        this.notes = new LinkedHashMap<>();
    }

    public void ajouterNotes(String matiere, float... vals) {
        notes.put(matiere, vals);
    }

    public float moyenneMatiere(String matiere) {
        float[] vals = notes.get(matiere);
        if (vals == null || vals.length == 0) return 0;
        float sum = 0;
        for (float v : vals) sum += v;
        return sum / vals.length;
    }

    public float moyenneGenerale() {
        if (notes.isEmpty()) return 0;
        float sum = 0;
        for (String m : notes.keySet()) sum += moyenneMatiere(m);
        return sum / notes.size();
    }

    public String mention() {
        float moy = moyenneGenerale();
        if (moy >= 16) return "Très Bien";
        if (moy >= 14) return "Bien";
        if (moy >= 12) return "Assez Bien";
        if (moy >= 10) return "Passable";
        return "Insuffisant";
    }

    public String getCin()  { return cin; }
    public String getNom()  { return nom; }
    public void   setCin(String cin) { this.cin = cin; }
    public void   setNom(String nom) { this.nom = nom; }
    public Map<String, float[]> getNotes() { return notes; }
}