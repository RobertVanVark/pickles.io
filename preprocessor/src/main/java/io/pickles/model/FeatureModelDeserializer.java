package io.pickles.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Tag;

class FeatureModelDeserializer implements JsonDeserializer<FeatureModel> {
	@Override
	public FeatureModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject featureJson = json.getAsJsonObject();

		String keyword = featureJson.get("keyword").getAsString();
		String name = featureJson.get("name").getAsString();
		String description = featureJson.get("description").getAsString();
		Integer line = featureJson.get("line").getAsInt();
		String id = featureJson.get("id").getAsString();

		List<Comment> comments = constructComments(featureJson);
		List<Tag> tags = constructTags(featureJson);

		FeatureModel model = new FeatureModel();
		Feature feature = new Feature(comments, tags, keyword, name, description, line, id);
		model.setFeature(feature);

		return model;
	}

	private List<Comment> constructComments(JsonObject featureJson) {
		List<Comment> comments = new ArrayList<>();
		if (featureJson.has("comments")) {
			for (JsonElement element : featureJson.get("comments").getAsJsonArray()) {
				Comment comment = new Comment(element.getAsJsonObject().get("value").getAsString(),
						element.getAsJsonObject().get("line").getAsInt());
				comments.add(comment);
			}
		}
		return comments;
	}

	private List<Tag> constructTags(JsonObject featureJson) {
		List<Tag> tags = new ArrayList<>();
		if (featureJson.has("tags")) {
			for (JsonElement element : featureJson.get("tags").getAsJsonArray()) {
				Tag tag = new Tag(element.getAsJsonObject().get("name").getAsString(),
						element.getAsJsonObject().get("line").getAsInt());
				tags.add(tag);
			}
		}
		return tags;
	}
}
