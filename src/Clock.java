import java.util.concurrent.Semaphore;

public class Clock {
    private int counter;
    private Semaphore semaphore;
    private Thread thread;

    public Clock() {
        counter = 0;
        semaphore = new Semaphore(1);
    }

    public void startClk(){
        thread = new Thread(this::incrementClk);
    }

    public void incrementClk()  {
        while(counter <= 10000){
            try {
                //Lock semaphore
                semaphore.acquire();
                Thread.sleep(1000);
                System.out.println("Time: "+ counter);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            semaphore.release();
            counter+=1;
        }
    }

    public int getClk(){
        return counter;
    }


}
