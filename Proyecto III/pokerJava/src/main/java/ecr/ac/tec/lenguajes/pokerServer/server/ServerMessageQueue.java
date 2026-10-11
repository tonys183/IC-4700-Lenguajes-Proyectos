package ecr.ac.tec.lenguajes.pokerServer.server;

import ecr.ac.tec.lenguajes.pokerServer.server.command.game.GameCommand;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ServerMessageQueue implements MessageQueue {
    private static final String PUSH_ERROR_PREFIX = "[ERROR] Failed to push command to queue: ";
    private static final String DEQUEUE_ERROR_PREFIX = "[ERROR] Failed to take command from queue: ";

    private final BlockingQueue<GameCommand> queue;

    public ServerMessageQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public void push(GameCommand command) {
        try {
            queue.put(command);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(PUSH_ERROR_PREFIX + e.getMessage());
        }
    }

    public GameCommand dequeue() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(DEQUEUE_ERROR_PREFIX + e.getMessage());
            return null;
        }
    }
}
