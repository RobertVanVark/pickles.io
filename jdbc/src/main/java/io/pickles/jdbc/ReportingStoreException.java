package io.pickles.jdbc;

public class ReportingStoreException extends RuntimeException {

	public ReportingStoreException(String message) {
		super(message);
	}

	public ReportingStoreException(String message, Throwable ex) {
		super(message, ex);
	}
}
