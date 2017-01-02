package nl.devon.pickles.preprocessor;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.preprocessor.stubs.SampleFeatureTemplates;

public class TemplateParserShould {

	/*
	 *
	 */

	@Test
	public void parseTemplateFile() {
		FeatureTemplate featureTemplate = parse("target/test-classes/features/SimpleBankingScenario.feature");

		assertThat(featureTemplate.getFeature(), notNullValue());
		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(2));
		assertThat(featureTemplate.getScenario(0).getSteps(), Matchers.hasSize(8));
		assertThat(featureTemplate.getScenario(1).getSteps(), Matchers.hasSize(7));
	}

	@Test
	public void parseTemplate() {
		FeatureTemplate featureTemplate = parse(SampleFeatureTemplates.oneScenarioFeature());

		assertThat(featureTemplate.getFeature(), notNullValue());
		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(1));
		assertThat(featureTemplate.getScenario(0).getSteps(), Matchers.hasSize(3));
	}

	private FeatureTemplate parse(String path) {
		TemplateParser parser = new TemplateParser();
		return parser.parse(path);
	}

	private FeatureTemplate parse(List<String> oneScenarioFeature) {
		TemplateParser parser = new TemplateParser();
		return parser.parse(oneScenarioFeature);
	}
}
