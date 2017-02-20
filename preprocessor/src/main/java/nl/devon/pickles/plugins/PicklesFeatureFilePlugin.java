package nl.devon.pickles.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devon.pickles.preprocessor.model.FeatureModel;

public class PicklesFeatureFilePlugin extends PicklesCorePlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger("nl.devon.pickles.plugin.PicklesCorePlugin");

	public PicklesFeatureFilePlugin(Appendable out) {
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

	private void log(String msg) {
		LOGGER.info(msg);
	}

}
