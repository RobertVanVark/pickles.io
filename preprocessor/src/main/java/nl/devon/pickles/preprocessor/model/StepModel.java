package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gherkin.formatter.Argument;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Range;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Step;

public class StepModel {

	private Step step;
	private Match match;
	private Result result;
	private ScenarioModel scenario;

	public StepModel(Step step) {
		this.step = step;
	}

	protected StepModel(Step step, Match match, Result result) {
		this.step = step;
		this.match = match;
		this.result = result;
	}

	public Step getStep() {
		return step;
	}

	public void setScenario(ScenarioModel scenario) {
		this.scenario = scenario;
	}

	public ScenarioModel getScenario() {
		return scenario;
	}

	public boolean hasComments() {
		return getComments() != null && !getComments().isEmpty();
	}

	public Collection<Comment> getComments() {
		return step.getComments();
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

	public String getName() {
		return step.getName();
	}

	public List<Argument> getOutlineArgs() {
		return step.getOutlineArgs();
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

		return gherkinList;
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
		stepJSON.put("result", resultJSON());

		return stepJSON;
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
		resultJSON.put("status", result.getStatus());
		resultJSON.putOpt("duration", result.getDuration());
		return resultJSON;
	}

	private JSONObject matchJSON() {
		JSONObject matchJSON = new JSONObject();
		matchJSON.put("location", match.getLocation());
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
