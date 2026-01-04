package communication.message;

public class MatchingResultMessage {
    public String type;        // "match_result"
    public boolean success;
    public int enemyId;        // 成功時
    public String errorMessage;
}
