package nl.devon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class TimeOffsetDelay extends Delay {

	static final String EXPRESSION = "[[01]?[0-9]|2[0-3]]:[0-5][0-9] hr";
	private static final Pattern PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):([0-5][0-9]) hr");

	public TimeOffsetDelay(String expression) {
		Matcher matcher = PATTERN.matcher(expression);
		if (matcher.find()) {
			hours = Integer.valueOf(matcher.group(1));
			minutes = Integer.valueOf(matcher.group(2));
		}
	}

	@Override
	public DateTime getVerifyAt(TestExecutionContext executionContext) {
		DateTime start = DateTime.now();
		if (executionContext.get() != null) {
			start = executionContext.get().getVerifyAt();
		}

		return start.plusHours(hours).plusMinutes(minutes);
	}

}
