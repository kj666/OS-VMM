
public class Page {
    String variableID;
    int value;
    long lastAccess;

    public Page(String pageID, int value) {
        this.variableID = pageID;
        this.value = value;
        lastAccess = System.currentTimeMillis();
    }

    public String getVariableID() {
        return variableID;
    }

    public void setVariableID(String variableID) {
        this.variableID = variableID;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
    }
}


