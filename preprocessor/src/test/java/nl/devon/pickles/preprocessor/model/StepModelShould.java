package nl.devon.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.DocString;
import gherkin.formatter.model.Step;

public class StepModelShould {

	@Test
	public void wrapStep() {
		List<Comment> comments = new ArrayList<>();
		String keyword = "";
		String name = "";
		Integer line = 0;
		List<DataTableRow> rows = new ArrayList<>();
		DocString docString = new DocString("", "", 0);
		Step step = new Step(comments, keyword, name, line, rows, docString);
		StepModel model = modelWith(step);

		assertThat(model.getStep(), is(step));
	}

	private StepModel modelWith(Step step) {
		return new StepModel(step);
	}
}
