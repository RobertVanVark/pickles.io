package nl.devon.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

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
		ScenarioModel model = modelWith(scenario);

		assertThat(model.getScenario(), is(scenario));
	}

	@Test
	public void haveSameNameAsCucumberScenario() {
		ScenarioModel model = modelWithName("scenario name");

		assertThat(model.getName(), is("scenario name"));
	}

	@Test
	public void knowItsFeatureModel() {
		ScenarioModel model = modelWithName("scenario name");
		FeatureModel featureModel = new FeatureModel();
		model.setFeatureModel(featureModel);

		assertThat(model.getFeatureModel(), is(featureModel));
	}

	@Test
	public void haveTagsIfCucumberScenarioHasTags() {
		ScenarioModel model = modelWithTags(Collections.emptyList());
		assertThat(model.hasTags(), is(false));

		model = modelWithTags(Arrays.asList(new Tag("@tag1", 1), new Tag("@tag2", 3)));
		assertThat(model.hasTags(), is(true));
	}

	@Test
	public void haveSameTagsAsCucumberScenario() {
		ScenarioModel model = modelWithTags(Collections.emptyList());
		assertThat(model.getTagNames(), Matchers.hasSize(0));

		model = modelWithTags(Arrays.asList(new Tag("@tag1", 1)));
		model.addTag("@tag2");
		assertThat(model.getTagNames(), contains("@tag1", "@tag2"));
	}

	@Test
	public void haveSteps() {
		ScenarioModel model = modelWithName("scenario name");
		assertThat(model.getSteps(), Matchers.hasSize(0));

		StepModel step1 = new StepModel(
				new Step(Collections.emptyList(), "", "step 1", -1, Collections.emptyList(), null));
		StepModel step2 = new StepModel(
				new Step(Collections.emptyList(), "", "step 2", -1, Collections.emptyList(), null));
		model.addStep(step1);
		model.addStep(step2);

		assertThat(model.getSteps(), contains(step1, step2));
		assertThat(model.getStep(0), is(step1));
		assertThat(model.getLastStep(), is(step2));
	}

	@Test
	public void convertToCondensedFeatureFileFormat() {
		List<Comment> comments = Arrays.asList(new Comment("Comment line", 1), new Comment("Another comment", 2));
		String keyword = "Scenario";
		String name = "Cucumber scenario";
		String description = "description";
		Integer line = 1;
		String id = "id";
		List<Tag> tags = Arrays.asList(new Tag("@tag3", 4), new Tag("@tag222222", 5));
		Scenario scenario = new Scenario(comments, tags, keyword, name, description, line, id);
		ScenarioModel model = modelWith(scenario);

		String[] lines = model.toGherkin().split(System.getProperty("line.separator"));
		assertThat(lines[0], is("@tag3 @tag222222"));
		assertThat(lines[1], is("Scenario: Cucumber scenario"));
	}

	private ScenarioModel modelWithName(String name) {
		Scenario scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "", name, "", -1, "");
		return modelWith(scenario);
	}

	private ScenarioModel modelWithTags(List<Tag> tags) {
		Scenario scenario = new Scenario(Collections.emptyList(), tags, "", "", "", -1, "");
		return modelWith(scenario);
	}

	private ScenarioModel modelWith(Scenario scenario) {
		ScenarioModel model = new ScenarioModel();
		model.setSCenario(scenario);
		return model;
	}
}
