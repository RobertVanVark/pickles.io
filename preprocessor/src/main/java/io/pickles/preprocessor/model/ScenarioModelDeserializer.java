package io.pickles.preprocessor.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;

class ScenarioModelDeserializer implements JsonDeserializer<ScenarioModel> {
	@Override
	public ScenarioModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject scenarioJson = json.getAsJsonObject();

		String keyword = scenarioJson.get("keyword").getAsString();
		String name = scenarioJson.get("name").getAsString();
		String description = scenarioJson.get("description").getAsString();
		Integer line = scenarioJson.get("line").getAsInt();
		String id = scenarioJson.get("id").getAsString();

		List<Comment> comments = constructComments(scenarioJson);
		List<Tag> tags = constructTags(scenarioJson);

		Scenario scenario = new Scenario(comments, tags, keyword, name, description, line, id);
		return new ScenarioModel(scenario);
	}

	private List<Comment> constructComments(JsonObject scenarioJson) {
		List<Comment> comments = new ArrayList<>();
		if (scenarioJson.has("comments")) {
			for (JsonElement element : scenarioJson.get("comments").getAsJsonArray()) {
				Comment comment = new Comment(element.getAsJsonObject().get("value").getAsString(),
						element.getAsJsonObject().get("line").getAsInt());
				comments.add(comment);
			}
		}
		return comments;
	}

	private List<Tag> constructTags(JsonObject scenarioJson) {
		List<Tag> tags = new ArrayList<>();
		if (scenarioJson.has("tags")) {
			for (JsonElement element : scenarioJson.get("tags").getAsJsonArray()) {
				Tag tag = new Tag(element.getAsJsonObject().get("name").getAsString(),
						element.getAsJsonObject().get("line").getAsInt());
				tags.add(tag);
			}
		}
		return tags;
	}
}
