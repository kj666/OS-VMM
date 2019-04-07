import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

class Process implements Runnable {
    private int arrivalTime;
    private int burstTime;
    private int PID;
    private boolean isReady;
    private final AtomicBoolean isFinished;
    private double remainingTime;
    private double duration;

    private int state;


    public Process(int arrivalTime, int burstTime, int PID) {
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.PID = PID;
        state = Scheduler.IDLE;

        this.isFinished = new AtomicBoolean(false);
        this.isReady = false;
    }

    public void print(){
        System.out.println("PID: "+PID+" - AT: "+ arrivalTime+" - BT: "+burstTime);
    }

    @Override
    public void run() {
        state = Scheduler.RUNNING;
        System.out.println("IN PROCESS Clock: "+ Scheduler.time + ", Process "+PID+": Started");

        while(arrivalTime+burstTime > Scheduler.time){

            try {
                VMmanager.nextCommand(this, Scheduler.time);
            }catch (IOException e){}

            if(Scheduler.time > (arrivalTime + burstTime)){
                System.out.println("IN PROCESS Running Finished @ "+ Scheduler.time);
                break;
            }


//            System.out.println("Process TIME: " + Scheduler.time);
        }
//        System.out.println(PID+" finished");
    }


    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public AtomicBoolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished.set(finished);
    }

    public double getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(double remainingTime) {
        this.remainingTime = remainingTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
