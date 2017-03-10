package io.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;

public class FeatureModelShould {

	@Test
	public void wrapCucumberFeature() {
		List<Comment> comments = new ArrayList<>();
		String keyword = "Feature";
		String name = "Cucumber feature";
		String description = "description";
		Integer line = 1;
		String id = "id";
		List<Tag> tags = new ArrayList<>();
		Feature feature = new Feature(comments, tags, keyword, name, description, line, id);
		FeatureModel featureModel = modelWith(feature);

		assertThat(featureModel.getFeature(), equalTo(feature));
	}

	@Test
	public void haveSameNameAsCucumberFeature() {
		FeatureModel featureModel = modelWithName("feature name");

		assertThat(featureModel.getName(), equalTo("feature name"));

	}

	@Test
	public void haveTagsIfCucumberFeatureHasTags() {
		FeatureModel featureModel = modelWithTags(Arrays.asList(new Tag("tag", 1)));
		assertThat(featureModel.hasTags(), equalTo(true));

		featureModel = modelWithTags(Collections.emptyList());
		assertThat(featureModel.hasTags(), equalTo(false));
	}

	@Test
	public void haveSameTagsAsCucumberFeature() {
		List<Tag> tags = Arrays.asList(new Tag("tag", 1), new Tag("another tag", 2));
		FeatureModel featureModel = modelWithTags(tags);

		assertThat(featureModel.getTagNames(), contains("tag", "another tag"));
	}

	@Test
	public void haveCommentsIfCucumberFeatureHasComments() {
		FeatureModel featureModel = modelWithComments(Arrays.asList(new Comment("comment", 1)));
		assertThat(featureModel.hasComments(), equalTo(true));

		featureModel = modelWithComments(Collections.emptyList());
		assertThat(featureModel.hasComments(), equalTo(false));
	}

	@Test
	public void haveSameCommentsAsCucumberFeature() {
		List<Comment> comments = Arrays.asList(new Comment("comment", 1), new Comment("another comment", 2));
		FeatureModel featureModel = modelWithComments(comments);

		assertThat(featureModel.getComments(), contains(comments.get(0), comments.get(1)));
	}

	@Test
	public void haveScenarios() {
		ScenarioModel scenarioModel1 = new ScenarioModel(null);
		ScenarioModel scenarioModel2 = new ScenarioModel(null);

		FeatureModel model = modelWithName("feature");
		model.addScenario(scenarioModel1);
		model.addScenario(scenarioModel2);

		assertThat(model.getScenarios(), hasSize(2));
		assertThat(model.getScenario(0), equalTo(scenarioModel1));
	}

	@Test
	public void haveLastAddedScenarioAsCurrentScenario() {
		FeatureModel featureModel = modelWithName("feature");
		assertThat(featureModel.getCurrentScenario(), nullValue());

		ScenarioModel scenarioModel = new ScenarioModel(null);
		featureModel.addScenario(scenarioModel);
		assertThat(featureModel.getCurrentScenario(), equalTo(scenarioModel));
	}

	@Test
	public void convertToGherkin() {
		List<Tag> tags = Arrays.asList(new Tag("@tag1", 4), new Tag("@tag2", 5));
		FeatureModel featureModel = modelWith("Cucumber feature", tags, Collections.emptyList());
		System.out.println(featureModel.toJsonObject().toString());

		String[] lines = featureModel.toGherkin().split(System.getProperty("line.separator"));
		assertThat(lines[0], equalTo("@tag1 @tag2"));
		assertThat(lines[1], equalTo("Feature: Cucumber feature"));
	}

	@Test
	public void convertToGherkinList() {
		FeatureModel featureModel = modelWithName("Cucumber feature");
		Scenario scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario", "first scenario",
				"", -1, "");
		featureModel.addScenario(new ScenarioModel(scenario));

		List<String> lines = featureModel.toGherkinList();
		assertThat(lines, hasSize(3));
		assertThat(lines.get(0), equalTo("Feature: Cucumber feature"));
		assertThat(lines.get(1), isEmptyString());
		assertThat(lines.get(2), equalTo("Scenario: first scenario"));
	}

	@Test
	public void findFirstUnmatchedStep() {
		StepModel firstUnmatchedStep = twoUnmatchedScenarios().getFirstStepWithoutMatch();
		assertThat(firstUnmatchedStep.getName(), equalTo("first scenario - first"));

		firstUnmatchedStep = lastStepUnmatched().getFirstStepWithoutMatch();
		assertThat(firstUnmatchedStep.getName(), equalTo("second scenario - last"));
	}

	@Test
	public void findFirstStepWithoutResult() {
		StepModel firstUnmatchedStep = twoUnmatchedScenarios().getFirstStepWithoutResult();
		assertThat(firstUnmatchedStep.getName(), equalTo("first scenario - first"));

		firstUnmatchedStep = lastStepUnmatched().getFirstStepWithoutResult();
		assertThat(firstUnmatchedStep.getName(), equalTo("second scenario - last"));
	}

	private FeatureModel twoUnmatchedScenarios() {
		FeatureModel feature = modelWithName("feature");

		ScenarioModel scenario = new ScenarioModel(new Scenario(Collections.emptyList(), Collections.emptyList(),
				"Scenario", "first scenario", "", 3, ""));
		feature.addScenario(scenario);
		Step step = new Step(Collections.emptyList(), "Given ", "first scenario - first", 4, Collections.emptyList(),
				null);
		scenario.addStep(new StepModel(step));
		step = new Step(Collections.emptyList(), "Then ", "first scenario - second", 5, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step));

		scenario = new ScenarioModel(new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario",
				"second scenario", "", 7, ""));
		feature.addScenario(scenario);
		step = new Step(Collections.emptyList(), "Given ", "second scenario - first", 8, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step));
		step = new Step(Collections.emptyList(), "Then ", "second scenario - second", 9, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step));

		return feature;
	}

	private FeatureModel lastStepUnmatched() {
		FeatureModel feature = modelWithName("feature");

		Match match = new Match(Collections.emptyList(), "location");
		Result result = new Result("Pending", 10L, null, null);

		ScenarioModel scenario = new ScenarioModel(new Scenario(Collections.emptyList(), Collections.emptyList(),
				"Scenario", "first scenario", "", 3, ""));
		feature.addScenario(scenario);
		Step step = new Step(Collections.emptyList(), "Given ", "first scenario - first", 4, Collections.emptyList(),
				null);
		scenario.addStep(new StepModel(step, match, result));
		step = new Step(Collections.emptyList(), "Then ", "first scenario - last", 5, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step, match, result));

		scenario = new ScenarioModel(new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario",
				"second scenario", "", 7, ""));
		feature.addScenario(scenario);
		step = new Step(Collections.emptyList(), "Given ", "second scenario - first", 8, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step, match, result));
		step = new Step(Collections.emptyList(), "Then ", "second scenario - last", 9, Collections.emptyList(), null);
		scenario.addStep(new StepModel(step));

		return feature;
	}

	static FeatureModel modelWithTags(List<Tag> tags) {
		return modelWith("", tags, Collections.emptyList());
	}

	static FeatureModel modelWithComments(List<Comment> comments) {
		return modelWith("", Collections.emptyList(), comments);
	}

	static FeatureModel modelWithName(String name) {
		return modelWith(name, Collections.emptyList(), Collections.emptyList());
	}

	static FeatureModel modelWith(String name, List<Tag> tags, List<Comment> comments) {
		Feature feature = new Feature(comments, tags, "Feature", name, "", -1, "");
		return modelWith(feature);
	}

	static FeatureModel modelWith(Feature feature) {
		FeatureModel featureModel = new FeatureModel();
		featureModel.setFeature(feature);
		return featureModel;
	}
}
