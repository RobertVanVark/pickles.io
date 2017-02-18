package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;

public class ScenarioModel {

	private Scenario scenario;
	private FeatureModel featureModel;
	private List<StepModel> stepModels = new ArrayList<>();

	public Scenario getScenario() {
		return scenario;
	}

	public void setSCenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public void addStep(StepModel step) {
		stepModels.add(step);
	}

	public List<StepModel> getSteps() {
		return stepModels;
	}

	public StepModel getStep(int i) {
		return stepModels.get(i);
	}

	public StepModel getLastStep() {
		return stepModels.get(stepModels.size() - 1);
	}

	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
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
		StringBuffer buffer = new StringBuffer(64);
		buffer.append(String.join(" ", getTagNames()));
		buffer.append(System.getProperty("line.separator"));
		buffer.append(scenario.getKeyword()).append(": ").append(getName());
		buffer.append(System.getProperty("line.separator"));
		buffer.append(System.getProperty("line.separator"));
		return buffer.toString();
	}
}
