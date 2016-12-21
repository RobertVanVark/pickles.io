package nl.devon;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.Delayed;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class DelayedVerificationShould {

    @Test
    public void haveCreationTime() {
        DelayedVerification verification = newDelayedVerification(DateTime.now(), "");
        assertThat(verification.getCreatedAt(), notNullValue() );
    }

    @Test
    public void haveVerificationTime() {
        DateTime verifyAt = DateTime.now().plusHours(2);
        DelayedVerification verification = newDelayedVerification(verifyAt, "");
        assertThat(verification.getVerifyAt(), is(verifyAt));
    }

    @Test
    public void identifyCreatingScenario() {
        String scenarioChecksum = "12345";
        DelayedVerification verification = newDelayedVerification(DateTime.now(), scenarioChecksum);
        assertThat(verification.getScenarioChecksum(), is(scenarioChecksum));
    }

    @Test
    public void generateUniqueId() {
        DelayedVerification verification = newDelayedVerification(DateTime.now(), "");
        assertThat(verification.getId(), notNullValue());
    }

    @Test
    public void HaveIdWhenRestored() {
        String uuid = "12454-23132-123-131-231";
        DelayedVerification verification = new DelayedVerification(DateTime.now(),DateTime.now(), "", uuid);
        assertThat(verification.getId(), is(uuid));
    }

    @Test
    public void HaveCorrectCreatedAtWhenRestored() {
        DateTime createdAt = DateTime.parse("2016-01-01 12:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        DelayedVerification verification = new DelayedVerification(createdAt, DateTime.now(), "", "");
        assertThat(verification.getCreatedAt(), is(createdAt));
    }

    private DelayedVerification newDelayedVerification(DateTime verifyAt, String scenarioChecksum) {
        return new DelayedVerification(verifyAt, scenarioChecksum);
    }

}
