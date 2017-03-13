package io.pickles.junit;

import java.util.ArrayList;
import java.util.List;

import io.pickles.steps.DelayedVerification;
import io.pickles.steps.DelayedVerificationStore;

public class StubDelayedVerificationStore implements DelayedVerificationStore {

	private int nrDvs;
	private Integer nextId = 1000;

	public StubDelayedVerificationStore(int nrDvs) {
		this.nrDvs = nrDvs;
	}

	@Override
	public void create(DelayedVerification verification) {
		System.out.println(
				"XXXXXXXX - StubDVStore - DV(create) : " + verification.getId() + "-" + verification.getFeatureUri());
	}

	@Override
	public DelayedVerification read(String dvId) {
		System.out.println("XXXXXXXX - StubDVStore - DV(read) : " + dvId);
		return null;
	}

	@Override
	public void update(DelayedVerification verification) {
		System.out.println(
				"XXXXXXXX - StubDVStore - DV(update) : " + verification.getId() + "-" + verification.getFeatureUri());
	}

	@Override
	public List<DelayedVerification> readAllForChecksum(String checksum) {
		System.out.println("XXXXXXXX - StubDVStore - readAll(checksum) : " + checksum);
		List<DelayedVerification> result = new ArrayList<>();
		for (int i = 1; i < nrDvs; i++) {
			DelayedVerification verification = new DelayedVerification(nextId.toString(), null, null, null, checksum,
					null);
			result.add(verification);
			nextId++;
		}
		return result;
	}

	@Override
	public List<DelayedVerification> readAllToVerify(String checksum) {
		System.out.println("XXXXXXXX - StubDVStore - readAll(verify) : " + checksum);
		return new ArrayList<>();
	}
}
