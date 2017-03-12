package io.pickles.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Step;

class StepModelDeserializer implements JsonDeserializer<StepModel> {
	@Override
	public StepModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject scenarioJson = json.getAsJsonObject();

		String keyword = scenarioJson.get("keyword").getAsString();
		String name = scenarioJson.get("name").getAsString();
		Integer line = scenarioJson.get("line").getAsInt();

		List<Comment> comments = constructComments(scenarioJson);
		List<DataTableRow> rows = constructRows(scenarioJson);

		Step step = new Step(comments, keyword, name, line, rows, null);
		return new StepModel(step);
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

	private List<DataTableRow> constructRows(JsonObject scenarioJson) {
		List<DataTableRow> rows = new ArrayList<>();
		if (scenarioJson.has("rows")) {
			for (JsonElement element : scenarioJson.get("rows").getAsJsonArray()) {
				List<String> cells = new ArrayList<String>();
				for (JsonElement cell : element.getAsJsonObject().get("cells").getAsJsonArray()) {
					cells.add(cell.getAsString());
				}
				rows.add(new DataTableRow(Collections.emptyList(), cells, null));
			}
		}
		return rows;
	}
}
