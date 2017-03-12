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
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;
import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;

public class FeatureModelDeserializerShould {

	@Test
	public void constructRequiredFieldsFromJson() {
		Feature feature = new Feature(Collections.emptyList(), Collections.emptyList(), "Feature", "Cucumber feature",
				"feature description", 14, "a-fancy-id");
		FeatureModel featureModel = new FeatureModel();
		featureModel.setFeature(feature);
		String json = featureModel.toJsonObject().toString();

		FeatureModel model = FeatureModel.fromJson(json);
		assertThat(model.getKeyword(), equalTo("Feature"));
		assertThat(model.getName(), equalTo("Cucumber feature"));
		assertThat(model.getLine(), equalTo(14));
		assertThat(model.getDescription(), equalTo("feature description"));
		assertThat(model.getFeatureId(), equalTo("a-fancy-id"));

		assertThat(model.getScenarios(), emptyIterable());
	}

	@Test
	public void constructOptionalCommentsFromJson() {
		List<Comment> comments = Arrays.asList(new Comment("comment", 1), new Comment("another comment", 2));
		FeatureModel featureModel = FeatureModelShould.modelWithComments(comments);
		String json = featureModel.toJsonObject().toString();

		FeatureModel model = FeatureModel.fromJson(json);
		assertThat(model.getComments(), hasSize(2));
		assertThat(model.getComments().get(0).getValue(), equalTo("comment"));
		assertThat(model.getComments().get(1).getLine(), equalTo(2));
	}

	@Test
	public void constructOptionalTagsFromJson() {
		List<Tag> tags = Arrays.asList(new Tag("tag", 1), new Tag("another tag", 2));
		FeatureModel featureModel = FeatureModelShould.modelWithTags(tags);
		String json = featureModel.toJsonObject().toString();

		FeatureModel model = FeatureModel.fromJson(json);
		assertThat(model.getTags(), hasSize(2));
		assertThat(model.getTags().get(0).getLine(), equalTo(1));
		assertThat(model.getTags().get(1).getName(), equalTo("another tag"));
	}

	@Test
	public void notConstructScenariosFromJson() {
		FeatureModel featureModel = FeatureModelShould.modelWithName("Cucumber feature");
		Scenario scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario", "first scenario",
				"", -1, "");
		featureModel.addScenario(new ScenarioModel(scenario));
		scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario", "second scenario", "", -1,
				"");
		featureModel.addScenario(new ScenarioModel(scenario));
		String json = featureModel.toJsonObject().toString();

		FeatureModel model = FeatureModel.fromJson(json);
		assertThat(model.getScenarios(), emptyIterable());
	}

}
