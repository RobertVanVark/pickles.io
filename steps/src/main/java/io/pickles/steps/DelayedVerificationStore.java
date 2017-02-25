package io.pickles.steps;

import java.util.List;

public interface DelayedVerificationStore {

	void create(DelayedVerification verification);

	DelayedVerification read(String dvId);

	void update(DelayedVerification verification);

	List<DelayedVerification> readAllForChecksum(String checksum);

	List<DelayedVerification> readAllToVerify(String checksum);
}
