package snma.junior_task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;


/** Class for reading formatted xml file. Returns read blocks as structure @InputLogEntry. */
public class InputLogReader implements AutoCloseable {
    private final FileInputStream input;
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    private final XMLStreamReader reader;
    
    public InputLogReader(String fname) {
        try {
            input = new FileInputStream(fname);
            reader = FACTORY.createXMLStreamReader(input);
        } catch (FileNotFoundException | XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public InputLogEntry readNext() {
        try {
            InputLogEntry answ = new InputLogEntry();
            boolean
                    tsSet = false,
                    uidSet = false,
                    urlSet = false,
                    secSet = false;
            while(reader.hasNext()) {
                final int event = reader.next();
                if (event == XMLEvent.END_ELEMENT && reader.getLocalName().equals("log")) {
                    if (tsSet && uidSet && urlSet && secSet) {
                        return answ;
                    } else {
                        throw new RuntimeException("Not all fields were set!");
                    }
                } else if (event == XMLEvent.START_ELEMENT) {
                    switch(reader.getLocalName()) {
                        case "timestamp":
                            answ.setTimestamp(Long.valueOf(reader.getElementText()));
                            tsSet = true;
                            break;
                        case "userId":
                            answ.setUserId(reader.getElementText());
                            uidSet = true;
                            break;
                        case "url":
                            answ.setUrl(reader.getElementText());
                            urlSet = true;
                            break;
                        case "seconds":
                            answ.setSeconds(Long.valueOf(reader.getElementText()));
                            secSet = true;
                            break;
                        default:
                            // ignore
                    }
                }
            }
            
            return null;
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() throws Exception {
        if (input != null) {
            input.close();
        }
    }
}
