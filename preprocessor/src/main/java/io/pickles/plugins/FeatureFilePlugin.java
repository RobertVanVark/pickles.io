package io.pickles.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.pickles.model.FeatureModel;

public class FeatureFilePlugin extends CorePlugin {

	private final Logger LOGGER = LoggerFactory.getLogger(FeatureFilePlugin.class);

	public FeatureFilePlugin(Appendable out) {
		super(out);
	}

	@Override
	public void done() {
		log("done");
		System.out.println("#######################################################################");
		for (FeatureModel feature : getFeatures()) {
			System.out.println(feature.toGherkin());
			System.out.println("#######################################################################");
		}
	}

	@Override
	protected void log(String msg) {
		LOGGER.debug(msg);
	}
}
