package nl.devon.pickles.preprocessor;

import java.util.List;

import gherkin.formatter.Formatter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;
import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.preprocessor.model.ScenarioTemplate;

public class TemplateFormatter implements Formatter {

	private FeatureTemplate featureTemplate;

	public TemplateFormatter(Appendable appendable, FeatureTemplate featureTemplate) {
		this.featureTemplate = featureTemplate;
	}

	@Override
	public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {
		// intentionally left blank
	}

	@Override
	public void uri(String s) {
		// intentionally left blank
	}

	@Override
	public void feature(Feature feature) {
		featureTemplate.setFeature(feature);
	}

	@Override
	public void scenarioOutline(ScenarioOutline scenarioOutline) {
		// intentionally left blank
	}

	@Override
	public void examples(Examples examples) {
		// intentionally left blank
	}

	@Override
	public void startOfScenarioLifeCycle(Scenario scenario) {
		// intentionally left blank
	}

	@Override
	public void background(Background background) {
		// intentionally left blank
	}

	@Override
	public void scenario(Scenario scenario) {
		ScenarioTemplate template = new ScenarioTemplate();
		template.setSCenario(scenario);
		featureTemplate.addScenario(template);
	}

	@Override
	public void step(Step step) {
		ScenarioTemplate scenario = featureTemplate.getCurrentScenario();
		scenario.addStep(step);
	}

	@Override
	public void endOfScenarioLifeCycle(Scenario scenario) {
		// intentionally left blank
	}

	@Override
	public void done() {
		// intentionally left blank
	}

	@Override
	public void close() {
		// intentionally left blank
	}

	@Override
	public void eof() {
		// intentionally left blank
	}
}
