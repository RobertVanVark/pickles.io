package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
		scenarioTemplate.setFeatureTemplate(this);
		current = scenarioTemplate;
	}

	public List<ScenarioTemplate> getScenarios() {
		return scenarios;
	}

	public ScenarioTemplate getCurrentScenario() {
		return current;
	}

	public boolean hasTags() {
		return !feature.getTags().isEmpty();
	}

	public List<String> getTagNames() {
		return feature.getTags().stream().map(t -> t.getName()).collect(Collectors.toList());
	}

	public String getName() {
		return feature.getName();
	}
}
