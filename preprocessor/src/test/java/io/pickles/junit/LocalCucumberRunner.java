package io.pickles.junit;

public class LocalCucumberRunner extends PicklesCucumberRunner {

	public LocalCucumberRunner(Class<?> clazz) {
		super(clazz);
		setDelayedVerificationStore(new StubDelayedVerificationStore(3));
	}
}
