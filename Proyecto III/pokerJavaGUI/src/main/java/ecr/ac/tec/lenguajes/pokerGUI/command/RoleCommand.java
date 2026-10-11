package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;
import ecr.ac.tec.lenguajes.pokerGUI.context.ClientContext;

public class RoleCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final String ROLE_DEALER = "DEALER";
    private static final String ROLE_SMALL_BLIND = "SMALL_BLIND";
    private static final String ROLE_BIG_BLIND = "BIG_BLIND";
    private static final String ROLE_DEALER_MAPPED = "D";
    private static final String ROLE_SMALL_BLIND_MAPPED = "SB";
    private static final String ROLE_BIG_BLIND_MAPPED = "BB";

    
    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String role = args[1];
            String mappedRole = mapRole(role);
            String myName = ClientContext.getInstance().getPlayerName();
            if (myName != null) {
                controller.setPlayerRole(myName, mappedRole);
            }
        }
    }

    private String mapRole(String role) {
        switch (role) {
            case ROLE_DEALER:
                return ROLE_DEALER_MAPPED;
            case ROLE_SMALL_BLIND:
                return ROLE_SMALL_BLIND_MAPPED;
            case ROLE_BIG_BLIND:
                return ROLE_BIG_BLIND_MAPPED;
            default:
                return "";
        }
    }
}
