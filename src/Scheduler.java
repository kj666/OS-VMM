//import sun.plugin.perf.PluginRollup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Scheduler implements Runnable{

    final static boolean DEBUG = true;
    final static int CORES = 2;
    static final int IDLE = 1;
    static final int RUNNING = 2;
    static final int FINISHED = 3;
    static Random rand;

    VMmanager vmm;

    public static int time;

    //store all process info while parsing text file
    ArrayList<Process> processes = new ArrayList<>();
    //Store all process ID when they are ready to be executed
    private Deque<Process> readyQueue = new LinkedList<>();
    //Store all process ID when they are running
    private Deque<Process> runningQueue = new LinkedList<>();
    private Deque<Process> finishedQueue = new LinkedList<>();
    Process currentProcess;
    boolean areProcessDone;
    Thread vmmThread;



    private int  threadCount;

    private int processToRun[] = new int[CORES];

    private Deque<Process> runningQ = new LinkedList<>();
    private Deque<Process> waitingQ = new LinkedList<>();

    Semaphore semaphore;



    public Scheduler() throws FileNotFoundException {
        time = 1;
        areProcessDone = false;
        threadCount = 0;
        vmm = new VMmanager();
        vmmThread = new Thread(vmm);
        vmmThread.start();
    }

    @Override
    public void run() {
        Thread t = new Thread(this);
        t.start();
    }

    public void startScheduler() throws IOException {
        //read txt file
        parseProcessFile("processes.txt");


        while(!areProcessDone){
            checkArrivalTimeProcess();

            //Move from readyQueue to runningQueue
            while(runningQueue.size()< CORES && !readyQueue.isEmpty()){
                runningQueue.addLast(readyQueue.getFirst());
                readyQueue.removeFirst();
                System.out.println("moving from ready to runningQ "+Scheduler.time);
            }

            if(currentProcess == null && runningQueue.size() != 0){
                currentProcess = runningQueue.getFirst();
            }


            runQueue();

            checkFinished();

//            System.out.println(time);
            time++;
        }
    }


    public Process findProcessAndSetState(int PID, int state){
        for(Process p: processes){
            if(p.getPID() == PID){
                p.setState(state);
                return p;
            }
        }
        return null;
    }

    public void checkArrivalTimeProcess(){
        for(Process p: processes){
            if(p.getArrivalTime() == time){
                findProcessAndSetState(p.getPID(), RUNNING);
                readyQueue.addLast(p);
            }
        }
    }

    public void runQueue() throws IOException {

        while(runningQueue.size() != 0){
            if(false){
                System.out.println("Running QUEUE");
//                printQID(runningQueue);
            }

            Process p = runningQueue.getFirst();
//            System.out.println(p.getState());
            if(p.getState() == RUNNING) {
                Thread thread = new Thread(p);
            System.out.println("Clock: "+ time + ", Process "+p.getPID()+": Started");
                thread.start();
            }


//            time += vmm.nextCommand(p, time);
            System.out.println("Clock: "+ time + ", Process "+p.getPID()+": Running");

            runningQueue.removeFirst();
        }
    }

    public void checkFinished(){
        for(Process p: processes){
            if(p.getArrivalTime()+p.getBurstTime() == time){
                findProcessAndSetState(p.getPID(), FINISHED);
                finishedQueue.addLast(p);
                System.out.println("Clock: "+ time + ", Process "+p.getPID()+": "+p.getState());
            }
            if(finishedQueue.size() == processes.size()){
                areProcessDone = true;
            }
        }
    }

    /**
     * Parse process input file into Process
     * @param fileName
     * @throws FileNotFoundException
     */
    void parseProcessFile(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)));

        threadCount = scanner.nextInt();
        System.out.println("\nNumber of Threads: "+threadCount);
        int PID = 1;
        scanner.nextLine();
        while (scanner.hasNextLine()){
            String p[] = scanner.nextLine().split(" ",2);
            int at = Integer.parseInt(p[0]);
            int bt = Integer.parseInt(p[1]);
            processes.add(new Process(at, bt , PID));
            PID++;
        }
        if(DEBUG) {
            System.out.println("\nList of all Process");
            for (Process p : processes) {
                p.print();
            }
            System.out.println();
        }
        scanner.close();

    }

    static int randomTime(){
        return rand.nextInt(100);
    }
//
    //
    //
    //NEVER USED NEVER USED


    //intialize mutex
    void initFlag(){
        semaphore = new Semaphore(1);
        for(int i = 0; i< processToRun.length; i++){
            processToRun[i] = 0;
        }
    }

    //give cpu a specific thread
    void setThreadFlag(int flag1, int flag2){
        //lock mutex before accessing flag
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //set thread value and notify process
        processToRun[0] = flag1;
        processToRun[1] = flag2;
        notify();

        semaphore.release();

    }

    //simulate process
    /*void runProcess(Process process){

        int id;
        Process info = process;
        //run process until total burst time reached
        while(info.getBurstTime() - info.getDuration() > 0){
            //lock mutex before accessing flag
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //check if current thread is in cpu
            while(processToRun[0]!= info.getPID() &&  (processToRun[1]!= info.getPID())){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            semaphore.release();

            if(DEBUG){
                System.out.println("time "+ time+" PID: "+info.getPID());
            }
        }
        int runTime = 0;

        int quantum = 300;
        while(runTime< quantum){

        }


    }*/

    //thread scheduler
    void start_scheduler(){
        boolean areProcessFinished;
        if(DEBUG)
            System.out.println("Starting RR");

        while(true){
            areProcessFinished = true;

            //Check arrival time and add process to running queue
            checkArrivalTime();

            //run all process in queue
            for(int i = 0; i < runningQ.size(); i++){
                if(!runningQ.isEmpty()){
                    //Wake first pid in queue
                    if(DEBUG)
                        printQ(runningQ);
                    setThreadFlag(runningQ.getFirst().getPID(),runningQ.getFirst().getPID());
                    //sleep until cpu is returned to scheduler
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while(processToRun[0] != 0 && processToRun[1] !=0){
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    semaphore.release();
                    if(DEBUG)
                        System.out.println("Scheduler Resumed");
                    time++;
                    checkArrivalTime();
                }
            }

            //check if process is still running
            if(waitingQ.size() == 0){
                if(!runningQ.isEmpty())
                    areProcessFinished = true;
                else
                    areProcessFinished = false;
            }
            if(areProcessFinished)
                break;
        }

    }
    /**
     * Print Queue
     * @param queue
     */
    public void printQ(Deque<Process> queue){
        for(Process p: queue){
            System.out.printf("PID: "+ p.getPID()+" arrival: "+p.getArrivalTime()+" remaining: "+ p.getRemainingTime());
        }
    }

    public void printQID(Deque<Integer> queue){
        for(Integer p: queue){
            System.out.println("Time: "+time+" PID: "+ p);
        }
    }

    /**
     * Check for process arrival time
     */
    public void checkArrivalTime(){
        for(int i = 0; i < waitingQ.size(); i++){
            if(waitingQ.getFirst().getArrivalTime() == time){
                runningQ.push(waitingQ.getFirst());
                System.out.println(waitingQ.getFirst().getPID()+" starting");
                waitingQ.removeFirst();
            }
            else
                break;
        }
    }



    void parseCommandFile(String fileName)throws FileNotFoundException{
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)));

    }

}
