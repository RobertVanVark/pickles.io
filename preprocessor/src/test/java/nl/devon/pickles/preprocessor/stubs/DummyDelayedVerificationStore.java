package nl.devon.pickles.preprocessor.stubs;

import java.util.Arrays;
import java.util.List;

import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.DelayedVerificationStore;

public class DummyDelayedVerificationStore implements DelayedVerificationStore {

	private Integer nextId = 1000;

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
		DelayedVerification verification = new DelayedVerification(nextId.toString(), null, null, null, checksum, null);
		nextId++;
		return Arrays.asList(verification);
	}

	@Override
	public List<DelayedVerification> readAllToVerify(String checksum) {
		// TODO Auto-generated method stub
		return null;
	}
}
