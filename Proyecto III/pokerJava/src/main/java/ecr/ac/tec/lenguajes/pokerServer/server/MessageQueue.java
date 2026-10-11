package ecr.ac.tec.lenguajes.pokerServer.server;

import ecr.ac.tec.lenguajes.pokerServer.server.command.game.GameCommand;

public interface MessageQueue {
    void push(GameCommand gameCommand);
    GameCommand dequeue();
}
