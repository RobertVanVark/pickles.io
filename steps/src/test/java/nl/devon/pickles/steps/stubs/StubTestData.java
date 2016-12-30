package nl.devon.pickles.steps.stubs;

import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.PersistableTestData;

public class StubTestData implements PersistableTestData {

	private int nrSaves = 0;
	private int nrLoads = 0;

	@Override
	public void save(DelayedVerification delayedVerification) {
		nrSaves++;
	}

	@Override
	public void load(DelayedVerification delayedVerification) {
		nrLoads++;
	}

	public int getNrSavesCalled() {
		return nrSaves;
	}

	public int getNrLoadsCalled() {
		return nrLoads;
	}
}