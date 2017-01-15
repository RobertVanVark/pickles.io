package nl.devon.pickles.steps.delays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

import nl.devon.pickles.steps.FixedTimeTest;
import nl.devon.pickles.steps.stubs.StubExecutionContext;

public class CombinedDelayShould extends FixedTimeTest {

	@Test
	public void addDelays() {
		CombinedDelay combined = (CombinedDelay) DelayFactory.create("Noon + 1:00 hr");
		assertThat(combined.getVerifyAt(new StubExecutionContext()), is(thirteen()));
	}

	private DateTime thirteen() {
		return midnight().withHourOfDay(13);
	}

}
