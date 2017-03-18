package io.pickles.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;

public class FeatureModel {

	private Integer id;
	private TestRun testRun;
	private DateTime startedAt;
	private DateTime finishedAt;
	private Feature feature;
	private String uri;
	private List<ScenarioModel> scenarioModels = new ArrayList<>();

	private ScenarioModel current;

	public static FeatureModel fromJson(String json) {
		Gson gson = ModelGsonBuilder.gson();
		return gson.fromJson(json, FeatureModel.class);
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Feature getFeature() {
		return feature;
	}

	public TestRun getTestRun() {
		return testRun;
	}

	public void setTestRun(TestRun testRun) {
		this.testRun = testRun;
	}

	public DateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(DateTime startedAt) {
		this.startedAt = startedAt;
	}

	public DateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(DateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public ScenarioModel getScenario(Scenario scenario) {
		for (ScenarioModel model : scenarioModels) {
			if (model.getScenarioId() == scenario.getId()) {
				return model;
			}
		}

		return null;
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

	public String getDescription() {
		return feature.getDescription();
	}

	public String getKeyword() {
		return feature.getKeyword();
	}

	public String getFeatureId() {
		return feature.getId();
	}

	public Integer getLine() {
		return feature.getLine();
	}

	public String getName() {
		return feature.getName();
	}

	public List<String> getTagNames() {
		return feature.getTags().stream().map(t -> t.getName()).collect(Collectors.toList());
	}

	public List<Tag> getTags() {
		return feature.getTags();
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void keepInitiatingScenariosOnly() {
		scenarioModels = scenarioModels.stream().filter(s -> s.isInitiation()).collect(Collectors.toList());
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

	public String toPrettyJson() {
		Gson gson = ModelGsonBuilder.builder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	public JsonObject toDeepJsonObject() {
		JsonObject featureJson = toJsonObject();
		JsonArray scenariosJson = new JsonArray();
		for (ScenarioModel scenarioModel : scenarioModels) {
			scenariosJson.add(scenarioModel.toDeepJsonObject());
		}
		featureJson.add("elements", scenariosJson);
		return featureJson;
	}

	public JsonObject toJsonObject() {
		Gson gson = ModelGsonBuilder.gson();
		return gson.toJsonTree(this).getAsJsonObject();
	}

	public ScenarioModel getFirstUnfinishedScenario() {
		for (ScenarioModel scenario : getScenarios()) {
			if (scenario.getFinishedAt() == null) {
				return scenario;
			}
		}

		return null;
	}

	public StepModel getFirstStepWithoutMatch() {
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
