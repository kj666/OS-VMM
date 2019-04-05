import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;

import java.util.Scanner;
import java.lang.*;
public class VMmanager implements Runnable {

    private int maxSize;
    private int value;
    private String variableID;
    ArrayList<String> memoryVariableID = new ArrayList<String>();
    ArrayList<Integer> memoryVariableValue = new ArrayList<Integer>();
    @Override
    public void run() {

    }

    public VMmanager() throws FileNotFoundException {
        parseMemConfigFile("memconfig.txt");
        System.out.println("memory size: "+maxSize);
    }

    public void store(String variableId, int value){
        memoryVariableID.add(variableId);
        memoryVariableValue.add(value);
    }

    public void release(String variableId){
        memoryVariableID.remove(variableId);
        memoryVariableValue.remove(memoryVariableID.indexOf(variableId));

    }

    public int lookUp(String variableId) {
        int variableIndex = memoryVariableID.indexOf(variableId);
        if (variableIndex != -1) {
            System.out.println("Variable " + memoryVariableID.get(variableIndex) + ", Value: " + memoryVariableValue.get(variableIndex));
        } else {

        }
        return -1;
    }
    public void swapMemory(){

    }

    public void handlePage(){

    }

    public void nextCommand(){

    }

    void parseMemConfigFile(String fileName)throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)));
        maxSize = scanner.nextInt();

    }


}
