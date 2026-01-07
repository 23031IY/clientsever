package communication.message;

public class LogoutReqMessage {
    private String sessionId;

    public LogoutReqMessage(String sessionId) {
        this.sessionId = sessionId;
    }
}
