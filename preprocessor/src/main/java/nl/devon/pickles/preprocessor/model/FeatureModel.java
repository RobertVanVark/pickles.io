package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gherkin.formatter.model.Feature;

public class FeatureModel {

	private Feature feature;
	private List<ScenarioModel> scenarios = new ArrayList<>();
	private ScenarioModel current;

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Feature getFeature() {
		return feature;
	}

	public void addScenario(ScenarioModel scenarioTemplate) {
		scenarios.add(scenarioTemplate);
		scenarioTemplate.setFeatureTemplate(this);
		current = scenarioTemplate;
	}

	public List<ScenarioModel> getScenarios() {
		return scenarios;
	}

	public ScenarioModel getScenario(int i) {
		return scenarios.get(i);
	}

	public ScenarioModel getCurrentScenario() {
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

	public String toFeatureString() {
		StringBuffer buffer = new StringBuffer(64);
		buffer.append(String.join(" ", getTagNames()));
		buffer.append(System.getProperty("line.separator"));
		buffer.append(feature.getKeyword()).append(": ").append(getName());
		buffer.append(System.getProperty("line.separator"));
		buffer.append(System.getProperty("line.separator"));
		return buffer.toString();
	}
}
