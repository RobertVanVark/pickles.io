package io.pickles.steps;

import org.joda.time.DateTime;

public class DelayedVerification {

	private final String id;
	private final DateTime createdAt;
	private final DateTime verifyAt;
	private final DateTime processedAt;
	private final String scenarioChecksum;
	private final String featureUri;

	public DelayedVerification(String id, DateTime verifyAt, String scenarioChecksum, String featureUri) {
		this(id, DateTime.now(), verifyAt, null, scenarioChecksum, featureUri);
	}

	public DelayedVerification(String id, DateTime createdAt, DateTime verifyAt, DateTime processedAt,
			String scenarioChecksum, String featureUri) {
		this.id = id;
		this.createdAt = createdAt;
		this.verifyAt = verifyAt;
		this.processedAt = processedAt;
		this.scenarioChecksum = scenarioChecksum;
		this.featureUri = featureUri;
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

	public String getFeatureUri() {
		return featureUri;
	}
}
