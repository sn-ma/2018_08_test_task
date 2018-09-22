package snma.junior_task;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Properties;

public class Config {
    private Config() {}
    
    private static final String CONFIG_FILE_PATH = "config.properties";
    
    public static final String
            FOLDER_INPUT,
            FOLDER_OUTPUT,
            TIME_ZONE;
    public static final ZoneId TIME_ZONE_ID;
    public static final int WORKER_THREADS_COUNT;
    
    static {
        try(FileReader propReader = new FileReader(CONFIG_FILE_PATH)) {
            Properties property = new Properties();
            property.load(propReader);
            FOLDER_INPUT = property.getProperty("folderInput");
            FOLDER_OUTPUT = property.getProperty("folderOutput");
            TIME_ZONE = property.getProperty("timeZone");
            TIME_ZONE_ID = ZoneId.of(TIME_ZONE);
            WORKER_THREADS_COUNT = Integer.parseInt(
                    property.getProperty("workerThreads", "10"));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Config file not found at " + CONFIG_FILE_PATH);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
