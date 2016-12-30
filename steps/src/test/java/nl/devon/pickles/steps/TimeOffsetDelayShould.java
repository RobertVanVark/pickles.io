package nl.devon.pickles.steps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.devon.pickles.steps.stubs.StubExecutionContext;

public class TimeOffsetDelayShould {

	/*
	 * move to next business day when crossing day boundary
	 *
	 * provide default BusinessDayCalendar that skips Weekends
	 *
	 * use TestExecutionContext for Business Event Resolution
	 *
	 * Pattern for BE through TEC or properties ?
	 *
	 */

	@Before
	public void givenTimeFixedAtTwelve() {
		DateTimeUtils.setCurrentMillisFixed(twelve().getMillis());
	}

	@After
	public void givenDateTimeReset() {
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void matchValidTimeExpression() {
		Delay delay = new TimeOffsetDelay("23:59 hr");
		assertThat(delay.getHours(), is(23));
		assertThat(delay.getMinutes(), is(59));

		delay = new TimeOffsetDelay("3:12 hr");
		assertThat(delay.getHours(), is(3));
		assertThat(delay.getMinutes(), is(12));
	}

	@Test
	public void notMatch24Hours() {
		Delay delay = new TimeOffsetDelay("24:00 hr");
		assertThat(delay.getHours(), nullValue());
		assertThat(delay.getMinutes(), nullValue());
	}

	@Test
	public void notMatch60Minutes() {
		Delay delay = new TimeOffsetDelay("12:60 hr");
		assertThat(delay.getHours(), nullValue());
		assertThat(delay.getMinutes(), nullValue());
	}

	@Test
	public void addOffsetToCurrentTime() {
		Delay delay = new TimeOffsetDelay("2:00 hr");
		DateTime time = delay.getVerifyAt(new StubExecutionContext());
		assertThat(time, is(twelve().plusHours(2)));
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

	private DateTime ten() {
		return midnight().withHourOfDay(10);
	}

	private DateTime twelve() {
		return midnight().withHourOfDay(12);
	}

	private DateTime midnight() {
		return DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
	}
}
