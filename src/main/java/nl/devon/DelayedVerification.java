package nl.devon;


import org.joda.time.DateTime;

public class DelayedVerification {

    private final DateTime createdAt;
    private final DateTime verifyAt;
    private final String scenarioChecksum;

    public DelayedVerification(DateTime verifyAt, String scenarioChecksum) {
        this.scenarioChecksum = scenarioChecksum;
        createdAt = DateTime.now();
        this.verifyAt = verifyAt;
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
}
