package nl.devon.pickles.preprocessor;

import java.util.List;

import nl.devon.pickles.preprocessor.stubs.DummyDelayedVerificationStore;
import nl.devon.pickles.preprocessor.stubs.SampleFeatureTemplates;

public class PreprocessorShould {

	/*
	 * transform all featuretemplates on the classpath or features setting in CucumberOptions into feature files
	 */

	public void parseTransformWrite() {
		List<String> featureContent = preprocess(SampleFeatureTemplates.twoThenAfterScenario());
		for (String line : featureContent) {
			System.out.println(line);
		}
	}

	private List<String> preprocess(List<String> featureLines) {
		Preprocessor preprocessor = new Preprocessor();
		preprocessor.setDelayedVerificationStore(new DummyDelayedVerificationStore(2));
		return preprocessor.process(featureLines);
	}
}
