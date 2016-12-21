package nl.devon;

/**
 * Created by harm on 20-12-2016.
 */
@DVStore
public class StubDelayedVerificationStore implements DelayedVerificationStore {

    @Override
    public void save(DelayedVerification verification) {

    }

    @Override
    public DelayedVerification load(String dvId) {
        return null;
    }
}
