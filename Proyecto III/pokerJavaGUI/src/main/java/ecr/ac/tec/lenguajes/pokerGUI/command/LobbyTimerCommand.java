package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class LobbyTimerCommand implements ClientCommand {
    private static final int TIMER_ARG_INDEX = 1;
    private static final String CANCELLED = "CANCELLED";
    private static final String TIMER_PREFIX = "Starting in: ";
    private static final String TIMER_CANCELLED_MSG = "Waiting for players...";

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length > TIMER_ARG_INDEX) {
            String timerValue = args[TIMER_ARG_INDEX];
            if (CANCELLED.equals(timerValue)) {
                controller.updateTimerLabel(TIMER_CANCELLED_MSG);
            } else if ("0".equals(timerValue)) {
                controller.updateTimerLabel(TIMER_PREFIX + "0s");
                controller.hideTimerWithFade();
            } else {
                controller.updateTimerLabel(TIMER_PREFIX + timerValue + "s");
            }
        }
    }
}
