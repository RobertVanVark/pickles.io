package io.pickles.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;

public class ScenarioModel {

	private Integer id;
	private DateTime startedAt;
	private DateTime finishedAt;
	private String triggeringDvId;
	private String nextDvId;

	private Scenario scenario;
	private FeatureModel feature;
	private List<StepModel> stepModels = new ArrayList<>();

	public ScenarioModel(Scenario scenario) {
		this.scenario = scenario;
	}

	public static ScenarioModel fromJson(String json) {
		Gson gson = ModelGsonBuilder.gson();
		return gson.fromJson(json, ScenarioModel.class);
	}

	public Scenario getScenario() {
		return scenario;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getTriggeringDvId() {
		return triggeringDvId;
	}

	public void setTriggeringDvId(String triggeringDvId) {
		this.triggeringDvId = triggeringDvId;
	}

	public String getNextDvId() {
		return nextDvId;
	}

	public void setNextDvId(String nextDvId) {
		this.nextDvId = nextDvId;
	}

	public void addStep(StepModel step) {
		stepModels.add(step);
		step.setScenario(this);
	}

	public void removeLastStep() {
		if (stepModels.size() > 0) {
			stepModels.remove(stepModels.size() - 1);
		}
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

	public boolean isInitiation() {
		return triggeringDvId == null || triggeringDvId.isEmpty();
	}

	public boolean isFinalScenario() {
		return nextDvId == null || nextDvId.isEmpty();
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
		scenario = new Scenario(getComments(), tags, getKeyword(), getName(), getDescription(), getLine(),
				getScenarioId());
	}

	public List<Comment> getComments() {
		return scenario.getComments();
	}

	public String getDescription() {
		return scenario.getDescription();
	}

	public String getScenarioId() {
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

		for (StepModel step : stepModels) {
			for (String stepGherkin : step.toGherkinList()) {
				gherkinList.add("    " + stepGherkin);
			}
		}

		return gherkinList;
	}

	public String toPrettyGson() {
		Gson gson = ModelGsonBuilder.builder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	public JsonObject toDeepJsonObject() {
		JsonObject scenarioJson = toJsonObject();
		JsonArray stepsJson = new JsonArray();
		for (StepModel stepModel : stepModels) {
			stepsJson.add(stepModel.toDeepJsonObject());
		}
		scenarioJson.add("steps", stepsJson);
		return scenarioJson;
	}

	public JsonObject toJsonObject() {
		Gson gson = ModelGsonBuilder.gson();
		return gson.toJsonTree(this).getAsJsonObject();
	}
}
