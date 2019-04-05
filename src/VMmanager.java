import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class VMmanager implements Runnable {

    int maxSize;
    Vector<Page> mainMemory;
    Vector<Page> virtualMemory;
    Vector<Command> commandList;


    @Override
    public void run() {

    }

    public VMmanager() throws FileNotFoundException {
        parseMemConfigFile("memconfig.txt");
        commandList = commmandParser("commands.txt");
        System.out.println("memory size: "+maxSize);
        mainMemory = new Vector<>();
        virtualMemory = new Vector<>();
    }

    //Stores given variableID and its value into first assigned spot in memory
    public int store(String variableId, int value) throws IOException {

        Page tmp = new Page(variableId, value);
        System.out.println(mainMemory.size());

        if(mainMemory.size() < maxSize){
            //not enough space in MainMemory so store in virtual memory
            System.out.println("Not Enough Space VMsize:" +virtualMemory.size());
            virtualMemory.add(tmp);
            System.out.println("Not Enough Space VMsize:" +virtualMemory.size());
            //todo store Page tmp into the vm.txt
            FileWriter file = new FileWriter("C:\\Dev\\OS-VMM\\vm.txt",true);
            PrintWriter writer = new PrintWriter(file,true);
                writer.println(tmp.variableID + " " + tmp.value);
                writer.close();
        }
        else{
            //enough space in main memory
            System.out.println("Enough Space");
            mainMemory.add(tmp);
        }
        return 1;
    }

    public void release(String variableId){
        //search in mainMemory for variableID
        for(int i = 0; i < mainMemory.size();i++){
            //if exist then remove it from mainMemory
            if(mainMemory.get(i).variableID.equals(variableId)){
                mainMemory.removeElementAt(i);
            }
        }

        //search in virtualMemory for variableID
        for(int i = 0; i < virtualMemory.size();i++){
            //if exist then remove it from virtualMemory
            if(virtualMemory.get(i).variableID.equals(variableId)){
                virtualMemory.removeElementAt(i);
            }
        }
    }

    public int lookUp(String variableId){

        //search in mainMemory for variableID
        for(int i = 0; i < mainMemory.size();i++){
            //if page exists then return its value
            if(mainMemory.get(i).variableID.equals(variableId)){
                mainMemory.get(i).setLastAccess(System.currentTimeMillis());
                return mainMemory.get(i).value;
            }
        }

        //search in virtualMemory for variableID
        for(int i = 0; i < virtualMemory.size();i++){
            //if page exists page fault occurs
            //then move page into memory and release this page from virtual memory
            //if no spot available in mainMemory, then swap with least recently accessed variable
            if(virtualMemory.get(i).variableID.equals(variableId)){
                swapMemory(variableId);
                virtualMemory.removeElementAt(i);
            }
        }

        return -1;
    }

    //find least recent page in mainMemory and swap with specified page
    public void swapMemory(String variableID){

        //smallest long is the oldest time
        long oldest = mainMemory.get(0).lastAccess;
        int positionOldest = 0;

        //check last accessed page
        for(int i = 1; i< mainMemory.size();i++){
            if(mainMemory.get(i).lastAccess < oldest){
                positionOldest = i-1;
            }
        }
        System.out.println("MemSwap "+ positionOldest +" lastAccessed: "+oldest);

        //Insert from mainMem to VM
        virtualMemory.add(mainMemory.get(positionOldest));
        //remove from VM
        mainMemory.removeElementAt(positionOldest);

        //insert from VM to mainMem once one spot free
        for(int i = 0; i < virtualMemory.size();i++){
            if(virtualMemory.get(i).variableID.equals(variableID)){
                virtualMemory.get(i).setLastAccess(System.currentTimeMillis());
                mainMemory.add(virtualMemory.get(i));
                virtualMemory.removeElementAt(i);
                break;
            }
        }
    }

    public boolean nextCommand() throws IOException {

        if(commandList.size() != 0){
            Command currentCmd = commandList.firstElement();
            commandList.removeElement(commandList.firstElement());

            if(currentCmd.getCommand().equals("Store")){
                store(currentCmd.getVariableId(), currentCmd.getValue());
                return true;
            }
            else if(currentCmd.getCommand().equals("Release")){
                release(currentCmd.getVariableId());
                return true;
            }
            else if(currentCmd.getCommand().equals("Lookup")){
                lookUp(currentCmd.getVariableId());
                return true;
            }
            else {
                System.out.println("Unknown Command");
                return false;
            }
        }
        return false;
    }

    void parseMemConfigFile(String fileName)throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)));
        maxSize = scanner.nextInt();
    }

    //parse commands text file and return an array of commands
     Vector<Command> commmandParser(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)));
        //temporary array to store commands
        Vector<Command> commands = new Vector<>();
        scanner.nextLine();

        //scan if next line exists
        while (scanner.hasNextLine()) {

            String info = scanner.nextLine();
            String arr[] = info.split(" ", 3);
            // store the command id
            String commandID = arr[0];
            String variableId = String.valueOf(arr[1]);
            int variableValue;
            if (arr.length < 2) {
                variableValue = Integer.parseInt(null);
            } else {
                variableValue = Integer.valueOf(arr[2]);
            }
            Command command = new Command(commandID, variableId, variableValue);
            commands.add(command);

        }
        return commands;
    }

}
