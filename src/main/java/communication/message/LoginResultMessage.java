package communication.message;

public class LoginResultMessage {
    public String type;          // "login_result"
    public boolean success;      // true / false
    public String sessionId;     // 成功時
    public String errorMessage;  // 失敗時
}