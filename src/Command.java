public class Command implements Runnable {
    private String variableId;
    private int value;
    private String command;

    public Command(String command,String variableId,  int value) {
        this.variableId = variableId;
        this.value = value;
        this.command = command;
    }
    @Override
    public void run() {
    }
    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
