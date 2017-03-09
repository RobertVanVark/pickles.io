package io.pickles.plugins;

import org.joda.time.DateTime;

import gherkin.formatter.model.Feature;
import io.pickles.preprocessor.model.TestRun;
import io.pickles.reporting.ReportingStore;

public class PicklesReportingPlugin extends PicklesCorePlugin {

	private ReportingStore store;
	private TestRun testRun;

	public PicklesReportingPlugin(Appendable out) {
		super(out);
	}

	public void setReportingStore(ReportingStore store) {
		this.store = store;
	}

	@Override
	public void feature(Feature feature) {
		super.feature(feature);
		storeTestRun();
	}

	public void storeTestRun() {
		if (testRun == null) {
			testRun = new TestRun("dummy", "dummy description", DateTime.now(), DateTime.now());
			store.createTestRun(testRun);
		}
	}

}
