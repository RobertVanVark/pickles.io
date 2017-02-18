package nl.devon.pickles.preprocessor.model;

import java.util.Collection;
import java.util.List;

import gherkin.formatter.Argument;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Range;
import gherkin.formatter.model.Step;

public class StepModel {

	private Step step;

	public StepModel(Step step) {
		this.step = step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	public Step getStep() {
		return step;
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
}
