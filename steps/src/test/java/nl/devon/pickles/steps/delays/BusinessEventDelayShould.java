package nl.devon.pickles.steps.delays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

import nl.devon.pickles.steps.FixedTimeTest;
import nl.devon.pickles.steps.stubs.StubExecutionContext;
import nl.devon.pickles.steps.stubs.StubNextDayExecutionContext;

public class BusinessEventDelayShould extends FixedTimeTest {

	@Test
	public void matchBusinessEventWithHyphen() {
		BusinessEventDelay delay = new BusinessEventDelay("NOON");
		assertThat(delay.getEvent(), is("NOON"));

		delay = new BusinessEventDelay("PRE-NOON");
		assertThat(delay.getEvent(), is("PRE-NOON"));
	}

	@Test
	public void matchOneWordOnly() {
		BusinessEventDelay delay = new BusinessEventDelay("NOON MEETING");
		assertThat(delay.getEvent(), is("NOON"));

		delay = new BusinessEventDelay("PRE-NOON MEETING");
		assertThat(delay.getEvent(), is("PRE-NOON"));
	}

	@Test
	public void matchMixedCase() {
		BusinessEventDelay delay = new BusinessEventDelay("noon meeting");
		assertThat(delay.getEvent(), is("noon"));

		delay = new BusinessEventDelay("pre-Noon MEETING");
		assertThat(delay.getEvent(), is("pre-Noon"));
	}

	@Test
	public void determineVerifyAtThroughTestExecutionTime() {
		BusinessEventDelay delay = new BusinessEventDelay("Noon");

		DateTime verifyAt = delay.getVerifyAt(new StubExecutionContext());
		assertThat(verifyAt, is(twelve()));

		verifyAt = delay.getVerifyAt(new StubNextDayExecutionContext());
		assertThat(verifyAt, is(midnight().withHourOfDay(12)));
	}

	@Test
	public void matchExpression() {
		assertThat(matchesExpression("NOON"), is(true));
		assertThat(matchesExpression("noon"), is(true));
		assertThat(matchesExpression("pre-Noon"), is(true));

		assertThat(matchesExpression("NOON MEETING"), is(false));
		assertThat(matchesExpression("pre-Noon MEETING"), is(false));
	}

	private boolean matchesExpression(String expression) {
		return expression.matches(BusinessEventDelay.EXPRESSION);
	}
}
