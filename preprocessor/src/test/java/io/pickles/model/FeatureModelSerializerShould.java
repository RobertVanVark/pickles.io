package io.pickles.model;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;

public class FeatureModelSerializerShould {

	@Test
	public void convertRequiredFieldsIntoJsonObject() {
		FeatureModel featureModel = FeatureModelShould.modelWithName("Cucumber feature");
		JsonElement gson = featureModel.toJsonObject();
		assertThat(gson.getAsJsonObject().get("line").getAsInt(), equalTo(featureModel.getLine()));
		assertThat(gson.getAsJsonObject().get("name").getAsString(), equalTo(featureModel.getName()));
		assertThat(gson.getAsJsonObject().get("elements").getAsJsonArray(), emptyIterable());
		assertThat(gson.getAsJsonObject().get("description").getAsString(), isEmptyString());
		assertThat(gson.getAsJsonObject().get("keyword").getAsString(), equalTo("Feature"));
		assertThat(gson.getAsJsonObject().get("id").getAsString(), isEmptyString());
	}

	@Test
	public void convertOptionalTagsIntoJsonObject() {
		List<Tag> tags = Arrays.asList(new Tag("tag", 1), new Tag("another tag", 2));
		FeatureModel featureModel = FeatureModelShould.modelWithTags(tags);

		JsonArray tagsGson = featureModel.toJsonObject().getAsJsonObject().get("tags").getAsJsonArray();
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
		FeatureModel featureModel = FeatureModelShould.modelWithComments(comments);

		JsonArray commentsGson = featureModel.toJsonObject().getAsJsonObject().get("comments").getAsJsonArray();
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
		FeatureModel featureModel = FeatureModelShould.modelWithName("Cucumber feature");
		Scenario scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario", "first scenario",
				"", -1, "");
		featureModel.addScenario(new ScenarioModel(scenario));
		scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario", "second scenario", "", -1,
				"");
		featureModel.addScenario(new ScenarioModel(scenario));

		JsonArray elements = featureModel.toDeepJsonObject().get("elements").getAsJsonArray();
		assertThat(elements.size(), equalTo(2));
		assertThat(elements.get(0).getAsJsonObject().get("name").getAsString(), equalTo("first scenario"));
		assertThat(elements.get(1).getAsJsonObject().get("name").getAsString(), equalTo("second scenario"));
	}

	@Test
	public void notConvertScenariosIntoJsonObject() {
		FeatureModel featureModel = FeatureModelShould.modelWithName("Cucumber feature");
		Scenario scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario", "first scenario",
				"", -1, "");
		featureModel.addScenario(new ScenarioModel(scenario));
		scenario = new Scenario(Collections.emptyList(), Collections.emptyList(), "Scenario", "second scenario", "", -1,
				"");
		featureModel.addScenario(new ScenarioModel(scenario));

		JsonArray elements = featureModel.toJsonObject().get("elements").getAsJsonArray();
		assertThat(elements, emptyIterable());
	}
}
