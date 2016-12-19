package nl.devon;

import org.joda.time.DateTime;
import org.junit.Test;

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
        DelayedVerification verification = new DelayedVerification(DateTime.now(),"");
        assertThat(verification.getCreatedAt(), notNullValue() );
    }

    @Test
    public void haveVerificationTime() {
        DateTime verifyAt = DateTime.now().plusHours(2);
        DelayedVerification verification = new DelayedVerification(verifyAt,"");
        assertThat(verification.getVerifyAt(), is(verifyAt));
    }

    @Test
    public void identifyCreatingScenario() {
        String scenarioChecksum = "12345";
        DelayedVerification verification = new DelayedVerification(DateTime.now(), scenarioChecksum);
        assertThat(verification.getScenarioChecksum(), is(scenarioChecksum));
    }
}
