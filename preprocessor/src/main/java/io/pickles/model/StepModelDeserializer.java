package io.pickles.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Step;

class StepModelDeserializer implements JsonDeserializer<StepModel> {
	@Override
	public StepModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

		JsonObject stepJson = json.getAsJsonObject();

		String keyword = stepJson.get("keyword").getAsString();
		String name = stepJson.get("name").getAsString();
		Integer line = stepJson.get("line").getAsInt();

		List<Comment> comments = constructComments(stepJson);
		List<DataTableRow> rows = constructRows(stepJson);
		Step step = new Step(comments, keyword, name, line, rows, null);
		StepModel stepModel = new StepModel(step);

		if (hasResult(stepJson)) {
			stepModel.setResult(constructResult(stepJson));
		}

		if (hasMatch(stepJson)) {
			stepModel.setMatch(constructMatch(stepJson));
		}

		return stepModel;
	}

	private List<Comment> constructComments(JsonObject stepJson) {
		List<Comment> comments = new ArrayList<>();
		if (stepJson.has("comments")) {
			for (JsonElement element : stepJson.get("comments").getAsJsonArray()) {
				Comment comment = new Comment(element.getAsJsonObject().get("value").getAsString(),
						element.getAsJsonObject().get("line").getAsInt());
				comments.add(comment);
			}
		}
		return comments;
	}

	private List<DataTableRow> constructRows(JsonObject stepJson) {
		List<DataTableRow> rows = new ArrayList<>();
		if (stepJson.has("rows")) {
			for (JsonElement element : stepJson.get("rows").getAsJsonArray()) {
				List<String> cells = new ArrayList<String>();
				for (JsonElement cell : element.getAsJsonObject().get("cells").getAsJsonArray()) {
					cells.add(cell.getAsString());
				}
				rows.add(new DataTableRow(Collections.emptyList(), cells, null));
			}
		}
		return rows;
	}

	private boolean hasResult(JsonObject stepJson) {
		JsonObject resultJson = stepJson.getAsJsonObject("result");
		String status = resultJson.get("status").getAsString();
		return !"undefined".equals(status);
	}

	private Result constructResult(JsonObject stepJson) {
		JsonObject resultJson = stepJson.getAsJsonObject("result");
		String status = resultJson.get("status").getAsString();
		long duration = resultJson.get("duration").getAsLong();
		String errorMessage = resultJson.get("error_message").getAsString();
		return new Result(status, duration, errorMessage);
	}

	private boolean hasMatch(JsonObject stepJson) {
		JsonObject matchJson = stepJson.getAsJsonObject("match");
		return null != matchJson.get("location");
	}

	private Match constructMatch(JsonObject stepJson) {
		JsonObject matchJson = stepJson.getAsJsonObject("match");
		String location = matchJson.get("location").getAsString();
		return new Match(Collections.emptyList(), location);
	}
}
