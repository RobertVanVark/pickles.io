package io.pickles.steps;

public interface PersistableTestData {

	void saveFor(DelayedVerification delayedVerification);

	void loadFor(DelayedVerification delayedVerification);
}
