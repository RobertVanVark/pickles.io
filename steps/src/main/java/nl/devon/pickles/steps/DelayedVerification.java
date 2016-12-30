package nl.devon.pickles.steps;

import java.util.UUID;

import org.joda.time.DateTime;

public class DelayedVerification {

	private final String id;
	private final DateTime createdAt;
	private final DateTime verifyAt;
	private final DateTime processedAt;
	private final String scenarioChecksum;
	private final String feature;

	public DelayedVerification(DateTime verifyAt, String scenarioChecksum, String feature) {
		this(UUID.randomUUID().toString(), DateTime.now(), verifyAt, null, scenarioChecksum, feature);
	}

	public DelayedVerification(String id, DateTime createdAt, DateTime verifyAt, DateTime processedAt,
			String scenarioChecksum, String feature) {
		this.id = id;
		this.createdAt = createdAt;
		this.verifyAt = verifyAt;
		this.processedAt = processedAt;
		this.scenarioChecksum = scenarioChecksum;
		this.feature = feature;
	}

	public String getId() {
		return id;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public DateTime getVerifyAt() {
		return verifyAt;
	}

	public DateTime getProcessedAt() {
		return processedAt;
	}

	public String getScenarioChecksum() {
		return scenarioChecksum;
	}

	public String getFeature() {
		return feature;
	}
}
