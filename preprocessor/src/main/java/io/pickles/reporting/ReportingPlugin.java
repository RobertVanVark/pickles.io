package io.pickles.reporting;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gherkin.formatter.model.Feature;
import io.pickles.plugins.CorePlugin;
import io.pickles.preprocessor.model.TestRun;

public class ReportingPlugin extends CorePlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingPlugin.class);

	private ReportingStore store;
	private TestRun testRun;

	public ReportingPlugin(Appendable out) {
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

	@Override
	protected void log(String msg) {
		LOGGER.debug(msg);
	}
}
