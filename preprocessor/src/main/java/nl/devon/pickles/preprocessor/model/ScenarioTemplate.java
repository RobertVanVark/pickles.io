package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;

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

	public void setFeatureTemplate(FeatureTemplate featureTemplate) {
		this.featureTemplate = featureTemplate;
	}

	public FeatureTemplate getFeatureTemplate() {
		return featureTemplate;
	}

	public boolean hasTags() {
		return !scenario.getTags().isEmpty();
	}

	public List<String> getTagNames() {
		return scenario.getTags().stream().map(t -> t.getName()).collect(Collectors.toList());
	}

	public void addTag(String name) {
		Tag tag = new Tag(name, 0);
		scenario.getTags().add(tag);
	}

	public String getName() {
		return scenario.getName();
	}
}
