package io.pickles.preprocessor.stubs;

import java.util.ArrayList;
import java.util.List;

import io.pickles.steps.DelayedVerification;
import io.pickles.steps.DelayedVerificationStore;

public class DummyDelayedVerificationStore implements DelayedVerificationStore {

	private int nrDvs;
	private Integer nextId = 1000;

	public DummyDelayedVerificationStore(int nrDvs) {
		this.nrDvs = nrDvs;
	}

	@Override
	public void create(DelayedVerification verification) {
	}

	@Override
	public DelayedVerification read(String dvId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(DelayedVerification verification) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DelayedVerification> readAllForChecksum(String checksum) {
		List<DelayedVerification> result = new ArrayList<>();
		for (int i = 0; i < nrDvs; i++) {
			DelayedVerification verification = new DelayedVerification(nextId.toString(), null, null, null, checksum,
					null);
			result.add(verification);
			nextId++;
		}
		return result;
	}

	@Override
	public List<DelayedVerification> readAllToVerify(String checksum) {
		return readAllForChecksum(checksum);
	}
}
