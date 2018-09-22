package snma.junior_task;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    public static void main(String[] args) {
        ExecutorService workersThreadPool = Executors.newFixedThreadPool(Config.WORKER_THREADS_COUNT);
        Thread dirWatcherThread = new Thread(new InputDirWatcher(workersThreadPool));
        dirWatcherThread.start();
        String[] existingXmlFiles = new File(Config.FOLDER_INPUT).list(
                (dir, fname) -> fname.toLowerCase().endsWith(".xml"));
        for (String fname : existingXmlFiles) {
            workersThreadPool.execute(new FileProcessTask(fname));
        }

        System.out.print("Press Enter to stop: ");
        try {
            System.in.read();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        System.out.println("Quiting...");
        dirWatcherThread.interrupt();
        workersThreadPool.shutdown();
    }
}
