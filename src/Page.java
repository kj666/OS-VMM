
public class Page {
    String variableID;
    int value;
    long lastAccess;

    public Page(String pageID, int value) {
        this.variableID = pageID;
        this.value = value;
        lastAccess = System.currentTimeMillis();
    }
}


