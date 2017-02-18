package nl.devon.pickles.preprocessor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gherkin.formatter.Argument;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Range;
import gherkin.formatter.model.Step;

public class StepModel {

	private Step step;
	private ScenarioModel scenario;

	public StepModel(Step step) {
		this.step = step;
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
}
