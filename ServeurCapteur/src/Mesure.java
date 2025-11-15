import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;

public class Mesure implements Serializable {

    private int idMesure;
    private int idClient;
    private double temperature;
    private double pression;
    private double Hummidite;
    private LocalDateTime date;

    public Mesure (int idClient, double temperature, double pression, double Hummidite) {
        Random rand = new Random();
        this.idMesure = rand.nextInt(10000);
        this.idClient = idClient;
        this.temperature = temperature;
        this.pression = pression;
        this.Hummidite = Hummidite;
        this.date = LocalDateTime.now();
    }

    public int getIdClient() {
        return idClient;
    }

    public int getIdMesure() {
        return idMesure;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPression() {
        return pression;
    }

    public double getHummidite() {
        return Hummidite;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Mesure{" +
                "idMesure=" + idMesure +
                ", idClient=" + idClient +
                ", temperature=" + temperature +
                ", pression=" + pression +
                ", Hummidite=" + Hummidite +
                ", Date=" + date +
                '}';
    }
}
