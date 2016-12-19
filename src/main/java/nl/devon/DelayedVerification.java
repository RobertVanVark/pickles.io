package nl.devon;


import org.joda.time.DateTime;

import java.util.UUID;

public class DelayedVerification {

    private final DateTime createdAt;
    private final DateTime verifyAt;
    private final String scenarioChecksum;
    private String id;

    public DelayedVerification(DateTime verifyAt, String scenarioChecksum) {
        this(DateTime.now(),verifyAt,scenarioChecksum, UUID.randomUUID().toString());
    }

    public DelayedVerification(DateTime createdAt, DateTime verifyAt, String scenarioChecksum, String id) {
        this.scenarioChecksum = scenarioChecksum;
        this.createdAt = createdAt;
        this.verifyAt = verifyAt;
        this.id = id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getVerifyAt() {
        return verifyAt;
    }

    public String getScenarioChecksum() {
        return scenarioChecksum;
    }

    public String getId() {
        return id;
    }
}
