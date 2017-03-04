package io.pickles.steps.delays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import io.pickles.steps.TestExecutionContext;

public class BusinessEventDelay extends Delay {

	private String event;

	public static final String EXPRESSION = "(?:\\S+)";
	private static final Pattern PATTERN = Pattern.compile("^(\\S+)");

	public BusinessEventDelay(String expression) {
		Matcher matcher = PATTERN.matcher(expression);
		if (matcher.find()) {
			event = matcher.group(1);
		}
	}

	@Override
	DateTime getVerificationTime(TestExecutionContext testExecutionContext, DateTime atOrAfter) {
		DateTime firstVerificationTimeFor = testExecutionContext.firstVerificationTimeFor(event, atOrAfter);
		return testExecutionContext.firstBusinessDay(firstVerificationTimeFor);
	}

	public String getEvent() {
		return event;
	}
}