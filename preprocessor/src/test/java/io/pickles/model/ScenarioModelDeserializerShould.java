package io.pickles.model;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import io.pickles.model.ScenarioModelShould;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;

public class ScenarioModelDeserializerShould {

	@Test
	public void constructRequiredFieldsFromJson() {
		Scenario scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario", "a scenario",
				"fancy description", 12, "an-id");
		ScenarioModel scenarioModel = new ScenarioModel(scenario);
		String json = scenarioModel.toJsonObject().toString();

		ScenarioModel model = ScenarioModel.fromJson(json);
		assertThat(model.getKeyword(), equalTo("Scenario"));
		assertThat(model.getName(), equalTo("a scenario"));
		assertThat(model.getDescription(), equalTo("fancy description"));
		assertThat(model.getLine(), equalTo(12));
		assertThat(model.getScenarioId(), equalTo("an-id"));

		assertThat(model.getSteps(), emptyIterable());

		assertThat(model.getComments(), emptyIterable());
		assertThat(model.getTags(), emptyIterable());
	}

	@Test
	public void constructOptionalCommentsFromJson() {
		List<Comment> comments = Arrays.asList(new Comment("comment", 1), new Comment("another comment", 2));
		ScenarioModel scenarioModel = ScenarioModelShould.modelWithComments(comments);
		String json = scenarioModel.toJsonObject().toString();

		ScenarioModel model = ScenarioModel.fromJson(json);
		assertThat(model.getComments(), hasSize(2));
		assertThat(model.getComments().get(0).getLine(), equalTo(1));
		assertThat(model.getComments().get(1).getValue(), equalTo("another comment"));
	}

	@Test
	public void constructOptionalTagsFromJson() {
		List<Tag> tags = Arrays.asList(new Tag("tag", 1), new Tag("another tag", 4));
		ScenarioModel scenarioModel = ScenarioModelShould.modelWithTags(tags);
		String json = scenarioModel.toJsonObject().toString();

		ScenarioModel model = ScenarioModel.fromJson(json);
		assertThat(model.getTags(), hasSize(2));
		assertThat(model.getTags().get(0).getName(), equalTo("tag"));
		assertThat(model.getTags().get(1).getLine(), equalTo(4));
	}

	@Test
	public void notConstructStepsFromJson() {
		ScenarioModel scenarioModel = ScenarioModelShould.modelWithName("A scenario");
		Step step = new Step(Collections.emptyList(), "", "a step", 1, Collections.emptyList(), null);
		scenarioModel.addStep(new StepModel(step));
		step = new Step(Collections.emptyList(), "", "some other step", 2, Collections.emptyList(), null);
		scenarioModel.addStep(new StepModel(step));
		String json = scenarioModel.toJsonObject().toString();

		ScenarioModel model = ScenarioModel.fromJson(json);
		assertThat(model.getSteps(), emptyIterable());
	}
}
