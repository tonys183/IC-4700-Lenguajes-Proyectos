package ecr.ac.tec.lenguajes.pokerServer.server.command.game;

import ecr.ac.tec.lenguajes.pokerServer.server.ServerContext;

public interface GameCommand {
    void execute(ServerContext serverContex) throws Exception;
}
