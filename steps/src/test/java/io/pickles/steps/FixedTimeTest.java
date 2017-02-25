package io.pickles.steps;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;

public class FixedTimeTest {

	@Before
	public void givenTimeFixedAtTen() {
		DateTimeUtils.setCurrentMillisFixed(ten().getMillis());
	}

	@After
	public void givenDateTimeReset() {
		DateTimeUtils.setCurrentMillisSystem();
	}

	protected DateTime ten() {
		return midnight().withHourOfDay(10);
	}

	protected DateTime twelve() {
		return midnight().withHourOfDay(12);
	}

	protected DateTime tenNextDay() {
		return ten().plusDays(1);
	}

	protected DateTime midnight() {
		return DateTime.now().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
				.withMillisOfSecond(0);
	}

}
