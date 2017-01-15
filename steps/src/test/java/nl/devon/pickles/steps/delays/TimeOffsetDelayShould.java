package nl.devon.pickles.steps.delays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.FixedTimeTest;
import nl.devon.pickles.steps.TestExecutionContext;
import nl.devon.pickles.steps.stubs.StubExecutionContext;
import nl.devon.pickles.steps.stubs.StubNextDayExecutionContext;

public class TimeOffsetDelayShould extends FixedTimeTest {

	@Test
	public void matchValidTimeExpression() {
		TimeOffsetDelay delay = new TimeOffsetDelay("23:59 hr");
		assertThat(delay.getHours(), is(23));
		assertThat(delay.getMinutes(), is(59));

		delay = new TimeOffsetDelay("3:12 hr");
		assertThat(delay.getHours(), is(3));
		assertThat(delay.getMinutes(), is(12));
	}

	@Test
	public void notMatch24Hours() {
		TimeOffsetDelay delay = new TimeOffsetDelay("24:00 hr");
		assertThat(delay.getHours(), nullValue());
		assertThat(delay.getMinutes(), nullValue());
	}

	@Test
	public void notMatch60Minutes() {
		TimeOffsetDelay delay = new TimeOffsetDelay("12:60 hr");
		assertThat(delay.getHours(), nullValue());
		assertThat(delay.getMinutes(), nullValue());
	}

	@Test
	public void addOffsetToCurrentTime() {
		Delay delay = new TimeOffsetDelay("2:00 hr");
		DateTime time = delay.getVerifyAt(new StubExecutionContext());
		assertThat(time, is(ten().plusHours(2)));
	}

	@Test
	public void addOffsetToPreviousDvsIfAvailable() {
		DelayedVerification verification = new DelayedVerification(ten(), "checksum", "feature");
		TestExecutionContext executionContext = new StubExecutionContext();
		executionContext.set(verification);

		Delay delay = new TimeOffsetDelay("2:00 hr");
		DateTime time = delay.getVerifyAt(executionContext);
		assertThat(time, is(twelve()));
	}

	@Test
	public void scheduleVerificationOnBusinessDaysOnly() {
		Delay delay = new TimeOffsetDelay("2:00 hr");
		DateTime time = delay.getVerifyAt(new StubNextDayExecutionContext());
		assertThat(time, is(tenNextDay().plusHours(2)));
	}

	@Test
	public void matchExpression() {
		assertThat(matchesExpression("23:59 hr"), is(true));
		assertThat(matchesExpression("0:00 hr"), is(true));
		assertThat(matchesExpression("10:12 hr"), is(true));

		assertThat(matchesExpression("35:35 hr"), is(false));
		assertThat(matchesExpression("12 hr"), is(false));

	}

	private boolean matchesExpression(String expression) {
		return expression.matches(TimeOffsetDelay.EXPRESSION);
	}
}
