package io.pickles.preprocessor;

import java.util.List;

import org.junit.Test;

import io.pickles.preprocessor.Preprocessor;
import io.pickles.preprocessor.stubs.DummyDelayedVerificationStore;
import io.pickles.preprocessor.stubs.SampleFeatureTemplates;

public class PreprocessorShould {

	/*
	 * transform all featuretemplates on the classpath or features setting in CucumberOptions into feature files
	 */

	@Test
	public void parseTransformWrite() {
		List<String> featureContent = preprocess(SampleFeatureTemplates.twoThenAfterScenario());
		for (String line : featureContent) {
			System.out.println(line);
		}
	}

	private List<String> preprocess(List<String> featureLines) {
		Preprocessor preprocessor = new Preprocessor();
		preprocessor.setDelayedVerificationStore(new DummyDelayedVerificationStore(2));
		return preprocessor.process("features/preprocessor.feature", featureLines);
	}
}
