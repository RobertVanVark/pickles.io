package io.pickles.preprocessor.model;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Tag;

class ScenarioModelSerializer implements JsonSerializer<ScenarioModel> {

	@Override
	public JsonElement serialize(ScenarioModel src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add("line", new JsonPrimitive(src.getLine()));
		json.add("steps", new JsonArray());
		json.add("name", new JsonPrimitive(src.getName()));
		json.add("description", new JsonPrimitive(src.getDescription()));
		json.add("keyword", new JsonPrimitive(src.getKeyword()));
		json.add("id", new JsonPrimitive(src.getId()));
		json.add("type", new JsonPrimitive(src.getKeyword().toLowerCase()));

		if (src.hasTags()) {
			json.add("tags", tagsJson(src));
		}

		if (src.hasComments()) {
			json.add("comments", commentsJson(src));
		}

		return json;
	}

	private JsonArray tagsJson(ScenarioModel src) {
		JsonArray tagsJson = new JsonArray();
		for (Tag tag : src.getTags()) {
			JsonObject json = new JsonObject();
			json.add("name", new JsonPrimitive(tag.getName()));
			json.add("line", new JsonPrimitive(tag.getLine()));
			tagsJson.add(json);
		}
		return tagsJson;
	}

	private JsonArray commentsJson(ScenarioModel src) {
		JsonArray commentsJson = new JsonArray();
		for (Comment comment : src.getComments()) {
			JsonObject json = new JsonObject();
			json.add("value", new JsonPrimitive(comment.getValue()));
			json.add("line", new JsonPrimitive(comment.getLine()));
			commentsJson.add(json);
		}
		return commentsJson;
	}
}
