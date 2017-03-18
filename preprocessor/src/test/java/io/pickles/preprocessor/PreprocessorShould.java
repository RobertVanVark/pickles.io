package io.pickles.preprocessor;

import java.util.List;

import io.pickles.preprocessor.stubs.StubDelayedVerificationStore;
import io.pickles.preprocessor.stubs.SampleFeatureTemplates;

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
		preprocessor.setDelayedVerificationStore(new StubDelayedVerificationStore(2));
		return preprocessor.process("features/preprocessor.feature", featureLines);
	}
}
