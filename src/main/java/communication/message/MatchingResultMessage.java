package communication.message;

public class MatchingResultMessage {
    public String type;        // "MATCH_STATUS"
    public boolean success;
    public String errorMessage;  //ステータスメッセージ
}
