package communication.message;

public class SignUpReqMessage {
    public String type;
    public String id;
    public String pass;

    public SignUpReqMessage(String id, String pass) {
        this.type = "RESISTER";
        this.id = id;
        this.pass = pass;
    }
}
