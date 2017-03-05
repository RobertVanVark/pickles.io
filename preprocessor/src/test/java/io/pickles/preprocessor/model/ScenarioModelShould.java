package io.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;

public class ScenarioModelShould {

	@Test
	public void wrapCucumberScenario() {
		List<Comment> comments = Collections.emptyList();
		String keyword = "Scenario";
		String name = "Cucumber scenario";
		String description = "description";
		Integer line = 1;
		String id = "id";
		List<Tag> tags = Collections.emptyList();
		Scenario scenario = new Scenario(comments, tags, keyword, name, description, line, id);
		ScenarioModel scenarioModel = modelWith(scenario);

		assertThat(scenarioModel.getScenario(), is(scenario));
	}

	@Test
	public void haveSameNameAsCucumberScenario() {
		ScenarioModel scenarioModel = modelWithName("scenario name");

		assertThat(scenarioModel.getName(), is("scenario name"));
	}

	@Test
	public void knowItsFeatureModel() {
		ScenarioModel scenarioModel = modelWithName("scenario name");
		FeatureModel featureModel = new FeatureModel();
		scenarioModel.setFeature(featureModel);

		assertThat(scenarioModel.getFeature(), is(featureModel));
	}

	@Test
	public void haveTagsIfCucumberScenarioHasTags() {
		ScenarioModel scenarioModel = modelWithTags(Collections.emptyList());
		assertThat(scenarioModel.hasTags(), is(false));

		scenarioModel = modelWithTags(Arrays.asList(new Tag("@tag1", 1), new Tag("@tag2", 3)));
		assertThat(scenarioModel.hasTags(), is(true));
	}

	@Test
	public void haveSameTagsAsCucumberScenario() {
		ScenarioModel scenarioModel = modelWithTags(Collections.emptyList());
		assertThat(scenarioModel.getTagNames(), Matchers.hasSize(0));

		scenarioModel = modelWithTags(Arrays.asList(new Tag("@tag1", 1)));
		scenarioModel.addTag("@tag2");
		assertThat(scenarioModel.getTagNames(), contains("@tag1", "@tag2"));
	}

	@Test
	public void haveSteps() {
		ScenarioModel scenarioModel = modelWithName("scenario name");
		assertThat(scenarioModel.getSteps(), Matchers.hasSize(0));

		StepModel step1 = new StepModel(
				new Step(Collections.emptyList(), "", "step 1", -1, Collections.emptyList(), null));
		StepModel step2 = new StepModel(
				new Step(Collections.emptyList(), "", "step 2", -1, Collections.emptyList(), null));
		scenarioModel.addStep(step1);
		scenarioModel.addStep(step2);

		assertThat(scenarioModel.getSteps(), contains(step1, step2));
		assertThat(scenarioModel.getStep(0), is(step1));
		assertThat(scenarioModel.getLastStep(), is(step2));
	}

	@Test
	public void convertToGherkin() {
		List<Comment> comments = Collections.emptyList();
		String keyword = "Scenario";
		String name = "Cucumber scenario";
		String description = "";
		Integer line = -1;
		String id = "";
		List<Tag> tags = Arrays.asList(new Tag("@tag3", 4), new Tag("@tag222222", 5));
		Scenario scenario = new Scenario(comments, tags, keyword, name, description, line, id);
		ScenarioModel scenarioModel = modelWith(scenario);

		String[] lines = scenarioModel.toGherkin().split(System.getProperty("line.separator"));
		assertThat(lines[0], is("@tag3 @tag222222"));
		assertThat(lines[1], is("Scenario: Cucumber scenario"));
	}

	@Test
	public void convertToGherkinList() {
		ScenarioModel scenarioModel = modelWithName("Cucumber scenario");
		StepModel step;
		step = new StepModel(new Step(Collections.emptyList(), "Given ", "step 1", -1, Collections.emptyList(), null));
		scenarioModel.addStep(step);
		step = new StepModel(new Step(Collections.emptyList(), "Then ", "step 2", -1, Collections.emptyList(), null));
		scenarioModel.addStep(step);

		List<String> lines = scenarioModel.toGherkinList();
		assertThat(lines.get(0), is("Scenario: Cucumber scenario"));
		assertThat(lines.get(1), is("    Given step 1"));
		assertThat(lines.get(2), is("    Then step 2"));
	}

	@Test
	public void convertRequiredFieldsIntoJsonObject() {
		ScenarioModel scenarioModel = modelWithName("scenario name");
		JsonObject jsonObject = scenarioModel.toJsonObject().getAsJsonObject();
		assertThat(jsonObject.get("line").getAsInt(), equalTo(scenarioModel.getLine()));
		assertThat(jsonObject.get("name").getAsString(), equalTo(scenarioModel.getName()));
		assertThat(jsonObject.get("keyword").getAsString(), equalTo(scenarioModel.getKeyword()));
		assertThat(jsonObject.get("type").getAsString(), equalTo(scenarioModel.getKeyword().toLowerCase()));
		assertThat(jsonObject.get("description").getAsString(), equalTo(scenarioModel.getDescription()));
		assertThat(jsonObject.get("id").getAsString(), equalTo(scenarioModel.getId()));

		assertThat(jsonObject.get("steps").getAsJsonArray(), emptyIterable());
	}

	@Test
	public void convertOptionalTagsIntoJsonObject() {
		List<Tag> tags = Arrays.asList(new Tag("tag", 1), new Tag("another tag", 2));
		ScenarioModel scenarioModel = modelWithTags(tags);

		JsonArray tagsGson = scenarioModel.toJsonObject().getAsJsonObject().get("tags").getAsJsonArray();
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
		ScenarioModel scenarioModel = modelWithComments(comments);

		JsonArray commentsGson = scenarioModel.toJsonObject().getAsJsonObject().get("comments").getAsJsonArray();
		assertThat(commentsGson.size(), equalTo(2));

		JsonObject comment = commentsGson.get(0).getAsJsonObject();
		assertThat(comment.get("value").getAsString(), equalTo("comment"));
		assertThat(comment.get("line").getAsInt(), equalTo(1));

		comment = commentsGson.get(1).getAsJsonObject();
		assertThat(comment.get("value").getAsString(), equalTo("another comment"));
		assertThat(comment.get("line").getAsInt(), equalTo(2));
	}

	static ScenarioModel modelWithComments(List<Comment> comments) {
		return modelWith("", comments, Collections.emptyList());
	}

	static ScenarioModel modelWithTags(List<Tag> tags) {
		return modelWith("", Collections.emptyList(), tags);
	}

	static ScenarioModel modelWithName(String name) {
		return modelWith(name, Collections.emptyList(), Collections.emptyList());
	}

	static ScenarioModel modelWith(String name, List<Comment> comments, List<Tag> tags) {
		Scenario scenario = new Scenario(comments, tags, "Scenario", name, "", -1, "");
		return modelWith(scenario);
	}

	static ScenarioModel modelWith(Scenario scenario) {
		return new ScenarioModel(scenario);
	}
}
