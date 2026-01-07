package communication.message;

public class SignUpResultMessage {
    public String type;
    public boolean success;      // true / false
    public String sessionId;     // 成功時
    public String errorMessage;  // 失敗時
}
