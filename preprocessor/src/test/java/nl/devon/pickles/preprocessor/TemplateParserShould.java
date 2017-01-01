package nl.devon.pickles.preprocessor;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import nl.devon.pickles.preprocessor.model.FeatureTemplate;

public class TemplateParserShould {

	/*
	 *
	 */

	@Test
	public void parseTemplateFile() {
		FeatureTemplate featureTemplate = parse("target/test-classes/features/SimpleBankingScenario.feature");

		assertThat(featureTemplate.getFeature(), notNullValue());
		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(2));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(8));
		assertThat(featureTemplate.getScenarios().get(1).getSteps(), Matchers.hasSize(7));
	}

	@Test
	public void parseTemplate() {
		FeatureTemplate featureTemplate = parse(oneScenarioFeature());

		assertThat(featureTemplate.getFeature(), notNullValue());
		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(1));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(3));
	}

	private FeatureTemplate parse(String path) {
		TemplateParser parser = new TemplateParser();
		return parser.parse(path);
	}

	private FeatureTemplate parse(List<String> oneScenarioFeature) {
		TemplateParser parser = new TemplateParser();
		return parser.parse(oneScenarioFeature);
	}

	private List<String> oneScenarioFeature() {
		List<String> template = new ArrayList<>();

		template.add("@FeatureTag");
		template.add("Feature: feature name");
		template.add("");
		template.add("Description");
		template.add("");
		template.add("@ScenarioTag1");
		template.add("Scenario: scenario name");
		template.add("Given a precondition");
		template.add("When an action");
		template.add("Then an outcome");

		return template;
	}
}
