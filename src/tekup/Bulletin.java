package tekup;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class Bulletin {

    public static String generer(Etudiant e) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       BULLETIN DE NOTES - TekUp        \n");
        sb.append("========================================\n");
        sb.append(String.format("Nom    : %s%n", e.getNom()));
        sb.append(String.format("CIN    : %s%n", e.getCin()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-18s %s%n", "Matière", "Moyenne"));
        sb.append("----------------------------------------\n");
        for (Map.Entry<String, float[]> entry : e.getNotes().entrySet()) {
            sb.append(String.format("%-18s %.2f / 20%n",
                    entry.getKey(), e.moyenneMatiere(entry.getKey())));
        }
        sb.append("========================================\n");
        sb.append(String.format("Moyenne générale : %.2f / 20%n", e.moyenneGenerale()));
        sb.append(String.format("Mention          : %s%n", e.mention()));
        sb.append("========================================\n");
        return sb.toString();
    }

    public static String exporter(Etudiant e) throws IOException {
        String chemin = "bulletin_" + e.getCin() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(chemin))) {
            pw.print(generer(e));
        }
        return chemin;
    }
}