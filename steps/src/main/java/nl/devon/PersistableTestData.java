package nl.devon;

public interface PersistableTestData {

	void save(DelayedVerification delayedVerification);

	void load(DelayedVerification delayedVerification);
}
