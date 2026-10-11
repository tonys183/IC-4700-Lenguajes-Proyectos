package ecr.ac.tec.lenguajes.pokerGUI.context;

public class ClientContext {
    private static ClientContext instance;
    private String playerName;

    private ClientContext() {
    }

    public static synchronized ClientContext getInstance() {
        if (instance == null) {
            instance = new ClientContext();
        }
        return instance;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
