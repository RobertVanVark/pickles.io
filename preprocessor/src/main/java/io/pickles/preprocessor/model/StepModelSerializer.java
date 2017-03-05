package io.pickles.preprocessor.model;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;

class StepModelSerializer implements JsonSerializer<StepModel> {

	@Override
	public JsonElement serialize(StepModel src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add("line", new JsonPrimitive(src.getLine()));
		json.add("name", new JsonPrimitive(src.getName()));
		json.add("keyword", new JsonPrimitive(src.getKeyword()));

		json.add("match", matchJson(src));

		json.add("result", resultJson(src));

		if (src.hasComments()) {
			json.add("comments", commentsJson(src));
		}

		if (src.hasRows()) {
			json.add("rows", rowsJson(src));
		}
		return json;
	}

	private JsonArray rowsJson(StepModel src) {
		JsonArray rowsJSON = new JsonArray();
		for (DataTableRow row : src.getRows()) {
			JsonObject rowJson = new JsonObject();
			JsonArray cellsJson = new JsonArray();
			for (String cell : row.getCells()) {
				cellsJson.add(new JsonPrimitive(cell));
			}
			rowJson.add("cells", cellsJson);
			rowsJSON.add(rowJson);
		}
		return rowsJSON;
	}

	private JsonArray commentsJson(StepModel src) {
		JsonArray commentsJson = new JsonArray();
		for (Comment comment : src.getComments()) {
			JsonObject json = new JsonObject();
			json.add("value", new JsonPrimitive(comment.getValue()));
			json.add("line", new JsonPrimitive(comment.getLine()));
			commentsJson.add(json);
		}
		return commentsJson;
	}

	private JsonObject resultJson(StepModel src) {
		JsonObject resultJson = new JsonObject();
		if (src.hasResult()) {
			resultJson.add("status", new JsonPrimitive(src.getResult().getStatus()));
			resultJson.add("duration", new JsonPrimitive(src.getResult().getDuration()));
		} else {
			resultJson.add("status", new JsonPrimitive("undefined"));
		}
		return resultJson;
	}

	private JsonObject matchJson(StepModel src) {
		JsonObject matchJson = new JsonObject();
		if (src.hasMatch()) {
			matchJson.add("location", new JsonPrimitive(src.getMatch().getLocation()));
		}
		return matchJson;
	}
}
