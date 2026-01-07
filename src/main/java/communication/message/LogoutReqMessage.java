package communication.message;

public class LogoutReqMessage {
    public String type;  //    LOGOUT_SUCCES or

    public LogoutReqMessage() {
        this.type = "LOGOUT";
    }
}
