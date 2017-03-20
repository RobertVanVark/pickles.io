package io.pickles.reporting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import io.pickles.model.ScenarioModel;
import io.pickles.model.TestRun;
import io.pickles.plugins.CorePlugin;

public class ReportingPlugin extends CorePlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingPlugin.class);

	private ReportingStore store;
	private TestRun testRun;

	private boolean isDryRun = true;

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
		lastFeature().setTestRun(testRun);
	}

	@Override
	public void result(Result result) {
		super.result(result);
		if (!"skipped".equals(result.getStatus()) && !"undefined".equals(result.getStatus())) {
			isDryRun = false;
		}
	}

	@Override
	public void write(String text) {
		lastFeature().getFirstUnfinishedScenario().getLastStep().addOutput(text);
		isDryRun = false;
	}

	@Override
	public void eof() {
		super.eof();
		testRun.setFinishedAt(DateTime.now());
		storeTestRun();
		lastFeature().setFinishedAt(DateTime.now());
		storeFeature();
	}

	private Pattern scenarioPattern = Pattern.compile(".* \\(dvId=(.*)\\)$");

	@Override
	public void scenario(Scenario scenario) {
		super.scenario(scenario);
		ScenarioModel model = lastFeature().getScenario(scenario);
		model.setStartedAt(DateTime.now());
		Matcher matcher = scenarioPattern.matcher(scenario.getName());
		if (matcher.matches()) {
			log("Triggered by dvID : (" + matcher.group(1) + ") from " + scenario.getName());
			model.setTriggeringDvId(matcher.group(1));
		}
	}

	private Pattern thenAfterPattern = Pattern.compile("^after.*dvId=(.+), .*$");

	@Override
	public void step(Step step) {
		super.step(step);
		Matcher matcher = thenAfterPattern.matcher(step.getName());
		if (matcher.matches()) {
			log("Triggering dvID : (" + matcher.group(1) + ") from " + step.getName());
			lastFeature().getFirstUnfinishedScenario().setNextDvId(matcher.group(1));
		}
	}

	@Override
	public void endOfScenarioLifeCycle(Scenario scenario) {
		super.endOfScenarioLifeCycle(scenario);
		ScenarioModel model = lastFeature().getScenario(scenario);
		model.setFinishedAt(DateTime.now());
	}

	private void storeTestRun() {
		if (testRun != null && testRun.getId() != null) {
			updateTestRun();
		} else {
			if (testRun == null) {
				testRun = new TestRun("blank", "intentionally left blank", DateTime.now(), DateTime.now());
			}
			if (!isDryRun) {
				store.create(testRun);
			}
		}
	}

	private void updateTestRun() {
		if (!isDryRun) {
			store.update(testRun);
		}
	}

	private void storeFeature() {
		if (!isDryRun) {
			store.create(lastFeature());
		}
	}

	@Override
	protected void log(String msg) {
		LOGGER.debug(msg);
	}

}
