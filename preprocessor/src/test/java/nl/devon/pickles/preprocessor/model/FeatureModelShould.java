package nl.devon.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.is;
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

		assertThat(featureModel.getFeature(), is(feature));
	}

	@Test
	public void haveSameNameAsCucumberFeature() {
		FeatureModel featureModel = modelWithName("feature name");

		assertThat(featureModel.getName(), is("feature name"));

	}

	@Test
	public void haveTagsIfCucumberFeatureHasTags() {
		FeatureModel featureModel = modelWithTags(Arrays.asList(new Tag("tag", 1)));
		assertThat(featureModel.hasTags(), is(true));

		featureModel = modelWithTags(Collections.emptyList());
		assertThat(featureModel.hasTags(), is(false));
	}

	@Test
	public void haveSameTagsAsCucumberFeature() {
		List<Tag> tags = Arrays.asList(new Tag("tag", 1), new Tag("another tag", 2));
		FeatureModel featureModel = modelWithTags(tags);

		assertThat(featureModel.getTagNames(), contains("tag", "another tag"));
	}

	@Test
	public void haveScenarios() {
		ScenarioModel scenarioModel1 = new ScenarioModel(null);
		ScenarioModel scenarioModel2 = new ScenarioModel(null);

		FeatureModel model = modelWithName("feature");
		model.addScenario(scenarioModel1);
		model.addScenario(scenarioModel2);

		assertThat(model.getScenarios(), hasSize(2));
		assertThat(model.getScenario(0), is(scenarioModel1));
	}

	@Test
	public void haveLastAddedScenarioAsCurrentScenario() {
		FeatureModel featureModel = modelWithName("feature");
		assertThat(featureModel.getCurrentScenario(), nullValue());

		ScenarioModel scenarioModel = new ScenarioModel(null);
		featureModel.addScenario(scenarioModel);
		assertThat(featureModel.getCurrentScenario(), is(scenarioModel));
	}

	@Test
	public void convertToGherkin() {
		List<Tag> tags = Arrays.asList(new Tag("@tag1", 4), new Tag("@tag2", 5));
		FeatureModel featureModel = modelWith("Cucumber feature", tags);

		String[] lines = featureModel.toGherkin().split(System.getProperty("line.separator"));
		assertThat(lines[0], is("@tag1 @tag2"));
		assertThat(lines[1], is("Feature: Cucumber feature"));
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
		StepModel firstUnmatchedStep = twoUnmatchedScenarios().getFirstUnmatchedStep();
		assertThat(firstUnmatchedStep.getName(), equalTo("first scenario - first"));

		firstUnmatchedStep = lastStepUnmatched().getFirstUnmatchedStep();
		assertThat(firstUnmatchedStep.getName(), equalTo("second scenario - last"));
	}

	@Test
	public void findFirstStepWithoutResult() {
		StepModel firstUnmatchedStep = twoUnmatchedScenarios().getFirstUnmatchedStep();
		assertThat(firstUnmatchedStep.getName(), equalTo("first scenario - first"));

		firstUnmatchedStep = lastStepUnmatched().getFirstUnmatchedStep();
		assertThat(firstUnmatchedStep.getName(), equalTo("second scenario - last"));
	}

	private FeatureModel modelWithTags(List<Tag> tags) {
		return modelWith("", tags);
	}

	private FeatureModel modelWithName(String name) {
		return modelWith(name, Collections.emptyList());
	}

	private FeatureModel modelWith(String name, List<Tag> tags) {
		Feature feature = new Feature(Collections.emptyList(), tags, "Feature", name, "", -1, "");
		return modelWith(feature);
	}

	private FeatureModel modelWith(Feature feature) {
		FeatureModel featureModel = new FeatureModel();
		featureModel.setFeature(feature);
		return featureModel;
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
}
