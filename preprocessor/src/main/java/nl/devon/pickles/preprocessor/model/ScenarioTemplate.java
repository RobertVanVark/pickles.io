package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;

import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;

public class ScenarioTemplate {

	private Scenario scenario;
	private FeatureTemplate featureTemplate;
	private List<Step> steps = new ArrayList<>();

	public Scenario getScenario() {
		return scenario;
	}

	public void setSCenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public void addStep(Step step) {
		steps.add(step);
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setFeature(FeatureTemplate featureTemplate) {
		this.featureTemplate = featureTemplate;
	}

	public FeatureTemplate getFeatureTemplate() {
		return featureTemplate;
	}
}
