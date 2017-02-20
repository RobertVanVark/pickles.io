package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import gherkin.formatter.model.Comment;
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

	public boolean hasComments() {
		return getComments() != null && !getComments().isEmpty();
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
		scenario = new Scenario(getComments(), tags, getKeyword(), getName(), getDescription(), getLine(), getId());
	}

	public List<Comment> getComments() {
		return scenario.getComments();
	}

	public String getDescription() {
		return scenario.getDescription();
	}

	public String getId() {
		return scenario.getId();
	}

	public String getKeyword() {
		return scenario.getKeyword();
	}

	public Integer getLine() {
		return scenario.getLine();
	}

	public String getName() {
		return scenario.getName();
	}

	public List<Tag> getTags() {
		return scenario.getTags();
	}

	public String toGherkin() {
		return String.join(System.getProperty("line.separator"), toGherkinList());
	}

	public List<String> toGherkinList() {
		List<String> gherkinList = new ArrayList<>();

		if (hasComments()) {
			for (Comment comment : getComments()) {
				gherkinList.add(comment.getValue());
			}
		}

		if (hasTags()) {
			gherkinList.add(String.join(" ", getTagNames()));
		}

		gherkinList.add(getKeyword() + ": " + getName());

		for (StepModel step : steps) {
			for (String stepGherkin : step.toGherkinList()) {
				gherkinList.add("    " + stepGherkin);
			}
		}

		return gherkinList;
	}

	public JSONObject toJSON() {
		JSONObject scenarioJSON = new JSONObject();

		scenarioJSON.put("id", getId());
		scenarioJSON.put("description", getDescription());

		if (hasTags()) {
			scenarioJSON.put("tags", tagsJSON());
		}

		if (hasComments()) {
			scenarioJSON.put("comments", commentsJSON());
		}

		scenarioJSON.put("name", getName());
		scenarioJSON.put("keyword", getKeyword());
		scenarioJSON.put("line", getLine());

		scenarioJSON.put("type", getKeyword().toLowerCase());

		JSONArray stepsJSON = new JSONArray();
		for (StepModel step : getSteps()) {
			stepsJSON.put(step.toJSON());
		}
		scenarioJSON.put("steps", stepsJSON);

		return scenarioJSON;
	}

	private JSONArray tagsJSON() {
		JSONArray tagsJSON = new JSONArray();
		for (Tag tag : getTags()) {
			JSONObject tagJSON = new JSONObject();
			tagJSON.put("name", tag.getName());
			tagJSON.put("line", tag.getLine());
			tagsJSON.put(tagJSON);
		}
		return tagsJSON;
	}

	private JSONArray commentsJSON() {
		JSONArray commentsJSON = new JSONArray();
		for (Comment comment : getComments()) {
			JSONObject commentJSON = new JSONObject();
			commentJSON.put("value", comment.getValue());
			commentJSON.put("line", comment.getLine());
			commentsJSON.put(commentJSON);
		}
		return commentsJSON;
	}
}
