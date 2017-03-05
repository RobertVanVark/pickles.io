package io.pickles.preprocessor.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ModelGsonBuilder {

	public static GsonBuilder builder() {
		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(FeatureModel.class, new FeatureModelSerializer());
		builder.registerTypeAdapter(FeatureModel.class, new FeatureModelDeserializer());

		builder.registerTypeAdapter(ScenarioModel.class, new ScenarioModelSerializer());
		builder.registerTypeAdapter(ScenarioModel.class, new ScenarioModelDeserializer());

		builder.registerTypeAdapter(StepModel.class, new StepModelSerializer());
		builder.registerTypeAdapter(StepModel.class, new StepModelDeserializer());
		return builder;
	}

	public static Gson gson() {
		return builder().create();
	}

}
