package ecr.ac.tec.lenguajes.pokerServer.server.c;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class CConnector {
    private final PrintWriter out;
    private final BufferedReader in;

    public CConnector(Socket socket) throws Exception {
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public synchronized String evaluateHand(String messageToC) throws Exception {
        out.println(messageToC);

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            if (line.trim().equals("<END>")) break;
            response.append(line).append("\n");
        }
        return response.toString().trim();
    }
}