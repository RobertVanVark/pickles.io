package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Tag;

public class FeatureModel {

	private Feature feature;
	private String uri;
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

	public boolean hasComments() {
		return feature.getComments() != null && !getComments().isEmpty();
	}

	public boolean hasTags() {
		return !feature.getTags().isEmpty();
	}

	public List<Comment> getComments() {
		return feature.getComments();
	}

	public String getKeyword() {
		return feature.getKeyword();
	}

	public String getName() {
		return feature.getName();
	}

	public List<String> getTagNames() {
		return feature.getTags().stream().map(t -> t.getName()).collect(Collectors.toList());
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

		for (ScenarioModel scenario : getScenarios()) {
			gherkinList.add("");
			gherkinList.addAll(scenario.toGherkinList());
		}

		return gherkinList;
	}

	public JSONObject toJSON() {
		JSONObject featureJSON = new JSONObject();

		featureJSON.put("id", feature.getId());
		featureJSON.put("description", feature.getDescription());

		if (hasComments()) {
			featureJSON.put("comments", commentsJSON());
		}

		if (hasTags()) {
			featureJSON.put("tags", tagsJSON());
		}

		featureJSON.put("name", getName());
		featureJSON.put("keyword", getKeyword());
		featureJSON.put("line", feature.getLine());

		JSONArray scenariosJSON = new JSONArray();
		for (ScenarioModel scenario : getScenarios()) {
			scenariosJSON.put(scenario.toJSON());
		}
		featureJSON.put("elements", scenariosJSON);

		return featureJSON;
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

	private JSONArray tagsJSON() {
		JSONArray tagsJSON = new JSONArray();
		for (Tag tag : feature.getTags()) {
			JSONObject tagJSON = new JSONObject();
			tagJSON.put("name", tag.getName());
			tagJSON.put("line", tag.getLine());
			tagsJSON.put(tagJSON);
		}
		return tagsJSON;
	}

	public StepModel getFirstUnmatchedStep() {
		return scenarioModels.stream().flatMap(s -> s.getSteps().stream()).filter(s -> {
			return !s.hasMatch();
		}).findFirst().get();
	}

	public StepModel getFirstStepWithoutResult() {
		return scenarioModels.stream().flatMap(s -> s.getSteps().stream()).filter(s -> {
			return !s.hasResult();
		}).findFirst().get();
	}

}
