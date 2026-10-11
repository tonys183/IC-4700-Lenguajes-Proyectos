package ecr.ac.tec.lenguajes.pokerServer.server.command.game;

import java.io.PrintWriter;

public interface CommandFactory {
    GameCommand create(String[] args, PrintWriter out) throws IllegalArgumentException;
}
