package io.pickles.model;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.iterableWithSize;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertThat;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Step;

public class StepModelDeserializerShould {

	@Test
	public void constructRequiredFieldsFromJson() {
		Step step = new Step(Collections.emptyList(), "Given", "a step", 25, Collections.emptyList(), null);
		StepModel stepModel = new StepModel(step);
		String json = stepModel.toJsonObject().toString();

		StepModel model = StepModel.fromJson(json);
		assertThat(model.getKeyword(), equalTo("Given"));
		assertThat(model.getName(), equalTo("a step"));
		assertThat(model.getLine(), equalTo(25));

		assertThat(model.getComments(), emptyIterable());
		assertThat(model.getRows(), emptyIterable());
	}

	@Test
	public void constructOptionalCommentsFromJson() {
		List<Comment> comments = Arrays.asList(new Comment("comment", 1), new Comment("another comment", 2));
		StepModel stepModel = StepModelShould.modelWithComments(comments);
		String json = stepModel.toJsonObject().toString();

		StepModel model = StepModel.fromJson(json);
		assertThat(model.getComments(), hasSize(2));
		assertThat(model.getComments().get(0).getLine(), equalTo(1));
		assertThat(model.getComments().get(1).getValue(), equalTo("another comment"));
	}

	@Test
	public void constructOptionalRowsFromJson() {
		List<DataTableRow> rows = Arrays.asList(
				new DataTableRow(Collections.emptyList(), Arrays.asList("h1", "h2", "h4"), 1),
				new DataTableRow(Collections.emptyList(), Arrays.asList("r1v1", "r1v2", "r1v4"), 2),
				new DataTableRow(Collections.emptyList(), Arrays.asList("r2v1", "r2v2", "r2v4"), 3));
		StepModel stepModel = StepModelShould.modelWithRows(rows);
		String json = stepModel.toJsonObject().toString();

		StepModel model = StepModel.fromJson(json);
		assertThat(model.getRows(), hasSize(3));
		assertThat(model.getRows().get(0).getCells().get(0), equalTo("h1"));
		assertThat(model.getRows().get(1).getCells().get(1), equalTo("r1v2"));
		assertThat(model.getRows().get(2).getCells().get(2), equalTo("r2v4"));
	}

	@Test
	public void constructOptionalErrorFromJson() {
		StepModel stepModel = StepModelShould.modelWithName("error step");

		Result result = new Result("status", 999L, new Throwable("error message"), null);
		stepModel.setResult(result);
		String json = stepModel.toJsonObject().toString();
		StepModel model = StepModel.fromJson(json);
		assertThat(model.getResult().getErrorMessage(), equalTo(stepModel.getResult().getErrorMessage()));

		result = new Result("status", 999L, "error message");
		stepModel.setResult(result);
		json = stepModel.toJsonObject().toString();
		model = StepModel.fromJson(json);
		assertThat(model.getResult().getErrorMessage(), equalTo(stepModel.getResult().getErrorMessage()));
	}

	@Test
	public void constructOptionalOutputFromJson() {
		StepModel stepModel = StepModelShould.modelWithName("step");
		stepModel.addOutput("sample output text");
		stepModel.addOutput("text");
		String json = stepModel.toJsonObject().toString();

		StepModel model = StepModel.fromJson(json);
		assertThat(model.getOutput(), iterableWithSize(2));
		assertThat(model.getOutput().get(0), equalTo("sample output text"));
		assertThat(model.getOutput().get(1), equalTo("text"));
	}
}
