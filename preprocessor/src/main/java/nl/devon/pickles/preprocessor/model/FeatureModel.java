package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gherkin.formatter.model.Feature;

public class FeatureModel {

	private Feature feature;
	private List<ScenarioModel> scenarioModels = new ArrayList<>();
	private ScenarioModel current;

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Feature getFeature() {
		return feature;
	}

	public void addScenarioModel(ScenarioModel scenarioModel) {
		scenarioModels.add(scenarioModel);
		scenarioModel.setFeatureModel(this);
		current = scenarioModel;
	}

	public List<ScenarioModel> getScenarios() {
		return scenarioModels;
	}

	public ScenarioModel getScenario(int i) {
		return scenarioModels.get(i);
	}

	public ScenarioModel getCurrentScenarioModel() {
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

	public String toGherkin() {
		StringBuffer buffer = new StringBuffer(64);
		buffer.append(String.join(" ", getTagNames()));
		buffer.append(System.getProperty("line.separator"));
		buffer.append(feature.getKeyword()).append(": ").append(getName());
		buffer.append(System.getProperty("line.separator"));
		buffer.append(System.getProperty("line.separator"));
		for (ScenarioModel scenario : getScenarios()) {
			buffer.append(scenario.toGherkin());
		}
		return buffer.toString();
	}
}
