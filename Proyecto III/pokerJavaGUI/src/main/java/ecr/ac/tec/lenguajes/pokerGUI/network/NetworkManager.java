package ecr.ac.tec.lenguajes.pokerGUI.network;

public interface NetworkManager {
    void connect(String host, int port);
    void send(String message);
    void setListener(NetworkListener listener);
}
