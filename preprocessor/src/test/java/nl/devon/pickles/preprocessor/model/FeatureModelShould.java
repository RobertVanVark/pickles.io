package nl.devon.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Feature;
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
		FeatureModel model = modelWith(feature);

		assertThat(model.getFeature(), is(feature));
	}

	@Test
	public void haveSameNameAsCucumberFeature() {
		FeatureModel model = modelWithName("feature name");

		assertThat(model.getName(), is("feature name"));

	}

	@Test
	public void haveTagsIfCucumberFeatureHasTags() {
		FeatureModel model = modelWithTags(Arrays.asList(new Tag("tag", 1)));
		assertThat(model.hasTags(), is(true));

		model = modelWithTags(Collections.emptyList());
		assertThat(model.hasTags(), is(false));
	}

	@Test
	public void haveSameTagsAsCucumberFeature() {
		List<Tag> tags = Arrays.asList(new Tag("tag", 1), new Tag("another tag", 2));
		FeatureModel model = modelWithTags(tags);

		assertThat(model.getTagNames(), contains("tag", "another tag"));
	}

	@Test
	public void haveScenarios() {
		ScenarioModel scenarioModel1 = new ScenarioModel();
		ScenarioModel scenarioModel2 = new ScenarioModel();

		FeatureModel model = modelWithName("feature");
		model.addScenario(scenarioModel1);
		model.addScenario(scenarioModel2);

		assertThat(model.getScenarios(), Matchers.hasSize(2));
		assertThat(model.getScenario(0), is(scenarioModel1));
	}

	@Test
	public void haveLastAddedScenarioAsCurrentScenario() {
		FeatureModel model = modelWithName("feature");
		assertThat(model.getCurrentScenario(), nullValue());

		ScenarioModel scenarioModel = new ScenarioModel();
		model.addScenario(scenarioModel);
		assertThat(model.getCurrentScenario(), is(scenarioModel));
	}

	@Test
	public void convertToCondensedFeatureFileFormat() {
		List<Comment> comments = Arrays.asList(new Comment("Comment line", 1), new Comment("Another comment", 2));
		String keyword = "Feature";
		String name = "Cucumber feature";
		String description = "description";
		Integer line = 1;
		String id = "id";
		List<Tag> tags = Arrays.asList(new Tag("@tag1", 4), new Tag("@tag2", 5));
		Feature feature = new Feature(comments, tags, keyword, name, description, line, id);
		FeatureModel model = modelWith(feature);

		assertThat(model.toFeatureString(), is("@tag1 @tag2\r\nFeature: Cucumber feature\r\n\r\n"));
	}

	private FeatureModel modelWithTags(List<Tag> tags) {
		Feature feature = new Feature(Collections.emptyList(), tags, "", "", "", -1, "");
		return modelWith(feature);
	}

	private FeatureModel modelWithName(String name) {
		Feature feature = new Feature(Collections.emptyList(), Collections.emptyList(), "", name, "", -1, "");
		return modelWith(feature);
	}

	private FeatureModel modelWith(Feature feature) {
		FeatureModel model = new FeatureModel();
		model.setFeature(feature);
		return model;
	}
}
