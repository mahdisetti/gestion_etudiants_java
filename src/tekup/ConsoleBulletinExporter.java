package tekup;

import java.io.IOException;

public class ConsoleBulletinExporter extends BulletinExporter {

    public ConsoleBulletinExporter() {
        super("Console");
    }

    @Override
    public void exporter(Etudiant etudiant) throws IOException {
        System.out.println(Bulletin.generer(etudiant));
    }
}