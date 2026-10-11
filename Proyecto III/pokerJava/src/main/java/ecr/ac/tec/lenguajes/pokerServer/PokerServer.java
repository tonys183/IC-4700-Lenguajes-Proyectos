package ecr.ac.tec.lenguajes.pokerServer;

import ecr.ac.tec.lenguajes.pokerServer.game.managers.LobbyPlayerManager;
import ecr.ac.tec.lenguajes.pokerServer.server.GameLoop;
import ecr.ac.tec.lenguajes.pokerServer.server.ServerContext;
import ecr.ac.tec.lenguajes.pokerServer.server.ServerMessageQueue;
import ecr.ac.tec.lenguajes.pokerServer.server.c.CConnector;
import ecr.ac.tec.lenguajes.pokerServer.server.c.ConsoleCComunicator;
import java.net.ServerSocket;
import java.net.Socket;

public final class PokerServer {
    private static final int PORT_GUI = 8080;
    private static final int PORT_C = 8081;
    private static final String GUI_LISTENING_MSG = "[INFO] GUI Listening on port ";
    private static final String C_LISTENING_MSG = "[INFO] C Listening on port ";

    public static void main(String[] args) {
        ServerContext context = new ServerContext(null, new ConsoleCComunicator());

        LobbyPlayerManager lobby = new LobbyPlayerManager(context);
        context.setController(lobby);

        ServerMessageQueue queue = new ServerMessageQueue();

        GameLoop gameLoop = new GameLoop(context, queue);
        new Thread(gameLoop).start();

        try (ServerSocket serverGuiSocket = new ServerSocket(PORT_GUI)) {
            System.out.println(GUI_LISTENING_MSG + PORT_GUI);

            new Thread(() -> {
                try (ServerSocket serverCSocket = new ServerSocket(PORT_C)) {
                    System.out.println(C_LISTENING_MSG + PORT_C);
                    Socket cConnection = serverCSocket.accept();

                    CConnector cConnector = new CConnector(cConnection);
                    context.setCEvaluator(cConnector);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                new Thread(new SocketRunnable(serverGuiSocket.accept(), queue)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
