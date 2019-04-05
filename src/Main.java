import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Main {

    final int q = 3000;
    final int numberCore = 2;

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");

        VMmanager vm = new VMmanager();
        vm.store("1", 5);
        vm.store("2", 30);

        Scheduler scheduler = new Scheduler();
        scheduler.startScheduler();

    }


}
