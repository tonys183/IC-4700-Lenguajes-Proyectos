package ecr.ac.tec.lenguajes.pokerGUI.log;

public final class ConsoleLog implements Log {
    private static final String LOG_INFO = "[INFO] ";
    private static final String LOG_ERROR = "[ERROR] ";

    @Override
    public void info(String message) {
        System.out.println(LOG_INFO + message);
    }

    @Override
    public void error(String message, Exception e) {
        System.err.println(LOG_ERROR + message);
        e.printStackTrace();
    }
}
