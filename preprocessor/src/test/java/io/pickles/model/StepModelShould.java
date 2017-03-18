package io.pickles.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
		StepModel stepModel = modelWith(step);

		assertThat(stepModel.getStep(), equalTo(step));
	}

	@Test
	public void haveSameNameAsCucumberStep() {
		StepModel stepModel = modelWithName("step name");
		assertThat(stepModel.getName(), equalTo("step name"));

		stepModel = modelWithName("another name");
		assertThat(stepModel.getName(), equalTo("another name"));
	}

	@Test
	public void haveSameKeywordAsCucumberStep() {
		StepModel stepModel = modelWithKeyword("Given");
		assertThat(stepModel.getKeyword(), equalTo("Given"));

		stepModel = modelWithKeyword("Then");
		assertThat(stepModel.getKeyword(), equalTo("Then"));
	}

	@Test
	public void knowItsScenario() {
		StepModel stepModel = modelWithName("step name");
		ScenarioModel scenario = new ScenarioModel(null);
		stepModel.setScenario(scenario);

		assertThat(stepModel.getScenario(), equalTo(scenario));
	}

	@Test
	public void convertToGherkin() {
		StepModel stepModel = modelWith("Given ", "a step name", Collections.emptyList(), Collections.emptyList());
		assertThat(stepModel.toGherkin(), equalTo("Given a step name"));

		stepModel = modelWith("Then ", "another step name", Collections.emptyList(), Collections.emptyList());
		assertThat(stepModel.toGherkin(), equalTo("Then another step name"));
	}

	@Test
	public void convertToGherkinList() {
		StepModel stepModel = modelWith("Given ", "a step name", Collections.emptyList(), Collections.emptyList());
		assertThat(stepModel.toGherkinList().size(), equalTo(1));
		assertThat(stepModel.toGherkinList().get(0), equalTo("Given a step name"));

		stepModel = modelWith("Then ", "yet another step name", Collections.emptyList(), Collections.emptyList());
		assertThat(stepModel.toGherkinList().size(), equalTo(1));
		assertThat(stepModel.toGherkinList().get(0), equalTo("Then yet another step name"));
	}

	@Test
	public void convertRequiredFieldsIntoJsonObject() {
		StepModel stepModel = modelWith("Given", "a step name", Collections.emptyList(), Collections.emptyList());
		JsonObject jsonObject = stepModel.toJsonObject().getAsJsonObject();
		System.out.println(jsonObject);
		assertThat(jsonObject.get("line").getAsInt(), equalTo(stepModel.getLine()));
		assertThat(jsonObject.get("name").getAsString(), equalTo(stepModel.getName()));
		assertThat(jsonObject.get("keyword").getAsString(), equalTo(stepModel.getKeyword()));

		assertThat(jsonObject.getAsJsonObject("match"), notNullValue());

		JsonObject resultJson = jsonObject.getAsJsonObject("result");
		assertThat(resultJson.get("status").getAsString(), equalTo("undefined"));
	}

	@Test
	public void convertOptionalRowsIntoJsonObject() {
		List<DataTableRow> rows = Arrays.asList(
				new DataTableRow(Collections.emptyList(), Arrays.asList("h1", "h2", "h4"), 1),
				new DataTableRow(Collections.emptyList(), Arrays.asList("r1v1", "r1v2", "r1v4"), 2),
				new DataTableRow(Collections.emptyList(), Arrays.asList("r2v1", "r2v2", "r2v4"), 3));
		StepModel stepModel = modelWithRows(rows);

		JsonObject jsonObject = stepModel.toJsonObject();
		JsonElement jsonElement = jsonObject.getAsJsonObject().get("rows");
		JsonArray rowsGson = jsonElement.getAsJsonArray();
		assertThat(rowsGson.size(), equalTo(3));

		JsonObject row = rowsGson.get(0).getAsJsonObject();
		assertThat(row.get("cells").getAsJsonArray().size(), equalTo(3));
		assertThat(row.get("cells").getAsJsonArray().get(0).getAsString(), equalTo("h1"));

		row = rowsGson.get(1).getAsJsonObject();
		assertThat(row.get("cells").getAsJsonArray().size(), equalTo(3));
		assertThat(row.get("cells").getAsJsonArray().get(1).getAsString(), equalTo("r1v2"));

		row = rowsGson.get(2).getAsJsonObject();
		assertThat(row.get("cells").getAsJsonArray().size(), equalTo(3));
		assertThat(row.get("cells").getAsJsonArray().get(2).getAsString(), equalTo("r2v4"));
	}

	@Test
	public void convertOptionalCommentsIntoJsonObject() {
		List<Comment> comments = Arrays.asList(new Comment("comment", 1), new Comment("another comment", 2));
		StepModel stepModel = modelWithComments(comments);

		JsonObject jsonObject = stepModel.toJsonObject();
		JsonElement jsonElement = jsonObject.getAsJsonObject().get("comments");
		JsonArray commentsGson = jsonElement.getAsJsonArray();
		assertThat(commentsGson.size(), equalTo(2));

		JsonObject comment = commentsGson.get(0).getAsJsonObject();
		assertThat(comment.get("value").getAsString(), equalTo("comment"));
		assertThat(comment.get("line").getAsInt(), equalTo(1));

		comment = commentsGson.get(1).getAsJsonObject();
		assertThat(comment.get("value").getAsString(), equalTo("another comment"));
		assertThat(comment.get("line").getAsInt(), equalTo(2));
	}

	static StepModel modelWithName(String name) {
		return modelWith("", name, Collections.emptyList(), Collections.emptyList());
	}

	static StepModel modelWithKeyword(String keyword) {
		return modelWith(keyword, "", Collections.emptyList(), Collections.emptyList());
	}

	static StepModel modelWithComments(List<Comment> comments) {
		return modelWith("", "", comments, Collections.emptyList());
	}

	static StepModel modelWithRows(List<DataTableRow> rows) {
		return modelWith("", "", Collections.emptyList(), rows);
	}

	static StepModel modelWith(String keyword, String name, List<Comment> comments, List<DataTableRow> rows) {
		Step step = new Step(comments, keyword, name, -1, rows, null);
		return new StepModel(step);
	}

	static StepModel modelWith(Step step) {
		return new StepModel(step);
	}
}
