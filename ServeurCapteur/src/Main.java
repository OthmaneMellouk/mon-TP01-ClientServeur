import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class Main {
    private static final int PORT = 1234;

    public static void main(String[] args) {

        try {
            Connection conn = DatabaseConnection.connect();
            System.out.println("Connexion à la base de données établie avec succès !");
            conn.close();
            ServerSocket server = new ServerSocket(PORT);

            while (true) {
                Socket client = server.accept();
                new Communication(client).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.err.println("Échec de la connexion à la base de données : " + e.getMessage());
        }

    }
}