import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class VMmanager implements Runnable {

    int maxSize;
    Vector<Page> mainMemory;
    Vector<Page> virtualMemory;
    Vector<Command> commandList;
    int timeTaken;

    AtomicBoolean isPaused;


    @Override
    public void run() {
        timeTaken = 0;
        while(!isPaused.get()){
            System.out.println("VMM TIME: "+timeTaken);
            timeTaken++;
        }
    }

    public VMmanager() throws FileNotFoundException {
        parseMemConfigFile("memconfig.txt");
        System.out.println("\nmemory size: "+maxSize);
        commandList = commmandParser("commands.txt");
        mainMemory = new Vector<>();
        virtualMemory = new Vector<>();
        timeTaken = 0;
        isPaused = new AtomicBoolean(true);
    }

    //Stores given variableID and its value into first assigned spot in memory
    public int store(String variableId, int value) throws IOException {

        Page tmp = new Page(variableId, value);
        System.out.println(mainMemory.size());

        if(mainMemory.size() < maxSize){
            //not enough space in MainMemory so store in virtual memory
            virtualMemory.add(tmp);
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

    public int nextCommand(Process p, int time) throws IOException {

        if(commandList.size() != 0){
            //retrieve the first command from list
            Command currentCmd = commandList.firstElement();
            //removes the first command from list
            commandList.removeElement(commandList.firstElement());

            if(currentCmd.getCommand().equals("Store")){
                timeTaken = 300;
                time+=timeTaken;
                System.out.println("Clock: "+ time + ", Process "+p.getPID()+", "+currentCmd.getCommand()+": Variable "+currentCmd.getVariableId()+", Value: "+currentCmd.getValue());
                store(currentCmd.getVariableId(), currentCmd.getValue());
                return timeTaken;
            }
            else if(currentCmd.getCommand().equals("Release")){
                timeTaken = 300;
                time+=timeTaken;
                System.out.println("Clock: "+ time + ", Process "+p.getPID()+", "+currentCmd.getCommand()+": Variable "+currentCmd.getVariableId());
                release(currentCmd.getVariableId());
                return timeTaken;
            }
            else if(currentCmd.getCommand().equals("Lookup")){
                timeTaken = 300;
                time+=timeTaken;
                System.out.println("Clock: "+ time + ", Process "+p.getPID()+", "+currentCmd.getCommand()+": Variable "+currentCmd.getVariableId()+", Value: "+currentCmd.getValue());
                lookUp(currentCmd.getVariableId());
                return timeTaken;
            }
            else {
                System.out.println("Unknown Command");
                return timeTaken;
            }
        }
        return timeTaken;
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

        //scan if next line exists
        while (scanner.hasNextLine()) {
            Command command;
            String info = scanner.nextLine();
            String arr[] = info.split(" ", 3);
            // store the command id
            String commandID = arr[0];
            String variableId = String.valueOf(arr[1]);

            if (arr.length < 3)
                command = new Command(commandID, variableId);
            else
                command = new Command(commandID, variableId, Integer.valueOf(arr[2]));

            commands.add(command);
        }

        System.out.println("\nList of Parsed Commands");
        for(Command c: commands){
            System.out.println("Cmd: "+ c.getCommand()+" vID: "+ c.getVariableId()+ " Value: "+c.getValue());
        }
        return commands;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setPausedState(boolean pause) {
        isPaused.set(pause);
    }
}
