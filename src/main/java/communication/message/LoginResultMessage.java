package communication.message;

public class LoginResultMessage {
    public String type;          // "LOGIN_SUCCES" or "LOGIN_FAILURE"
    public boolean success;      // true / false
    public String errorMessage;  // 失敗時
}