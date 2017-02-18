package nl.devon.pickles.preprocessor;

import java.util.ArrayList;
import java.util.List;

import nl.devon.pickles.preprocessor.model.FeatureModel;
import nl.devon.pickles.preprocessor.model.ScenarioModel;
import nl.devon.pickles.preprocessor.model.StepModel;

public class FeatureWriter {

	private FeatureModel featureModel;
	private List<String> content = new ArrayList<>();

	public FeatureWriter(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}

	public List<String> generate() {

		generateFeature();
		for (ScenarioModel scenarioTemplate : featureModel.getScenarios()) {
			generateScenario(scenarioTemplate);
		}

		return content;
	}

	public void generateFeature() {
		if (featureModel.hasTags()) {
			content.add(String.join(" ", featureModel.getTagNames()));
		}
		content.add("Feature: " + featureModel.getName());
	}

	public void generateScenario(ScenarioModel scenarioTemplate) {
		content.add("");
		if (scenarioTemplate.hasTags()) {
			content.add(String.join(" ", scenarioTemplate.getTagNames()));
		}
		content.add("Scenario: " + scenarioTemplate.getName());
		for (StepModel step : scenarioTemplate.getSteps()) {
			content.add("    " + step.getKeyword() + step.getName());
		}
	}

}
