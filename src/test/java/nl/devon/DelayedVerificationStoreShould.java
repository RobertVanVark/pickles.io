package nl.devon;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

public class DelayedVerificationStoreShould {

	@Test
	public void haveIdWhenRestored() {
		String uuid = "12454-23132-123-131-231";
		DelayedVerification verification = new DelayedVerification(DateTime.now(), DateTime.now(), "", uuid);
		assertThat(verification.getId(), is(uuid));
	}

	public void haveCorrectCreatedAtWhenRestored() {
		DateTime createdAt = DateTime.parse("2016-01-01 12:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
		DelayedVerification verification = new DelayedVerification(createdAt, DateTime.now(), "", "");
		assertThat(verification.getCreatedAt(), is(createdAt));
	}
}
