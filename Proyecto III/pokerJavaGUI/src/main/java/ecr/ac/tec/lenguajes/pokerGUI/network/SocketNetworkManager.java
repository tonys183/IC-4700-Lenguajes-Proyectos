package ecr.ac.tec.lenguajes.pokerGUI.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class SocketNetworkManager implements NetworkManager {
    private static final String LOG_CONNECTED = "Connected to ";
    private static final String LOG_FAILED_CONNECT = "Failed to connect: ";
    private static final String LOG_CONNECTION_LOST = "Connection lost: ";
    private static final String SEPARATOR = ":";

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private NetworkListener listener;
    private Thread listenerThread;

    @Override
    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            startListening();
            if (listener != null) {
                listener.onConnectionStatusChanged(true);
            }
            System.out.println(LOG_CONNECTED + host + SEPARATOR + port);
        } catch (IOException e) {
            System.err.println(LOG_FAILED_CONNECT + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    @Override
    public void setListener(NetworkListener listener) {
        this.listener = listener;
        if (this.listener != null && socket != null && socket.isConnected() && !socket.isClosed()) {
             this.listener.onConnectionStatusChanged(true);
        }
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (listener != null) {
                        listener.onMessage(msg);
                    }
                }
            } catch (IOException e) {
                System.err.println(LOG_CONNECTION_LOST + e.getMessage());
                if (listener != null) {
                    listener.onConnectionStatusChanged(false);
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
}
