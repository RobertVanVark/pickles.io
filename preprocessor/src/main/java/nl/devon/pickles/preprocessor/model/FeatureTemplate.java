package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;

import gherkin.formatter.model.Feature;

public class FeatureTemplate {

	private Feature feature;
	private List<ScenarioTemplate> scenarios = new ArrayList<>();
	private ScenarioTemplate current;

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Feature getFeature() {
		return feature;
	}

	public void addScenario(ScenarioTemplate scenarioTemplate) {
		scenarios.add(scenarioTemplate);
		scenarioTemplate.setFeature(this);
		current = scenarioTemplate;
	}

	public List<ScenarioTemplate> getScenarios() {
		return scenarios;
	}

	public ScenarioTemplate getCurrentScenario() {
		return current;
	}
}
