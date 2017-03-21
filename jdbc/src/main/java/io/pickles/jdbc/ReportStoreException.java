package io.pickles.jdbc;

public class ReportStoreException extends RuntimeException {

	public ReportStoreException(String message) {
		super(message);
	}

	public ReportStoreException(String message, Throwable ex) {
		super(message, ex);
	}
}
