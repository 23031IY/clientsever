package communication.message;

public class LoginInfoMessage {
    public int id;
    public String password;

    public LoginInfoMessage(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
