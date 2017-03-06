package io.pickles.steps.stubs;

import io.pickles.steps.DelayedVerification;
import io.pickles.steps.PersistableTestData;

public class StubTestData implements PersistableTestData {

	private int nrSaves = 0;
	private int nrLoads = 0;

	@Override
	public void saveFor(DelayedVerification delayedVerification) {
		nrSaves++;
	}

	@Override
	public void loadFor(DelayedVerification delayedVerification) {
		nrLoads++;
	}

	public int getNrSavesCalled() {
		return nrSaves;
	}

	public int getNrLoadsCalled() {
		return nrLoads;
	}
}