package nl.devon;

public interface TestExecutionContext {

	void set(DelayedVerification verification);

	DelayedVerification get();
}
