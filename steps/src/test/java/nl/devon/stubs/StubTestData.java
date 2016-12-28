package nl.devon.stubs;

import nl.devon.DelayedVerification;
import nl.devon.PersistableTestData;

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