
package ecr.ac.tec.lenguajes.pokerServer;

import ecr.ac.tec.lenguajes.pokerServer.server.ServerMessageQueue;
import ecr.ac.tec.lenguajes.pokerServer.server.command.game.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

public final class SocketRunnable implements Runnable {
    private static final String CMD_JOIN = "JOIN";
    private static final String CMD_FOLD = "FOLD";
    private static final String CMD_CALL = "CALL";
    private static final String CMD_RAISE = "RAISE";
    private static final String MSG_OK_JOINED = "OK:Joined";
    private static final String MSG_OK_FOLDED = "OK:Folded";
    private static final String MSG_OK_CALLED = "OK:Called";
    private static final String MSG_OK_RAISED = "OK:Raised";
    private static final String MSG_OK = "OK";
    private static final String ERROR_UNKNOWN_COMMAND = "Error:Unknown Command";
    private static final String ERROR_PREFIX = "Error:";
    private static final String ERROR_INTERNAL_SERVER = "Error:Internal Server Error";
    private static final String LOG_RECEIVED = "Received: ";
    private static final String LOG_CONNECTION_LOST = "Connection lost: ";
    private static final String LOG_PLAYER_DISCONNECTED = "Player disconnected: ";
    private static final String SEPARATOR = ":";
    private static final double INITIAL_MONEY = 1000.0;

    private final Socket socket;
    private final ServerMessageQueue queue;
    private String playerName;

    private final Map<String, CommandFactory> commandFactories = Map.of(
            CMD_JOIN, (args, out) -> {
                this.playerName = args[1];
                return new JoinCommand(args[1], INITIAL_MONEY, out);
            },
            CMD_FOLD, (args, out) -> new FoldCommand(),
            CMD_CALL, (args, out) -> new CallCommand(),
            CMD_RAISE, (args, out) -> new RaiseCommand(Double.parseDouble(args[1])));

    private final Map<String, String> successMessages = Map.of(
            CMD_JOIN, MSG_OK_JOINED,
            CMD_FOLD, MSG_OK_FOLDED,
            CMD_CALL, MSG_OK_CALLED,
            CMD_RAISE, MSG_OK_RAISED);

    public SocketRunnable(final Socket socket, final ServerMessageQueue queue) {
        this.socket = socket;
        this.queue = queue;
    }

    @Override
    public void run() {
        try (Socket autoCloseSocket = this.socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(autoCloseSocket.getInputStream()));
                PrintWriter out = new PrintWriter(autoCloseSocket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(LOG_RECEIVED + message);
                try {
                    Optional<GameCommand> command = parseCommand(message, out);
                    if (command.isPresent()) {
                        queue.push(command.get());
                        String commandName = message.split(SEPARATOR)[0];
                        out.println(successMessages.getOrDefault(commandName, MSG_OK));
                    } else {
                        out.println(ERROR_UNKNOWN_COMMAND);
                    }
                } catch (IllegalArgumentException e) {
                    out.println(ERROR_PREFIX + e.getMessage());
                } catch (Exception e) {
                    out.println(ERROR_INTERNAL_SERVER);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println(LOG_CONNECTION_LOST + e.getMessage());
        } finally {
            if (playerName != null) {
                System.out.println(LOG_PLAYER_DISCONNECTED + playerName);
                queue.push(new LeaveCommand(playerName));
            }
        }
    }

    private Optional<GameCommand> parseCommand(String msg, PrintWriter out) throws Exception {
        String[] parts = msg.split(SEPARATOR);
        String commandName = parts[0];
        CommandFactory factory = commandFactories.get(commandName);
        if (factory != null) {
            return Optional.of(factory.create(parts, out));
        }
        return Optional.empty();
    }
}