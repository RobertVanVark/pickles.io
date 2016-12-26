package nl.devon.cucumber.jdbc;

public class DelayedVerificationStoreException extends RuntimeException {

	public DelayedVerificationStoreException(String message) {
		super(message);
	}

	public DelayedVerificationStoreException(String message, Throwable ex) {
		super(message, ex);
	}
}
