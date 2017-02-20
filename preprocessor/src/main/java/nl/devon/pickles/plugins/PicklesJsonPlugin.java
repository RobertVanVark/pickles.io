package nl.devon.pickles.plugins;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devon.pickles.preprocessor.model.FeatureModel;

public class PicklesJsonPlugin extends PicklesCorePlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger("nl.devon.pickles.plugin.PicklesJsonPlugin");

	public PicklesJsonPlugin(Appendable out) {
		super(out);
	}

	@Override
	public void done() {
		log("done");
		JSONArray featuresJSON = new JSONArray();
		for (FeatureModel feature : getFeatures()) {
			featuresJSON.put(feature.toJSON());
			// featuresJSON.addAll((feature.toJSON());
		}

		System.out.println(featuresJSON.toString(4));
	}

	private void log(String msg) {
		LOGGER.info(msg);
	}
}
