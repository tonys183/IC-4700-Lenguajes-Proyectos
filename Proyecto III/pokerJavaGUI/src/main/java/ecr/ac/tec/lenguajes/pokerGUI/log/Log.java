package ecr.ac.tec.lenguajes.pokerGUI.log;

public interface Log {
    void info(String message);
    void error(String message, Exception e);
}
