package snma.junior_task;

import java.time.Instant;


public class InputLogEntry {
    private String userId, url;
    private long seconds;
    private Instant startTime;
    
    public Instant getTimeStart() {
        return startTime;
    }
    
    public Instant getTimeEnd() {
        return startTime.plusSeconds(seconds);
    }

    public void setTimestamp(long timestamp) {
        startTime = Instant.ofEpochSecond(timestamp);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("ts:"); sb.append(getTimeStart()); sb.append(", ");
        sb.append("usr:"); sb.append(userId); sb.append(", ");
        sb.append("url:"); sb.append(url); sb.append(", ");
        sb.append("sec:"); sb.append(seconds);
        sb.append("}");
        return sb.toString();
    }
}
