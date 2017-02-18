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

	public void addScenario(ScenarioModel scenario) {
		scenarioModels.add(scenario);
		scenario.setFeature(this);
		current = scenario;
	}

	public List<ScenarioModel> getScenarios() {
		return scenarioModels;
	}

	public ScenarioModel getScenario(int i) {
		return scenarioModels.get(i);
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

	public String getKeyword() {
		return feature.getKeyword();
	}

	public String getName() {
		return feature.getName();
	}

	public String toGherkin() {
		return String.join(System.getProperty("line.separator"), toGherkinList());
	}

	public List<String> toGherkinList() {
		List<String> gherkinList = new ArrayList<>();

		if (hasTags()) {
			gherkinList.add(String.join(" ", getTagNames()));
		}

		gherkinList.add(getKeyword() + ": " + getName());

		for (ScenarioModel scenario : getScenarios()) {
			gherkinList.add("");
			gherkinList.addAll(scenario.toGherkinList());
		}

		return gherkinList;
	}
}
