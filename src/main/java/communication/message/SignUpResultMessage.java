package communication.message;

public class SignUpResultMessage {
    public String type;
    public boolean success;      // true / false
    public String errorMessage;  // 失敗時
}
