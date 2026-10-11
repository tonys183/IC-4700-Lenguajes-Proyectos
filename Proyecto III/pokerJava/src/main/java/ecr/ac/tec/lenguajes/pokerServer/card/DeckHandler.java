package ecr.ac.tec.lenguajes.pokerServer.card;

public interface DeckHandler {
    void shuffle();
    Card draw();
    void fill();
}
