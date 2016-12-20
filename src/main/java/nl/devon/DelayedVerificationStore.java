package nl.devon;

public interface DelayedVerificationStore {

    void save(DelayedVerification verification);

    DelayedVerification load(String dvId);
}
