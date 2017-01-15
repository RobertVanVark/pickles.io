package nl.devon.pickles.steps.delays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DelayFactoryShould {

	@Test
	public void constructTimeOffsetDelay() {
		Delay delay = DelayFactory.create("02:00 hr");
		assertThat(delay, is(instanceOf(TimeOffsetDelay.class)));
	}

	@Test
	public void constructBusinessEventDelay() {
		Delay delay = DelayFactory.create("Delay");
		assertThat(delay, is(instanceOf(BusinessEventDelay.class)));
	}

	@Test
	public void constructCombinedDelay() {
		Delay delay = DelayFactory.create("Lunch + 02:00 hr");
		assertThat(delay, is(instanceOf(CombinedDelay.class)));

		CombinedDelay combined = (CombinedDelay) delay;
		assertThat(combined.getFirstDelay(), is(instanceOf(BusinessEventDelay.class)));
		assertThat(combined.getSecondDelay(), is(instanceOf(TimeOffsetDelay.class)));
	}
}
