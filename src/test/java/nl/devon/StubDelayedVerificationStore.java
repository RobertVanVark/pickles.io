package nl.devon;

/**
 * Created by harm on 20-12-2016.
 */
public class StubDelayedVerificationStore implements DelayedVerificationStore {

    private static int nrTimesCreated;

    public static void resetNrTimesCreated() {
        nrTimesCreated = 0;
    }

    public static int getNrTimesCreated() {
        return nrTimesCreated;
    }


    public StubDelayedVerificationStore() {
        nrTimesCreated++;
    }

    @Override
    public void save(DelayedVerification verification) {

    }

    @Override
    public DelayedVerification load(String dvId) {
        return null;
    }
}
