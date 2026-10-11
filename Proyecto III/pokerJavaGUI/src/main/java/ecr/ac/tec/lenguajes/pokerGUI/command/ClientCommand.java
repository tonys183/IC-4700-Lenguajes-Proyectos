package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public interface ClientCommand {
    void execute(FXMLTableController controller, String[] args);
}
