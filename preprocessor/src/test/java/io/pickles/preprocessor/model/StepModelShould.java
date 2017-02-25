package io.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.DocString;
import gherkin.formatter.model.Step;
import io.pickles.preprocessor.model.ScenarioModel;
import io.pickles.preprocessor.model.StepModel;

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
		StepModel stepModel = modelWith(step);

		assertThat(stepModel.getStep(), is(step));
	}

	@Test
	public void haveSameNameAsCucumberStep() {
		StepModel stepModel = modelWithName("step name");
		assertThat(stepModel.getName(), is("step name"));

		stepModel = modelWithName("another name");
		assertThat(stepModel.getName(), is("another name"));
	}

	@Test
	public void haveSameKeywordAsCucumberStep() {
		StepModel stepModel = modelWithKeyword("Given");
		assertThat(stepModel.getKeyword(), is("Given"));

		stepModel = modelWithKeyword("Then");
		assertThat(stepModel.getKeyword(), is("Then"));
	}

	@Test
	public void knowItsScenario() {
		StepModel stepModel = modelWithName("step name");
		ScenarioModel scenario = new ScenarioModel(null);
		stepModel.setScenario(scenario);

		assertThat(stepModel.getScenario(), is(scenario));
	}

	@Test
	public void convertToGherkin() {
		StepModel stepModel = modelWith("Given ", "a step name");
		assertThat(stepModel.toGherkin(), is("Given a step name"));

		stepModel = modelWith("Then ", "another step name");
		assertThat(stepModel.toGherkin(), is("Then another step name"));
	}

	@Test
	public void convertToGherkinList() {
		StepModel stepModel = modelWith("Given ", "a step name");
		assertThat(stepModel.toGherkinList().size(), is(1));
		assertThat(stepModel.toGherkinList().get(0), is("Given a step name"));

		stepModel = modelWith("Then ", "yet another step name");
		assertThat(stepModel.toGherkinList().size(), is(1));
		assertThat(stepModel.toGherkinList().get(0), is("Then yet another step name"));
	}

	private StepModel modelWith(String keyword, String name) {
		Step step = new Step(Collections.emptyList(), keyword, name, -1, Collections.emptyList(), null);
		return new StepModel(step);
	}

	private StepModel modelWithName(String name) {
		return modelWith("", name);
	}

	private StepModel modelWithKeyword(String keyword) {
		Step step = new Step(Collections.emptyList(), keyword, "", -1, Collections.emptyList(), null);
		return new StepModel(step);
	}

	private StepModel modelWith(Step step) {
		return new StepModel(step);
	}
}
