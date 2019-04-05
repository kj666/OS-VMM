import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    }

    //Stores given variableID and its value into first assigned spot in memory
    public int store(String variableId, int value){

        Page tmp = new Page(variableId, value);

        if(mainMemory.size() >= maxSize){
            //not enough space in MainMemory so store in virtual memory
            System.out.println("Enough Space");
            virtualMemory.add(tmp);
            //todo store Page tmp into the vm.txt file

        }
        else{
            //enough space in main memory
            mainMemory.add(tmp);
        }
        return 1;
    }

    public void release(String variableId){

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
