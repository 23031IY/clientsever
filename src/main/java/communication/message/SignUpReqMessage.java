package communication.message;

public class SignUpReqMessage {
    public String type;
    public String id;
    public String pass;

    public SignUpReqMessage(String type, String id, String pass) {
        this.type = type; //SignUp
        this.id = id;
        this.pass = pass;
    }
}
