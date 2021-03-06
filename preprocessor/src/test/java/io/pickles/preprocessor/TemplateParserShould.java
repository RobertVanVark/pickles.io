package io.pickles.preprocessor;

import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import io.pickles.model.FeatureModel;
import io.pickles.preprocessor.TemplateParser;
import io.pickles.preprocessor.stubs.SampleFeatureTemplates;

import static org.junit.Assert.assertThat;

public class TemplateParserShould {

	@Test
	public void parseTemplateFile() {
		FeatureModel featureTemplate = parse("target/test-classes/features/SimpleBankingScenario.feature");

		assertThat(featureTemplate.getFeature(), notNullValue());
		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(2));
		assertThat(featureTemplate.getScenario(0).getSteps(), Matchers.hasSize(8));
		assertThat(featureTemplate.getScenario(1).getSteps(), Matchers.hasSize(7));
	}

	@Test
	public void parseTemplate() {
		FeatureModel featureTemplate = parse(SampleFeatureTemplates.oneScenarioFeature());

		assertThat(featureTemplate.getFeature(), notNullValue());
		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(1));
		assertThat(featureTemplate.getScenario(0).getSteps(), Matchers.hasSize(3));
	}

	private FeatureModel parse(String path) {
		TemplateParser parser = new TemplateParser();
		return parser.parse(path);
	}

	private FeatureModel parse(List<String> oneScenarioFeature) {
		TemplateParser parser = new TemplateParser();
		return parser.parse("", oneScenarioFeature);
	}
}
