package communication.message;

import java.util.List;

public class AppMessage {

    public enum Type {
        HELLO,
        START,
        BET,

        ROLL,       // 1回目を振る
        REROLL,     // 振り直し
        CONFIRM,    // 確定

        STATE,
        RESULT,
        ERROR
    }


    public Type type;

    // client identity
    public Integer playerId;

    // command payload
    public Integer betBananas;

    // server -> client
    public String phase;
    public String message;

    public String result;
    public String handName;
    public String hand;

    public List<PlayerState> players;

    public AppMessage() {}

    public static AppMessage error(String msg) {
        AppMessage m = new AppMessage();
        m.type = Type.ERROR;
        m.message = msg;
        return m;
    }

    public static AppMessage state(String phase, List<PlayerState> players) {
        AppMessage m = new AppMessage();
        m.type = Type.STATE;
        m.phase = phase;
        m.players = players;
        return m;
    }

    public static AppMessage result(int playerId, String phase, String result, String handName, String hand) {
        AppMessage m = new AppMessage();
        m.type = Type.RESULT;
        m.playerId = playerId;
        m.phase = phase;
        m.result = result;
        m.handName = handName;
        m.hand = hand;
        return m;
    }

    public static class PlayerState {
        public int playerId;
        public String name;
        public int ownedBananas;
        public boolean dealer;
        public int currentBetBananas;

        public PlayerState() {}

        public PlayerState(int playerId, String name, int ownedBananas, boolean dealer, int currentBetBananas) {
            this.playerId = playerId;
            this.name = name;
            this.ownedBananas = ownedBananas;
            this.dealer = dealer;
            this.currentBetBananas = currentBetBananas;
        }
    }
}
