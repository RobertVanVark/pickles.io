package nl.devon;

import org.joda.time.DateTime;

public abstract class Delay {

	protected Integer hours;
	protected Integer minutes;

	abstract public DateTime getVerifyAt(TestExecutionContext executionContext);

	Integer getHours() {
		return hours;
	}

	Integer getMinutes() {
		return minutes;
	}

}