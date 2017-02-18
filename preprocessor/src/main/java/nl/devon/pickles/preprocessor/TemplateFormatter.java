package nl.devon.pickles.preprocessor;

import java.util.List;

import gherkin.formatter.Formatter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;
import nl.devon.pickles.preprocessor.model.FeatureModel;
import nl.devon.pickles.preprocessor.model.ScenarioModel;
import nl.devon.pickles.preprocessor.model.StepModel;

public class TemplateFormatter implements Formatter {

	private FeatureModel featureModel;

	public TemplateFormatter(Appendable appendable, FeatureModel featureModel) {
		this.featureModel = featureModel;
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
		featureModel.setFeature(feature);
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
		ScenarioModel model = new ScenarioModel(scenario);
		featureModel.addScenario(model);
	}

	@Override
	public void step(Step step) {
		ScenarioModel scenario = featureModel.getCurrentScenario();
		scenario.addStep(new StepModel(step));
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
