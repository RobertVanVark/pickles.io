package io.pickles.steps.delays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

import io.pickles.steps.stubs.StubExecutionContext;
import io.pickles.steps.stubs.StubNextDayExecutionContext;

public class BusinessEventDelayShould extends BaseFixedTimeTest {

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
	public void determineVerificationTimeThroughTestExecutionTime() {
		BusinessEventDelay delay = new BusinessEventDelay("Noon");

		DateTime verifyAt = delay.getVerificationTime(new StubExecutionContext());
		assertThat(verifyAt, is(twelve()));

		verifyAt = delay.getVerificationTime(new StubNextDayExecutionContext());
		assertThat(verifyAt, is(midnight().withDayOfMonth(2).withHourOfDay(12)));
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
