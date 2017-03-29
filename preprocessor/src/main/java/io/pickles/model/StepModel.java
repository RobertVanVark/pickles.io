package io.pickles.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import gherkin.formatter.Argument;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Range;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Step;

public class StepModel {

	private Integer id;

	private Step step;
	private Match match;
	private Result result;
	private ScenarioModel scenario;
	private List<String> output = new ArrayList<>();

	public StepModel(Step step) {
		this.step = step;
	}

	StepModel(Step step, Match match, Result result) {
		this.step = step;
		this.match = match;
		this.result = result;
	}

	public static StepModel fromJson(String json) {
		Gson gson = ModelGsonBuilder.gson();
		return gson.fromJson(json, StepModel.class);
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	public void setScenario(ScenarioModel scenario) {
		this.scenario = scenario;
	}

	public ScenarioModel getScenario() {
		return scenario;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void addOutput(String text) {
		output.add(text);
	}

	public List<String> getOutput() {
		return output;
	}

	public boolean hasComments() {
		return getComments() != null && !getComments().isEmpty();
	}

	public boolean hasRows() {
		return getRows() != null && !getRows().isEmpty();
	}

	public List<Comment> getComments() {
		return step.getComments();
	}

	public List<DataTableRow> getDatatable() {
		return step.getRows();
	}

	public void setDatatable(List<DataTableRow> rows) {
		Step newStep = new Step(step.getComments(), step.getKeyword(), step.getName(), step.getLine(), rows,
				step.getDocString());
		step = newStep;
	}

	public String getKeyword() {
		return step.getKeyword();
	}

	public Integer getLine() {
		return step.getLine();
	}

	public Range getLineRange() {
		return step.getLineRange();
	}

	public Match getMatch() {
		return match;
	}

	public String getName() {
		return step.getName();
	}

	public void setName(String name) {
		Step tmp = new Step(getComments(), getKeyword(), name, getLine(), getRows(), null);
		setStep(tmp);
	}

	public List<Argument> getOutlineArgs() {
		return step.getOutlineArgs();
	}

	public Result getResult() {
		return result;
	}

	public String getStatus() {
		return result.getStatus();
	}

	public List<DataTableRow> getRows() {
		return step.getRows();
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

		gherkinList.add(getKeyword() + getName());
		if (hasRows()) {
			for (DataTableRow row : getRows()) {
				gherkinList.add("| " + String.join(" | ", row.getCells()) + " |");
			}
		}

		return gherkinList;
	}

	public JsonObject toDeepJsonObject() {
		return toJsonObject();
	}

	public JsonObject toJsonObject() {
		Gson gson = ModelGsonBuilder.gson();
		return gson.toJsonTree(this).getAsJsonObject();
	}

	public JSONObject toJSON() {
		JSONObject stepJSON = new JSONObject();

		stepJSON.put("name", getName());
		stepJSON.put("keyword", getKeyword());
		stepJSON.put("line", getLine());

		if (hasComments()) {
			stepJSON.put("comments", commentsJSON());
		}

		stepJSON.put("match", matchJSON());

		if (hasRows()) {
			stepJSON.put("rows", rowsJSON());
		}

		stepJSON.put("result", resultJSON());

		return stepJSON;
	}

	private JSONArray rowsJSON() {
		JSONArray rowsJSON = new JSONArray();
		for (DataTableRow row : getRows()) {
			JSONObject rowJSON = new JSONObject();
			JSONArray cellsJSON = new JSONArray();
			for (String cell : row.getCells()) {
				cellsJSON.put(cell);
			}
			rowJSON.put("cells", cellsJSON);
			rowsJSON.put(rowJSON);
		}
		return rowsJSON;
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

	private JSONObject resultJSON() {
		JSONObject resultJSON = new JSONObject();
		if (hasResult()) {
			resultJSON.put("status", result.getStatus());
			resultJSON.putOpt("duration", result.getDuration());
		} else {
			resultJSON.put("status", "undefined");
		}
		return resultJSON;
	}

	private JSONObject matchJSON() {
		JSONObject matchJSON = new JSONObject();
		if (hasMatch()) {
			matchJSON.put("location", match.getLocation());
		}
		return matchJSON;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public boolean hasMatch() {
		return match != null;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public boolean hasResult() {
		return result != null;
	}
}
