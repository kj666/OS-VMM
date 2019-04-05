
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Virtual Memory Manager");

        Scheduler scheduler = new Scheduler();
        scheduler.startScheduler();

    }
}
