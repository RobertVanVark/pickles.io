package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	public String getKeyword() {
		return step.getKeyword();
	}

	public String getName() {
		return step.getName();
	}

	public Integer getLine() {
		return step.getLine();
	}

	public Collection<Comment> getComments() {
		return step.getComments();
	}

	public Range getLineRange() {
		return step.getLineRange();
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

		gherkinList.add(getKeyword() + getName());

		return gherkinList;
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
