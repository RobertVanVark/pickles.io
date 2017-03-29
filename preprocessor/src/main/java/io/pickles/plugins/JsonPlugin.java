package io.pickles.plugins;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.pickles.model.FeatureModel;

public class JsonPlugin extends CorePlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonPlugin.class);

	public JsonPlugin(Appendable out) {
		super(out);
	}

	@Override
	public void done() {
		log("done");
		JSONArray featuresJSON = new JSONArray();
		for (FeatureModel feature : getFeatures()) {
			featuresJSON.put(feature.toJsonObject().toString());
			// featuresJSON.addAll((feature.toJSON());
		}

		out.println(featuresJSON.toString(4));
	}

	@Override
	protected void log(String msg) {
		LOGGER.trace(msg);
	}
}
