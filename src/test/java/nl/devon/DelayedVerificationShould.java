package nl.devon;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class DelayedVerificationShould {

    /*
    * have a unique id
    *
     */

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
        String uuid = UUID.randomUUID().toString();
        DelayedVerification verification = new DelayedVerification(DateTime.now(), "", uuid);
        assertThat(verification.getId(), is(uuid));
    }

    private DelayedVerification newDelayedVerification(DateTime verifyAt, String scenarioChecksum) {
        return new DelayedVerification(verifyAt, scenarioChecksum);
    }

}
