package nl.devon.pickles.preprocessor;

import java.util.ArrayList;
import java.util.List;

import gherkin.formatter.model.Step;
import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.preprocessor.model.ScenarioTemplate;

public class FeatureWriter {

	private FeatureTemplate featureTemplate;
	private List<String> content = new ArrayList<>();

	public FeatureWriter(FeatureTemplate featureTemplate) {
		this.featureTemplate = featureTemplate;
	}

	public List<String> generate() {

		generateFeature();
		for (ScenarioTemplate scenarioTemplate : featureTemplate.getScenarios()) {
			generateScenario(scenarioTemplate);
		}

		return content;
	}

	public void generateFeature() {
		if (featureTemplate.hasTags()) {
			content.add(String.join(" ", featureTemplate.getTagNames()));
		}
		content.add("Feature: " + featureTemplate.getName());
	}

	public void generateScenario(ScenarioTemplate scenarioTemplate) {
		content.add("");
		if (scenarioTemplate.hasTags()) {
			content.add(String.join(" ", scenarioTemplate.getTagNames()));
		}
		content.add("Scenario: " + scenarioTemplate.getName());
		for (Step step : scenarioTemplate.getSteps()) {
			content.add("    " + step.getKeyword() + step.getName());
		}
	}

}
