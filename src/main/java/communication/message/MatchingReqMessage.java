package communication.message;

public class MatchingReqMessage {
    public String type;
    public String id; //ユーザー名


    public MatchingReqMessage() {
        this.type = "MATCHING";
    }
}
