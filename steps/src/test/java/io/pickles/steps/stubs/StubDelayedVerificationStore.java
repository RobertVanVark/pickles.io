package io.pickles.steps.stubs;

import java.util.List;

import org.joda.time.DateTime;

import io.pickles.steps.DelayedVerification;
import io.pickles.steps.DelayedVerificationStore;

public class StubDelayedVerificationStore implements DelayedVerificationStore {

	int nrLoads = 0;
	int nrSaves = 0;
	private DelayedVerification lastStored;

	public int getNrLoadsCalled() {
		return nrLoads;
	}

	public int getNrSavesCalled() {
		return nrSaves;
	}

	public DelayedVerification getDvSaved() {
		return lastStored;
	}

	@Override
	public void create(DelayedVerification verification) {
		nrSaves++;
		lastStored = verification;
	}

	@Override
	public DelayedVerification read(String dvId) {
		nrLoads++;
		return new DelayedVerification(dvId, DateTime.now(), DateTime.now(), null, "stubChecksum", "stubFeature");
	}

	@Override
	public void update(DelayedVerification verification) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DelayedVerification> readAllForChecksum(String checksum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DelayedVerification> readAllToVerify(String checksum) {
		// TODO Auto-generated method stub
		return null;
	}
}