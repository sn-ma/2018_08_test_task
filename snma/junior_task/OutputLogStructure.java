package snma.junior_task;

import java.io.File;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** This class is modeling logical structure of generated xml files */
public class OutputLogStructure {
    private final Map<LocalDate, Logday> days = new TreeMap<>();
    private final DocumentBuilder docBuilder;
    
    private static final DocumentBuilderFactory DOC_FACTORY = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory TRANSF_FACTORY = TransformerFactory.newInstance();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    
    public OutputLogStructure() {
        try {
            docBuilder = DOC_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void store(LocalDate date, String userId, String url, long average) {
        if (!days.containsKey(date)) {
            days.put(date, new Logday());
        }
        days.get(date).store(userId, url, average);
    }
    
    /** Update stored data with new InputLogEntry
     * 
     * @param input read data unit.
     */
    public void processInputLogEntry(InputLogEntry input) {
        ZonedDateTime start = input.getTimeStart().atZone(Config.TIME_ZONE_ID);
        ZonedDateTime end = input.getTimeEnd().atZone(Config.TIME_ZONE_ID);
        
        ZonedDateTime curStart = start;
        for (ZonedDateTime midnight = start.plusDays(1).truncatedTo(ChronoUnit.DAYS);
                midnight.isBefore(end.plusDays(1)); midnight = midnight.plusDays(1)) {
            
            ZonedDateTime curEnd = (midnight.isBefore(end)) ? midnight : end;
            long seconds = (curEnd.toEpochSecond() - curStart.toEpochSecond());
            
            LocalDate date = curStart.toLocalDate();
            store(date, input.getUserId(), input.getUrl(), seconds);
            
            curStart = midnight;
        }
    }
    
    /** Store currently available data to file by given file path
     * 
     * @param fname file name and path
     */
    public void printXml(String fname) {
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement("output");
        doc.appendChild(root);
        
        days.forEach((date, logday) -> {
            Element logdayTag = doc.createElement("logday");
            root.appendChild(logdayTag);
            
            Element dayTag = doc.createElement("day");
            logdayTag.appendChild(dayTag);
            dayTag.appendChild(doc.createTextNode(date.format(DATE_FORMATTER).toUpperCase()));
            
            Element usersTag = doc.createElement("users");
            logdayTag.appendChild(usersTag);
            
            logday.users.forEach((userId, userInfoList) -> {
                userInfoList.userInfo.forEach((String url, Long average) -> {
                    Element userTag = doc.createElement("user");
                    usersTag.appendChild(userTag);
                    
                    Element idTag = doc.createElement("id");
                    userTag.appendChild(idTag);
                    idTag.appendChild(doc.createTextNode(userId));
                    
                    Element urlTag = doc.createElement("url");
                    userTag.appendChild(urlTag);
                    urlTag.appendChild(doc.createTextNode(url));
                    
                    Element averageTag = doc.createElement("average");
                    userTag.appendChild(averageTag);
                    averageTag.appendChild(doc.createTextNode(average.toString()));
                });
            });
        });
        
        try {
            doc.setXmlStandalone(true);
            Transformer transformer = TRANSF_FACTORY.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fname));
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
    private static class UserInfoList {
        // map: url -> average
        final Map<String, Long> userInfo = new TreeMap<>(new AlphanumericComparator());

        public void store(String url, long average) {
            userInfo.merge(url, average, (avg, prevAvg) -> (avg + prevAvg));
        }
    }


    private static class Logday {
        // map: userId -> UserInfoList
        final Map<String, UserInfoList> users = new TreeMap<>(new AlphanumericComparator());

        public void store(String userId, String url, long average) {
            if (!users.containsKey(userId)) {
                users.put(userId, new UserInfoList());
            }
            users.get(userId).store(url, average);
        }
    }
}
