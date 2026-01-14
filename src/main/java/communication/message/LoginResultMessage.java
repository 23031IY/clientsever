package communication.message;

public class LoginResultMessage {
    public String type;          // "LOGIN_SUCCES" or "LOGIN_FAILURE"
    public boolean result;      // true / false
    public String message;  // 失敗時
    public String userName;

}