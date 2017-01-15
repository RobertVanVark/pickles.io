package nl.devon.pickles.preprocessor;

import java.util.List;

import org.junit.Test;

import nl.devon.pickles.preprocessor.model.FeatureModel;
import nl.devon.pickles.preprocessor.stubs.SampleFeatureTemplates;

public class FeatureWriterShould {

	/*
	 * FeatureTemplate.toGherkinContent() ? FeatureTemplate.toReporterJson ?
	 *
	 * ScenarioTemplate.toGherkinContent() ? ScenariotTemplate.toReporterJson() ?
	 */

	@Test
	public void generateContent() {
		List<String> featureFile = generateFor(SampleFeatureTemplates.twoThenAfterScenario());

		for (String line : featureFile) {
			System.out.println(line);
		}
	}

	private List<String> generateFor(List<String> scenario) {
		FeatureModel featureTemplate = new TemplateParser().parse(scenario);
		FeatureWriter writer = new FeatureWriter(featureTemplate);
		return writer.generate();
	}
}
