package io.pickles.reporting;

import io.pickles.reporting.stubs.StubReportingStore;

public class LocalReportingPlugin extends ReportingPlugin {

	public LocalReportingPlugin(Appendable out) {
		super(out);
		setReportingStore(new StubReportingStore());
	}
}
