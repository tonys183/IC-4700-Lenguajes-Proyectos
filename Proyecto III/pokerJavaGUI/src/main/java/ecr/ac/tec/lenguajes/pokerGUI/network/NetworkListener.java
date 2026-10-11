package ecr.ac.tec.lenguajes.pokerGUI.network;

public interface NetworkListener {
    void onMessage(String message);
    void onConnectionStatusChanged(boolean isConnected);
}
