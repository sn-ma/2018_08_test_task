package snma.junior_task;

import java.nio.file.Path;
import java.nio.file.Paths;


/** Runnable to process log .xml file */
public class FileProcessTask implements Runnable {
    private final String fname;

    public FileProcessTask(String fname) {
        this.fname = fname;
    }
    
    @Override
    public void run() {
        Path inFName = Paths.get(Config.FOLDER_INPUT, fname);
        Path outFName = Paths.get(Config.FOLDER_OUTPUT, "avg_" + fname);
        OutputLogStructure outputLog = new OutputLogStructure();
        try (InputLogReader logReader = new InputLogReader(inFName.toString())) {
            while (true) {
                InputLogEntry entry = logReader.readNext();
                if (entry == null) {
                    break;
                }
                outputLog.processInputLogEntry(entry);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        outputLog.printXml(outFName.toString());
    }
}
