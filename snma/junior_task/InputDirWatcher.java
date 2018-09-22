package snma.junior_task;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;

/** Class to monitor new .xml files creation */
class InputDirWatcher implements Runnable {
    private final ExecutorService threadPool;

    public InputDirWatcher(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        try {
            Path inputPath = Paths.get(Config.FOLDER_INPUT);
            WatchService watchService = FileSystems.getDefault().newWatchService();
            inputPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey watchKey;
            while ((watchKey = watchService.take()) != null) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    String fname = ((Path)event.context()).toString();
                    if (fname.toLowerCase().endsWith(".xml")) {
                        threadPool.execute(new FileProcessTask(fname));
                    }
                }
                watchKey.reset();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            // Normal quiting
        }
    }
}
