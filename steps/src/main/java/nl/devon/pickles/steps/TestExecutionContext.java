package nl.devon.pickles.steps;

public interface TestExecutionContext {

	void set(DelayedVerification verification);

	DelayedVerification get();
}
