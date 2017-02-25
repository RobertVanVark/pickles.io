package io.pickles.preprocessor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gherkin.formatter.Formatter;
import gherkin.formatter.NiceAppendable;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;
import io.pickles.preprocessor.model.FeatureModel;
import io.pickles.preprocessor.model.ScenarioModel;
import io.pickles.preprocessor.model.StepModel;

public class TemplateFormatter implements Formatter {

	private static final Logger LOGGER = LoggerFactory.getLogger("nl.devon.pickles.plugin.TemplateFormatter");

	private List<FeatureModel> features;
	protected NiceAppendable out;

	public TemplateFormatter(Appendable out) {
		this.out = new NiceAppendable(out);
		features = new ArrayList<>();
	}

	public List<FeatureModel> getFeatures() {
		return features;
	}

	protected FeatureModel lastFeature() {
		if (features.isEmpty()) {
			return null;
		}
		return features.get(features.size() - 1);
	}

	@Override
	public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {
		log("syntaxError");
		// intentionally left blank
	}

	@Override
	public void uri(String uri) {
		log("uri");
		FeatureModel feature = new FeatureModel();
		feature.setUri(uri);
		features.add(feature);
	}

	@Override
	public void feature(Feature feature) {
		log("feature");
		lastFeature().setFeature(feature);
	}

	@Override
	public void scenarioOutline(ScenarioOutline scenarioOutline) {
		log("scenario outline");
		// intentionally left blank
	}

	@Override
	public void examples(Examples examples) {
		log("examples");
		// intentionally left blank
	}

	@Override
	public void startOfScenarioLifeCycle(Scenario scenario) {
		log("start of scenario");
		// intentionally left blank
	}

	@Override
	public void background(Background background) {
		log("background");
		// intentionally left blank
	}

	@Override
	public void scenario(Scenario scenario) {
		log("scenario");
		ScenarioModel model = new ScenarioModel(scenario);
		lastFeature().addScenario(model);
	}

	@Override
	public void step(Step step) {
		log("step");
		ScenarioModel scenario = lastFeature().getCurrentScenario();
		scenario.addStep(new StepModel(step));
	}

	@Override
	public void endOfScenarioLifeCycle(Scenario scenario) {
		log("end of scenario");
		// intentionally left blank
	}

	@Override
	public void done() {
		log("done");
		// intentionally left blank
	}

	@Override
	public void close() {
		log("close");
		// intentionally left blank
	}

	@Override
	public void eof() {
		log("eof");
		// intentionally left blank
	}

	private void log(String msg) {
		LOGGER.info(msg);
	}
}
