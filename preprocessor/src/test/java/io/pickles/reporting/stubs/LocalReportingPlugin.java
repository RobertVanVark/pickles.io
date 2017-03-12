package io.pickles.reporting.stubs;

import io.pickles.reporting.ReportingPlugin;

public class LocalReportingPlugin extends ReportingPlugin {

	public LocalReportingPlugin(Appendable out) {
		super(out);
		setReportingStore(new StubReportingStore());
	}
}
