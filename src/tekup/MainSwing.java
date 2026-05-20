package tekup;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class MainSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainSwing::showLogin);
    }

    // LOGIN SCREEN
    static void showLogin() {
        JFrame frame = new JFrame("TekUp – Connexion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 180);
        frame.setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(4, 2, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField loginField = new JTextField();
        JPasswordField mdpField = new JPasswordField();
        JLabel errLabel = new JLabel(" ");
        errLabel.setForeground(Color.RED);
        JButton btn = new JButton("Se connecter");

        p.add(new JLabel("Login :")); p.add(loginField);
        p.add(new JLabel("Mot de passe :")); p.add(mdpField);
        p.add(errLabel); p.add(new JLabel());
        p.add(new JLabel()); p.add(btn);

        Runnable login = () -> {
            if (Authentification.authentifier(loginField.getText().trim(), new String(mdpField.getPassword()))) {
                frame.dispose();
                showApp();
            } else {
                errLabel.setText("Identifiants incorrects.");
                mdpField.setText("");
            }
        };
        btn.addActionListener(e -> login.run());
        mdpField.addActionListener(e -> login.run());

        frame.setContentPane(p);
        frame.setVisible(true);
    }

    // MAIN WINDOW
    static void showApp() {
        JFrame frame = new JFrame("TekUp – Gestion des Étudiants");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 480);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Liste",    tabListe());
        tabs.addTab("Ajouter", tabAjouter());
        tabs.addTab("Notes",   tabNotes());
        tabs.addTab("Bulletin", tabBulletin());

        frame.setContentPane(tabs);
        frame.setVisible(true);
    }

    // TAB 1 – LIST
    static JPanel tabListe() {
        String[] cols = {"CIN", "Nom", "Moyenne", "Mention"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        refreshList(model);

        JButton btnDel = new JButton("Supprimer");
        btnDel.setForeground(Color.RED);
        JButton btnRefresh = new JButton("Actualiser");

        btnRefresh.addActionListener(e -> refreshList(model));
        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { msg("Sélectionnez un étudiant."); return; }
            String cin = (String) model.getValueAt(row, 0);
            if (confirm("Supprimer " + cin + " ?")) {
                GestionEtudiants.supprimer(cin);
                refreshList(model);
            }
        });

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(btnRefresh); south.add(btnDel);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    static void refreshList(DefaultTableModel model) {
        model.setRowCount(0);
        for (Etudiant e : GestionEtudiants.getListe())
            model.addRow(new Object[]{e.getCin(), e.getNom(),
                    String.format("%.2f", e.moyenneGenerale()), e.mention()});
    }

    // TAB 2 – ADD STUDENT
    static JPanel tabAjouter() {
        JTextField cin = new JTextField(16), nom = new JTextField(16);
        JLabel msg = new JLabel(" ");
        JButton btn = new JButton("Ajouter");

        btn.addActionListener(e -> {
            String c = cin.getText().trim(), n = nom.getText().trim();
            if (c.isEmpty() || n.isEmpty()) { err(msg, "CIN et Nom requis."); return; }
            if (GestionEtudiants.cinExiste(c)) { err(msg, "CIN déjà existant."); return; }
            GestionEtudiants.ajouterEtudiant(new Etudiant(c, n));
            ok(msg, "Ajouté !");
            cin.setText(""); nom.setText("");
        });

        return form(new String[]{"CIN :", "Nom :"}, new JComponent[]{cin, nom}, btn, msg);
    }

    // TAB 3 – ADD GRADES
    static JPanel tabNotes() {
        JTextField cin = new JTextField(12), mat = new JTextField(12);
        JTextField n1 = new JTextField(6), n2 = new JTextField(6);
        JLabel msg = new JLabel(" ");
        JButton btn = new JButton("Enregistrer");

        btn.addActionListener(e -> {
            Optional<Etudiant> opt = GestionEtudiants.findByCin(cin.getText().trim());
            if (opt.isEmpty()) { err(msg, "CIN introuvable."); return; }
            if (mat.getText().trim().isEmpty()) { err(msg, "Matière requise."); return; }
            try {
                float v1 = Float.parseFloat(n1.getText().trim());
                float v2 = n2.getText().trim().isEmpty() ? v1 : Float.parseFloat(n2.getText().trim());
                if (v1 < 0 || v1 > 20 || v2 < 0 || v2 > 20) throw new NumberFormatException();
                opt.get().ajouterNotes(mat.getText().trim(), v1, v2);
                ok(msg, "Notes enregistrées !");
                mat.setText(""); n1.setText(""); n2.setText("");
            } catch (NumberFormatException ex) { err(msg, "Notes invalides (0–20)."); }
        });

        return form(new String[]{"CIN :", "Matière :", "Note 1 :", "Note 2 :"},
                new JComponent[]{cin, mat, n1, n2}, btn, msg);
    }

    // TAB 4 – BULLETIN
    static JPanel tabBulletin() {
        JTextField cin = new JTextField(14);
        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        JLabel msg = new JLabel(" ");

        JButton btnView = new JButton("Voir");
        JButton btnExport = new JButton("Exporter (.txt)");

        btnView.addActionListener(e -> {
            Optional<Etudiant> opt = GestionEtudiants.findByCin(cin.getText().trim());
            if (opt.isEmpty()) { err(msg, "CIN introuvable."); return; }
            area.setText(Bulletin.generer(opt.get())); msg.setText(" ");
        });
        btnExport.addActionListener(e -> {
            Optional<Etudiant> opt = GestionEtudiants.findByCin(cin.getText().trim());
            if (opt.isEmpty()) { err(msg, "CIN introuvable."); return; }
            try { ok(msg, "Exporté : " + Bulletin.exporter(opt.get())); }
            catch (IOException ex) { err(msg, "Erreur : " + ex.getMessage()); }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("CIN :")); top.add(cin);
        top.add(btnView); top.add(btnExport);

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        panel.add(msg, BorderLayout.SOUTH);
        return panel;
    }

    // HELPERS
    static JPanel form(String[] labels, JComponent[] fields, JButton btn, JLabel msg) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            g.gridy = i; g.gridx = 0; g.gridwidth = 1; p.add(new JLabel(labels[i]), g);
            g.gridx = 1; p.add(fields[i], g);
        }
        g.gridy = labels.length; g.gridx = 0; g.gridwidth = 2; p.add(msg, g);
        g.gridy++; p.add(btn, g);
        return p;
    }

    static void err(JLabel l, String text) { l.setForeground(Color.RED); l.setText(text); }
    static void ok(JLabel l, String text)  { l.setForeground(new Color(0, 128, 0)); l.setText(text); }
    static void msg(String text)           { JOptionPane.showMessageDialog(null, text); }
    static boolean confirm(String text)    { return JOptionPane.showConfirmDialog(null, text, "Confirmer", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; }
}