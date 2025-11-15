import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Socket s = new Socket("127.0.0.1", 1234)) {


            OutputStream out = s.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);

            // === LIRE ID CLIENT ET RÉCEPTION DE L'OBJET INPUT ===
            InputStream in = s.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);

            int idClient = ois.readInt();
            System.out.println("ID client reçu : " + idClient);

            Scanner scanner = new Scanner(System.in);
            boolean estConnecte = false;
            boolean estAdmin = false;

            // === AUTHENTIFICATION PAR OBJET USER ===
            while (!estConnecte) {
                System.out.print("Vous êtes admin ou client : ");
                String role = scanner.nextLine().trim();

                System.out.print("Entrez votre mot de passe : ");
                String password = scanner.nextLine();

                // Créer et envoyer l'objet User
                User user = new User(idClient, password, role);
                oos.writeObject(user);
                oos.flush(); // Important !

                // Recevoir réponse du serveur
                boolean authentifie = ois.readBoolean();
                if (authentifie) {
                    estConnecte = true;
                    estAdmin = role.equalsIgnoreCase("admin");
                    System.out.println("Connexion réussie en tant que " + (estAdmin ? "ADMIN" : "CLIENT") + " !");
                } else {
                    System.out.println("Échec de l'authentification. Réessayez.");
                }
            }

            // === MENU APRÈS CONNEXION ===
            while (estConnecte) {
                if (estAdmin) {
                    System.out.println("\n--- MENU ADMIN ---");
                    System.out.println("1. Voir toutes les Mesures");
                    System.out.println("2. Voir toutes les Mesures avec Grandeurs par capteurs");
                    System.out.println("3. Toutes les moyennes par capteurs");
                    System.out.println("4. La moyenne par Grandeur");
                    System.out.println("0. Quitter");
                } else {
                    System.out.println("\n--- MENU CLIENT ---");
                    System.out.println("1. Ajouter un capteur");
                    System.out.println("0. Quitter");
                }
                System.out.print("Choix : ");
                String choix = scanner.nextLine();

                if (estAdmin) {
                    switch (choix) {
                        case "1":
                            oos.writeInt(1); // Demande toutes les mesures
                            oos.flush();
                            String reponse1 = (String) ois.readObject();
                            System.out.println("=== TOUTES LES MESURES ===\n" + reponse1);
                            break;
                        case "2":
                            oos.writeInt(2);
                            oos.flush();

                            System.out.println("Entrez l'Id du capteurs : ");
                            int id = scanner.nextInt();
                            scanner.nextLine();
                            oos.writeInt(id);
                            oos.flush();

                            String reponse2 = (String) ois.readObject();
                            System.out.println("=== MESURES AVEC Capteurs ===\n" + reponse2);
                            break;
                        case "3":
                            oos.writeInt(3);
                            oos.flush();

                            System.out.println("Entrez l'Id du capteurs : ");
                            id = scanner.nextInt();
                            scanner.nextLine();
                            oos.writeInt(id);
                            oos.flush();

                            String reponse3 = (String) ois.readObject();
                            System.out.println("=== MOYENNES GLOBALES par capteur ===\n" + reponse3);
                            break;
                        case "4":
                            System.out.println("Choisissez la grandeur : temperature, humidite ou pression");
                            String grandeur = scanner.nextLine().trim();

                            oos.writeInt(4);
                            oos.flush();

                            oos.writeObject(grandeur);
                            oos.flush();

                            String reponse = (String) ois.readObject();
                            System.out.println("=== Moyenne par grandeur ===\n" + reponse);
                            break; // ← reste dans le menu
                        case "0":
                            System.out.println("Déconnexion...");
                            estConnecte = false;
                            break;
                        default:
                            System.out.println("Choix invalide !");
                            break;
                    }
                } else {
                    switch (choix) {
                        case "1":
                            boolean quitter = false;
                            while (!quitter) {
                                System.out.println("Client " + idClient + " : Ajout d'une mesure");

                                System.out.print("Température : ");
                                double tmp = Double.parseDouble(scanner.nextLine());

                                System.out.print("Humidité : ");
                                double humidite = Double.parseDouble(scanner.nextLine());

                                System.out.print("Pression : ");
                                double pression = Double.parseDouble(scanner.nextLine());

                                // === 1. ENVOYER CODE -1 POUR SIGNALER UNE MESURE ===
                                oos.writeInt(-1);
                                oos.flush();

                                Mesure mesure = new Mesure(idClient, tmp, humidite, pression);
                                oos.writeObject(mesure);
                                oos.flush(); // Envoi immédiat

                                System.out.println("Mesure envoyée au serveur !");
                                Thread.sleep(3000);

                                // Condition pour quitter
                                System.out.println("Voulez-vous continuer à envoyer des mesures ? (o/n)");
                                String reponse = scanner.nextLine();
                                if (reponse.equalsIgnoreCase("n")) {
                                    quitter = true;
                                }
                            }
                            break;

                        case "0":
                            System.out.println("Déconnexion...");
                            estConnecte = false;
                            break;

                        default:
                            System.out.println("Choix invalide !");
                            break;
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Fin de la connexion.");
        }
    }
}