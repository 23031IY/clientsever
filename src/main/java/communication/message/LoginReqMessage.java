package communication.message;

public class LoginReqMessage {
    public String type;
    public String id;
    public String password;

    public LoginReqMessage(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
