package io.pickles.model;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static org.junit.Assert.assertThat;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;

public class ScenarioModelSerializerShould {

	@Test
	public void convertRequiredFieldsIntoJsonObject() {
		ScenarioModel scenarioModel = ScenarioModelShould.modelWithName("scenario name");
		JsonObject jsonObject = scenarioModel.toJsonObject();
		assertThat(jsonObject.get("line").getAsInt(), equalTo(scenarioModel.getLine()));
		assertThat(jsonObject.get("name").getAsString(), equalTo(scenarioModel.getName()));
		assertThat(jsonObject.get("keyword").getAsString(), equalTo(scenarioModel.getKeyword()));
		assertThat(jsonObject.get("type").getAsString(), equalTo(scenarioModel.getKeyword().toLowerCase()));
		assertThat(jsonObject.get("description").getAsString(), equalTo(scenarioModel.getDescription()));
		assertThat(jsonObject.get("id").getAsString(), equalTo(scenarioModel.getScenarioId()));

		assertThat(jsonObject.get("steps").getAsJsonArray(), emptyIterable());
	}

	@Test
	public void convertOptionalTagsIntoJsonObject() {
		List<Tag> tags = Arrays.asList(new Tag("tag", 1), new Tag("another tag", 2));
		ScenarioModel scenarioModel = ScenarioModelShould.modelWithTags(tags);

		JsonArray tagsGson = scenarioModel.toJsonObject().get("tags").getAsJsonArray();
		assertThat(tagsGson.size(), equalTo(2));

		JsonObject tag = tagsGson.get(0).getAsJsonObject();
		assertThat(tag.get("name").getAsString(), equalTo("tag"));
		assertThat(tag.get("line").getAsInt(), equalTo(1));

		tag = tagsGson.get(1).getAsJsonObject();
		assertThat(tag.get("name").getAsString(), equalTo("another tag"));
		assertThat(tag.get("line").getAsInt(), equalTo(2));

	}

	@Test
	public void convertOptionalCommentsIntoJsonObject() {
		List<Comment> comments = Arrays.asList(new Comment("comment", 1), new Comment("another comment", 2));
		ScenarioModel scenarioModel = ScenarioModelShould.modelWithComments(comments);

		JsonArray commentsGson = scenarioModel.toJsonObject().get("comments").getAsJsonArray();
		assertThat(commentsGson.size(), equalTo(2));

		JsonObject comment = commentsGson.get(0).getAsJsonObject();
		assertThat(comment.get("value").getAsString(), equalTo("comment"));
		assertThat(comment.get("line").getAsInt(), equalTo(1));

		comment = commentsGson.get(1).getAsJsonObject();
		assertThat(comment.get("value").getAsString(), equalTo("another comment"));
		assertThat(comment.get("line").getAsInt(), equalTo(2));
	}

	@Test
	public void convertScenariosIntoDeepJsonObject() {
		ScenarioModel scenarioModel = ScenarioModelShould.modelWithName("A scenario");
		Step step = new Step(Collections.emptyList(), "", "a step", 1, Collections.emptyList(), null);
		scenarioModel.addStep(new StepModel(step));
		step = new Step(Collections.emptyList(), "", "some other step", 2, Collections.emptyList(), null);
		scenarioModel.addStep(new StepModel(step));

		JsonArray steps = scenarioModel.toDeepJsonObject().get("steps").getAsJsonArray();
		assertThat(steps.size(), equalTo(2));
		assertThat(steps.get(0).getAsJsonObject().get("name").getAsString(), equalTo("a step"));
		assertThat(steps.get(1).getAsJsonObject().get("name").getAsString(), equalTo("some other step"));
	}

	@Test
	public void notConvertStepsIntoJsonObject() {
		ScenarioModel scenarioModel = ScenarioModelShould.modelWithName("A scenario");
		Step step = new Step(Collections.emptyList(), "", "a step", 1, Collections.emptyList(), null);
		scenarioModel.addStep(new StepModel(step));
		step = new Step(Collections.emptyList(), "", "some other step", 2, Collections.emptyList(), null);
		scenarioModel.addStep(new StepModel(step));

		JsonArray steps = scenarioModel.toJsonObject().get("steps").getAsJsonArray();
		assertThat(steps, emptyIterable());
	}

}
