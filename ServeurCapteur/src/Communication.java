import java.io.*;
import java.net.Socket;
import java.sql.*;

public class Communication extends Thread {
    private static int cpt = 0;
    private int idClient;
    private Socket clientsocket;

    public Communication(Socket socket) {
        this.clientsocket = socket;
        this.idClient = ++cpt;
    }

    public void run() {
        System.out.println("Nouveau client connecté - ID : " + idClient);

        try {
            // === ENVOYER L'ID CLIENT ===
            OutputStream os = clientsocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeInt(idClient);
            oos.flush();

            // === LIRE L'OBJET USER ===
            InputStream is = clientsocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);

            boolean authentifie = false;
            User user = null;

            while (!authentifie) {
                try {
                    user = (User) ois.readObject();
                    System.out.println("Utilisateur reçu : " + user);

                    String role = user.getRole().trim().toLowerCase();
                    String password = user.getPassword();

                    if (role.equals("admin") && password.equals("admin")) {
                        authentifie = true;
                        System.out.println("Connexion ADMIN réussie");
                    } else if (role.equals("client") && password.equals("client")) {
                        authentifie = true;
                        System.out.println("Connexion CLIENT réussie");
                    } else {
                        System.out.println("Mot de passe ou rôle incorrect");
                    }

                    // Envoyer résultat
                    oos.writeBoolean(authentifie);
                    oos.flush();

                } catch (ClassNotFoundException e) {
                    System.out.println("Classe User non trouvée côté serveur");
                    oos.writeBoolean(false);
                    oos.flush();
                }
            }

            // === RÉCEPTION DES MESURES ===
            while (true) {
                try {
                    int req = ois.readInt();
                    switch (req) {
                        case 1:
                            try (Connection conn = DatabaseConnection.connect();) {

                                Statement stmt = conn.createStatement();

                                String sql = "SELECT * FROM \"Cap\" ORDER BY date_mesure DESC LIMIT 10";
                                ResultSet rs = stmt.executeQuery(sql);

                                StringBuilder sb = new StringBuilder();

                                while (rs.next()) {
                                    int idMesure = rs.getInt("idMesure");
                                    int clientId = rs.getInt("client_id");
                                    double temperature = rs.getDouble("temperature");
                                    double humidite = rs.getDouble("humidite");
                                    double pression = rs.getDouble("pression");
                                    String date = rs.getString("date_mesure");

                                    sb.append("ID: ").append(idMesure)
                                            .append(" | Client: ").append(clientId)
                                            .append(" | Temp: ").append(temperature)
                                            .append(" | Hum: ").append(humidite)
                                            .append(" | Pression: ").append(pression)
                                            .append(" | Date: ").append(date)
                                            .append("\n");
                                }

                                oos.writeObject(sb.toString());
                                oos.flush();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case 2:
                            Connection conn = DatabaseConnection.connect();
                            int id = ois.readInt();  // correspond à writeInt(id) côté client
                            try (PreparedStatement pst = conn.prepareStatement(
                                    "SELECT * FROM \"Cap\" WHERE client_id = ?")) {
                                pst.setInt(1, id);

                                try (ResultSet rs = pst.executeQuery()) {
                                    StringBuilder sb = new StringBuilder();
                                    while (rs.next()) {
                                        sb.append("ID: ").append(rs.getInt("idMesure"))
                                                .append(" | Client: ").append(rs.getInt("client_id"))
                                                .append(" | Temp: ").append(rs.getDouble("temperature"))
                                                .append(" | Hum: ").append(rs.getDouble("humidite"))
                                                .append(" | Pression: ").append(rs.getDouble("pression"))
                                                .append(" | Date: ").append(rs.getString("date_mesure"))
                                                .append("\n");
                                    }
                                    oos.writeObject(sb.toString());
                                    oos.flush();
                                }
                            }
                            break;

                        case 3:
                            conn = DatabaseConnection.connect();
                            id = ois.readInt();
                            try (PreparedStatement pst = conn.prepareStatement(
                                    "SELECT AVG(temperature) AS avg_temp, AVG(humidite) AS avg_hum, AVG(pression) AS avg_press FROM \"Cap\" WHERE client_id = ?"
                            )) {
                                pst.setInt(1, id);

                                try (ResultSet rs = pst.executeQuery()) {
                                    StringBuilder sb = new StringBuilder();
                                    while (rs.next()) {
                                        sb.append("Client: ").append(id)
                                                .append(" | Temp Moyenne: ").append(rs.getDouble("avg_temp"))
                                                .append(" | Hum Moyenne: ").append(rs.getDouble("avg_hum"))
                                                .append(" | Pression Moyenne: ").append(rs.getDouble("avg_press"))
                                                .append("\n");
                                    }
                                    oos.writeObject(sb.toString());
                                    oos.flush();
                                }
                            }
                            break;

                        case 4:
                            conn = DatabaseConnection.connect();
                            try {
                                String grandeur = (String) ois.readObject();

                                if (!grandeur.equalsIgnoreCase("temperature") &&
                                        !grandeur.equalsIgnoreCase("humidite") &&
                                        !grandeur.equalsIgnoreCase("pression")) {

                                    oos.writeObject("Grandeur invalide ! Choisissez : temperature, humidite ou pression");
                                    oos.flush();
                                    break; // ← REMPLACE return;
                                }

                                String sql = "SELECT AVG(\"" + grandeur + "\") AS moyenne FROM \"Cap\"";

                                try (PreparedStatement pst = conn.prepareStatement(sql);
                                     ResultSet rs = pst.executeQuery()) {

                                    StringBuilder sb = new StringBuilder();
                                    if (rs.next()) {
                                        double avg = rs.getDouble("moyenne");
                                        sb.append("Moyenne de ").append(grandeur)
                                                .append(" : ").append(String.format("%.2f", avg)).append("\n");
                                    } else {
                                        sb.append("Aucune donnée pour ").append(grandeur).append("\n");
                                    }
                                    oos.writeObject(sb.toString());
                                    oos.flush();
                                }
                            } catch (Exception e) {
                                oos.writeObject("Erreur SQL : " + e.getMessage());
                                oos.flush();
                            } finally {
                                try {
                                    if (conn != null) conn.close();
                                } catch (SQLException ignored) {
                                }
                            }
                            break; // ← reste dans la boucle while(true)

                        default:
                            System.out.println("Choix invalide !");
                            break;
                    }

                    if (req == -1) {

                        Object obj = ois.readObject();
                        if (obj instanceof Mesure mesure) {
                            System.out.println("Mesure reçue : " + mesure);

                            try (Connection conn = DatabaseConnection.connect()) {
                                String sql = "INSERT INTO \"Cap\" (\"idMesure\", \"client_id\", \"temperature\", \"pression\", \"humidite\", \"date_mesure\") VALUES (?,?,?,?,?,?)";
                                PreparedStatement pst = conn.prepareStatement(sql);
                                pst.setInt(1, mesure.getIdMesure());
                                pst.setDouble(2, mesure.getIdClient());
                                pst.setDouble(3, mesure.getTemperature());
                                pst.setDouble(4, mesure.getPression());
                                pst.setDouble(5, mesure.getHummidite());
                                pst.setTimestamp(6, Timestamp.valueOf(mesure.getDate()));
                                pst.executeUpdate();
                                System.out.println("Mesure insérée en base");
                            }
                        }
                    }


                } catch (EOFException e) {
                    System.out.println("Client " + idClient + " déconnecté.");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Client " + idClient + " déconnecté.");
        } finally {
            try {
                clientsocket.close();
            } catch (IOException e) {
            }
        }
    }
}
