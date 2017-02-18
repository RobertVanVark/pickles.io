package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;

public class ScenarioModel {

	private Scenario scenario;
	private FeatureModel feature;
	private List<StepModel> steps = new ArrayList<>();

	public ScenarioModel(Scenario scenario) {
		this.scenario = scenario;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void addStep(StepModel step) {
		steps.add(step);
	}

	public List<StepModel> getSteps() {
		return steps;
	}

	public StepModel getStep(int i) {
		return steps.get(i);
	}

	public StepModel getLastStep() {
		return steps.get(steps.size() - 1);
	}

	public void setFeature(FeatureModel feature) {
		this.feature = feature;
	}

	public FeatureModel getFeature() {
		return feature;
	}

	public boolean hasTags() {
		return !scenario.getTags().isEmpty();
	}

	public List<String> getTagNames() {
		return scenario.getTags().stream().map(t -> t.getName()).collect(Collectors.toList());
	}

	public void addTag(String name) {
		List<Tag> tags = new ArrayList<>(scenario.getTags());
		tags.add(new Tag(name, 0));
		scenario = new Scenario(scenario.getComments(), tags, scenario.getKeyword(), scenario.getName(),
				scenario.getDescription(), scenario.getLine(), scenario.getId());
	}

	public String getName() {
		return scenario.getName();
	}

	public String toGherkin() {
		return String.join(System.getProperty("line.separator"), toGherkinList());
	}

	public List<String> toGherkinList() {
		List<String> gherkinList = new ArrayList<>();

		if (hasTags()) {
			gherkinList.add(String.join(" ", getTagNames()));
		}

		gherkinList.add(scenario.getKeyword() + ": " + getName());

		for (StepModel step : steps) {
			for (String stepGherkin : step.toGherkinList()) {
				gherkinList.add("    " + stepGherkin);
			}
		}

		return gherkinList;

	}
}
