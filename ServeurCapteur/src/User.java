import java.io.Serializable;

public class User implements Serializable {

    private int clientId;
    private String password;
    private String role;

    public User(int clientId, String password, String role) {
        this.clientId = clientId;
        this.password = password;
        this.role = role;
    }

    // Getters et Setters
    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "clientId=" + clientId +
                ", role='" + role + '\'' +
                '}';
    }
}
