package nl.devon.pickles.steps.delays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import nl.devon.pickles.steps.TestExecutionContext;

public class BusinessEventDelay extends Delay {

	private String event;

	public static final String EXPRESSION = "\\S+";
	private static final Pattern PATTERN = Pattern.compile("^(\\S+)");

	public BusinessEventDelay(String expression) {
		Matcher matcher = PATTERN.matcher(expression);
		if (matcher.find()) {
			event = matcher.group(1);
		}
	}

	@Override
	DateTime getVerifyAt(TestExecutionContext executionContext, DateTime startingFrom) {
		return executionContext.verifyTimeFor(event, startingFrom);
	}

	public String getEvent() {
		return event;
	}
}