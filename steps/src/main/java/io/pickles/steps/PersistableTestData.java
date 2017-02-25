package io.pickles.steps;

public interface PersistableTestData {

	void save(DelayedVerification delayedVerification);

	void load(DelayedVerification delayedVerification);
}
