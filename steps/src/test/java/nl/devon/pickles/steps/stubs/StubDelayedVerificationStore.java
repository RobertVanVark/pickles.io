package nl.devon.pickles.steps.stubs;

import java.util.List;

import org.joda.time.DateTime;

import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.DelayedVerificationStore;

public class StubDelayedVerificationStore implements DelayedVerificationStore {

	int nrLoads = 0;
	int nrSaves = 0;

	public int getNrLoadsCalled() {
		return nrLoads;
	}

	public int getNrSavesCalled() {
		return nrSaves;
	}

	@Override
	public void create(DelayedVerification verification) {
		nrSaves++;
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