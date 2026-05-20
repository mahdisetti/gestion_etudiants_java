package tekup;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class MainSwing {

    public static void main(String[] args) {
        Database.initDatabase();

        SwingUtilities.invokeLater(MainSwing::showLogin);
    }

    // LOGIN SCREEN
    static void showLogin() {
        JFrame frame = new JFrame("TekUp – Connexion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(380, 240);
        frame.setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(5, 2, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField loginField = new JTextField();
        JPasswordField mdpField = new JPasswordField();

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);

        JButton btnLogin = new JButton("Se connecter");
        JButton btnSignup = new JButton("Créer un compte");

        p.add(new JLabel("Login :"));
        p.add(loginField);

        p.add(new JLabel("Mot de passe :"));
        p.add(mdpField);

        p.add(messageLabel);
        p.add(new JLabel());

        p.add(btnLogin);
        p.add(btnSignup);

        Runnable login = () -> {
            String username = loginField.getText().trim();
            String password = new String(mdpField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Login et mot de passe requis.");
                return;
            }

            if (Authentification.authentifier(username, password)) {
                GestionEtudiants.chargerDepuisDatabase();

                frame.dispose();
                showApp();
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Identifiants incorrects.");
                mdpField.setText("");
            }
        };

        btnLogin.addActionListener(e -> login.run());
        mdpField.addActionListener(e -> login.run());

        btnSignup.addActionListener(e -> {
            String username = loginField.getText().trim();
            String password = new String(mdpField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Login et mot de passe requis.");
                return;
            }

            if (password.length() < 4) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Mot de passe trop court.");
                return;
            }

            boolean success = UserDAO.register(username, password);

            if (success) {
                messageLabel.setForeground(new Color(0, 128, 0));
                messageLabel.setText("Compte créé. Connectez-vous.");
                mdpField.setText("");
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Utilisateur existe déjà.");
            }
        });

        frame.setContentPane(p);
        frame.setVisible(true);
    }

    // MAIN WINDOW
    static void showApp() {
        JFrame frame = new JFrame("TekUp – Gestion des Étudiants - " + Session.getCurrentUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 500);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Liste", tabListe());
        tabs.addTab("Ajouter", tabAjouter());
        tabs.addTab("Notes", tabNotes());
        tabs.addTab("Bulletin", tabBulletin());

        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.addActionListener(e -> {
            Session.logout();
            frame.dispose();
            showLogin();
        });

        JPanel top = new JPanel(new BorderLayout());

        JLabel userLabel = new JLabel("Utilisateur connecté : " + Session.getCurrentUsername());
        userLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        top.add(userLabel, BorderLayout.WEST);
        top.add(btnLogout, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(top, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    // TAB 1 – LIST
    static JPanel tabListe() {
        String[] cols = {"CIN", "Nom", "Moyenne", "Mention"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        refreshList(model);

        JButton btnDel = new JButton("Supprimer");
        btnDel.setForeground(Color.RED);

        JButton btnRefresh = new JButton("Actualiser");

        btnRefresh.addActionListener(e -> refreshList(model));

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                msg("Sélectionnez un étudiant.");
                return;
            }

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
        south.add(btnRefresh);
        south.add(btnDel);

        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    static void refreshList(DefaultTableModel model) {
        model.setRowCount(0);

        for (Etudiant e : GestionEtudiants.getListe()) {
            model.addRow(new Object[]{
                    e.getCin(),
                    e.getNom(),
                    String.format("%.2f", e.moyenneGenerale()),
                    e.mention()
            });
        }
    }

    // TAB 2 – ADD STUDENT
    static JPanel tabAjouter() {
        JTextField cin = new JTextField(16);
        JTextField nom = new JTextField(16);

        JLabel msg = new JLabel(" ");
        JButton btn = new JButton("Ajouter");

        btn.addActionListener(e -> {
            String c = cin.getText().trim();
            String n = nom.getText().trim();

            if (c.isEmpty() || n.isEmpty()) {
                err(msg, "CIN et Nom requis.");
                return;
            }

            if (GestionEtudiants.cinExiste(c)) {
                err(msg, "CIN déjà existant pour ce compte.");
                return;
            }

            GestionEtudiants.ajouterEtudiant(new Etudiant(c, n));

            ok(msg, "Étudiant ajouté et sauvegardé !");
            cin.setText("");
            nom.setText("");
        });

        return form(
                new String[]{"CIN :", "Nom :"},
                new JComponent[]{cin, nom},
                btn,
                msg
        );
    }

    // TAB 3 – ADD GRADES
    static JPanel tabNotes() {
        JTextField cin = new JTextField(12);
        JTextField mat = new JTextField(12);
        JTextField n1 = new JTextField(6);
        JTextField n2 = new JTextField(6);

        JLabel msg = new JLabel(" ");
        JButton btn = new JButton("Enregistrer");

        btn.addActionListener(e -> {
            String c = cin.getText().trim();
            String matiere = mat.getText().trim();

            Optional<Etudiant> opt = GestionEtudiants.findByCin(c);

            if (opt.isEmpty()) {
                err(msg, "CIN introuvable pour ce compte.");
                return;
            }

            if (matiere.isEmpty()) {
                err(msg, "Matière requise.");
                return;
            }

            try {
                float v1 = Float.parseFloat(n1.getText().trim());

                float v2;
                if (n2.getText().trim().isEmpty()) {
                    v2 = v1;
                } else {
                    v2 = Float.parseFloat(n2.getText().trim());
                }

                if (v1 < 0 || v1 > 20 || v2 < 0 || v2 > 20) {
                    throw new NumberFormatException();
                }

                GestionEtudiants.ajouterNotes(c, matiere, v1, v2);

                ok(msg, "Notes enregistrées et sauvegardées !");
                mat.setText("");
                n1.setText("");
                n2.setText("");

            } catch (NumberFormatException ex) {
                err(msg, "Notes invalides (0–20).");
            }
        });

        return form(
                new String[]{"CIN :", "Matière :", "Note 1 :", "Note 2 :"},
                new JComponent[]{cin, mat, n1, n2},
                btn,
                msg
        );
    }

    // TAB 4 – BULLETIN
    static JPanel tabBulletin() {
        JTextField cin = new JTextField(14);

        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);

        JLabel msg = new JLabel(" ");

        JButton btnView = new JButton("Voir");
        JButton btnExport = new JButton("Exporter");

        String[] formats = {"TXT", "Console"};
        JComboBox<String> formatBox = new JComboBox<>(formats);

        btnView.addActionListener(e -> {
            Optional<Etudiant> opt = GestionEtudiants.findByCin(cin.getText().trim());

            if (opt.isEmpty()) {
                err(msg, "CIN introuvable pour ce compte.");
                return;
            }

            area.setText(Bulletin.generer(opt.get()));
            msg.setText(" ");
        });

        btnExport.addActionListener(e -> {
            Optional<Etudiant> opt = GestionEtudiants.findByCin(cin.getText().trim());

            if (opt.isEmpty()) {
                err(msg, "CIN introuvable pour ce compte.");
                return;
            }

            try {
                BulletinExporter exporter;

                if ("TXT".equals(formatBox.getSelectedItem())) {
                    exporter = new TxtBulletinExporter();
                } else {
                    exporter = new ConsoleBulletinExporter();
                }

                exporter.exporter(opt.get());

                ok(msg, "Bulletin exporté avec le format : " + exporter.getNomFormat());

            } catch (IOException ex) {
                err(msg, "Erreur : " + ex.getMessage());
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("CIN :"));
        top.add(cin);
        top.add(btnView);

        top.add(new JLabel("Format :"));
        top.add(formatBox);

        top.add(btnExport);

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
            g.gridy = i;
            g.gridx = 0;
            g.gridwidth = 1;
            p.add(new JLabel(labels[i]), g);

            g.gridx = 1;
            p.add(fields[i], g);
        }

        g.gridy = labels.length;
        g.gridx = 0;
        g.gridwidth = 2;
        p.add(msg, g);

        g.gridy++;
        p.add(btn, g);

        return p;
    }

    static void err(JLabel l, String text) {
        l.setForeground(Color.RED);
        l.setText(text);
    }

    static void ok(JLabel l, String text) {
        l.setForeground(new Color(0, 128, 0));
        l.setText(text);
    }

    static void msg(String text) {
        JOptionPane.showMessageDialog(null, text);
    }

    static boolean confirm(String text) {
        return JOptionPane.showConfirmDialog(
                null,
                text,
                "Confirmer",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }
}