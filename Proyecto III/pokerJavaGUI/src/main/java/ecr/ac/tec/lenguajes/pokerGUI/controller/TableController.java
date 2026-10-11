package ecr.ac.tec.lenguajes.pokerGUI.controller;

import ecr.ac.tec.lenguajes.pokerGUI.model.card.Card;
import ecr.ac.tec.lenguajes.pokerGUI.model.game.GameType;

public interface TableController {
    void setGameMode(GameType mode);
    void updatePot(int amount);
    void updatePlayerMoney(int amount);
    void updateCommunityCards(Card... cards);
    void updatePlayerHand(Card... cards);
    void updateOpponentHand(int cardCount);
    void showGameStatus(String message);
}
