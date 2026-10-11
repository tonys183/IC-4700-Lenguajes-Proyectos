package ecr.ac.tec.lenguajes.pokerServer.server.command.game;

import ecr.ac.tec.lenguajes.pokerServer.player.BetStatus;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.server.ServerContext;
import ecr.ac.tec.lenguajes.pokerServer.game.managers.GameManager;
import ecr.ac.tec.lenguajes.pokerServer.server.c.CComunicator;
import java.util.List;
import java.util.Optional;

public class FoldCommand implements GameCommand {

    @Override
    public void execute(ServerContext context) throws Exception {
        if (context.getController() instanceof GameManager manager) {
            boolean roundFinished = manager.playerBet(BetStatus.FOLD, Optional.empty());
            Optional<PokerPlayer> posibleWinner = manager.winnerByFold();
            if (posibleWinner.isPresent()) {
                manager.finishCurrentGame(List.of(posibleWinner.get()));
                manager.doCurrentRound();
            } else if (manager.shouldRoundEnd()) {
                askForWinners(manager, context);
            } else if (roundFinished)  {
                if(manager.doCurrentRound()) {
                    askForWinners(manager, context);
                }
            }
        }
    }

    private void askForWinners(GameManager manager, ServerContext context) throws Exception {
        CComunicator comunicator = context.getCComunicator();
        String playersMessage = comunicator.buildMessage(manager.activePlayers(), manager.getCurrentGame());
        String winnersRaw = context.getCEvaluator().evaluateHand(playersMessage);
        List<PokerPlayer> winners = comunicator.parseWinners(winnersRaw, manager.activePlayers(), manager.getCurrentGame());
        manager.finishCurrentGame(winners);
        manager.doCurrentRound();
    }
}
