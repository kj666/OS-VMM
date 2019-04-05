import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class VMmanager implements Runnable {

    int maxSize;
    Vector<Page> mainMemory;
    Vector<Page> virtualMemory;


    @Override
    public void run() {

    }

    public VMmanager() throws FileNotFoundException {
        parseMemConfigFile("memconfig.txt");
        System.out.println("memory size: "+maxSize);
        mainMemory = new Vector<>();
        virtualMemory = new Vector<>();
    }

    //Stores given variableID and its value into first assigned spot in memory
    public int store(String variableId, int value) throws IOException {

        Page tmp = new Page(variableId, value);
        System.out.println(mainMemory.size());

        if(mainMemory.size() >= maxSize){
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
        //search in memory for variableID
        for(int i = 0; i < mainMemory.size();i++){
            if(mainMemory.get(i).variableID.equals(variableId)){
                mainMemory.removeElementAt(i);
            }
        }

        //search in virtualMemory for variableID
        for(int i = 0; i < virtualMemory.size();i++){
            if(virtualMemory.get(i).variableID.equals(variableId)){
                virtualMemory.removeElementAt(i);
            }
        }

    }

    public int lookUp(String variableId){
        return 1;
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

    void parseVMM(){

    }
}
