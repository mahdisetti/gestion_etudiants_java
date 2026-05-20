package tekup;

import java.io.IOException;

public abstract class BulletinExporter {

    protected String nomFormat;

    public BulletinExporter(String nomFormat) {
        this.nomFormat = nomFormat;
    }

    public String getNomFormat() {
        return nomFormat;
    }

    public abstract void exporter(Etudiant etudiant) throws IOException;
}