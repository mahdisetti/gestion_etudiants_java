package tekup;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TxtBulletinExporter extends BulletinExporter {

    public TxtBulletinExporter() {
        super("TXT");
    }

    @Override
    public void exporter(Etudiant etudiant) throws IOException {
        String chemin = "bulletin_" + etudiant.getCin() + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(chemin))) {
            pw.print(Bulletin.generer(etudiant));
        }

        System.out.println("Bulletin exporté en TXT : " + chemin);
    }
}