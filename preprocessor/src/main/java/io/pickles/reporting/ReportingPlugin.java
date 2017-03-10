package io.pickles.reporting;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import io.pickles.plugins.CorePlugin;
import io.pickles.preprocessor.model.ScenarioModel;
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
		lastFeature().setStartedAt(DateTime.now());
		storeTestRun();
	}

	@Override
	public void eof() {
		super.eof();
		lastFeature().setFinishedAt(DateTime.now());
		store.create(lastFeature());
	}

	@Override
	public void startOfScenarioLifeCycle(Scenario scenario) {
		super.startOfScenarioLifeCycle(scenario);
		ScenarioModel model = lastFeature().getScenario(scenario);
		model.setStartedAt(DateTime.now());
	}

	@Override
	public void scenario(Scenario scenario) {
		super.scenario(scenario);
		ScenarioModel model = lastFeature().getScenario(scenario);
		if (model.getName().matches(".* \\(dvId=.*\\)$")) {
			System.out.println("Triggered by dvID : " + model.getName());
		}
	}

	@Override
	public void step(Step step) {
		super.step(step);
		if (step.getName().startsWith("after ")) {
			System.out.println("Triggering dvID : " + step.getName());
		}
	}

	@Override
	public void endOfScenarioLifeCycle(Scenario scenario) {
		super.endOfScenarioLifeCycle(scenario);
		ScenarioModel model = lastFeature().getScenario(scenario);
		model.setFinishedAt(DateTime.now());
	}

	public void storeTestRun() {
		if (testRun == null) {
			testRun = new TestRun("dummy", "dummy description", DateTime.now(), DateTime.now());
			store.create(testRun);
		}
	}

	@Override
	protected void log(String msg) {
		LOGGER.debug(msg);
	}
}
