package nl.devon.pickles.steps.delays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void matchSingleDelayExpressions() {
		assertTrue("after 02:00 hr".matches(DelayFactory.DELAY_EXPRESSION));
		assertTrue("after NOON".matches(DelayFactory.DELAY_EXPRESSION));
	}

	@Test
	public void matchDoubleDelayExpressions() {
		assertTrue("after 02:00 hr + 03:15 hr".matches(DelayFactory.DELAY_EXPRESSION));
		assertTrue("after NOON + 02:30 hr".matches(DelayFactory.DELAY_EXPRESSION));
	}

	@Test
	public void matchTripleDelayExpressions() {
		assertTrue("after 02:00 hr + 03:15 hr + 0:55 hr".matches(DelayFactory.DELAY_EXPRESSION));
		assertTrue("after NOON + 02:30 hr + DINNER".matches(DelayFactory.DELAY_EXPRESSION));
	}
}
