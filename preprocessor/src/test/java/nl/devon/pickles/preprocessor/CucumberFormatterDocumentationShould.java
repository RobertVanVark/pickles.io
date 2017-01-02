package nl.devon.pickles.preprocessor;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.preprocessor.stubs.SampleFeatureTemplates;

public class CucumberFormatterDocumentationShould {

	@Test
	public void documentFeatureFields() {
		FeatureTemplate featureTemplate = new TemplateParser().parse(SampleFeatureTemplates.simmpleFeatureTemplate());

		Feature feature = featureTemplate.getFeature();
		// No Comments for Features !
		assertThat(feature.getComments(), empty());
		assertThat(feature.getDescription(), is("\nDescription\n\nDescription"));
		assertThat(feature.getId(), is("feature name".replaceAll(" ", "-")));
		assertThat(feature.getKeyword(), is("Feature"));
		assertThat(feature.getLine(), is(3));
		assertThat(feature.getLineRange().getFirst(), is(1));
		assertThat(feature.getLineRange().getLast(), is(3));
		assertThat(feature.getName(), is("feature name"));
		List<String> tagNames = feature.getTags().stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(tagNames, hasItem("@FeatureTag1"));
	}

	@Test
	public void documentScenarioFields() {
		FeatureTemplate featureTemplate = new TemplateParser().parse(SampleFeatureTemplates.simmpleFeatureTemplate());

		Scenario scenario = featureTemplate.getScenario(0).getScenario();
		List<String> comments = scenario.getComments().stream().map(s -> s.getValue()).collect(Collectors.toList());
		assertThat(comments, hasItem("# Scenario comment"));
		assertThat(scenario.getDescription(), is(""));

		assertThat(scenario.getId(), is("feature name;scenario name".replaceAll(" ", "-")));
		assertThat(scenario.getKeyword(), is("Scenario"));
		assertThat(scenario.getLine(), is(12));
		assertThat(scenario.getLineRange().getFirst(), is(9));
		assertThat(scenario.getLineRange().getLast(), is(12));
		assertThat(scenario.getName(), is("scenario name"));
		List<String> tagNames = scenario.getTags().stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(tagNames, hasItem("@ScenarioTag1"));
	}

	@Test
	public void documentGivenStepFields() {
		FeatureTemplate featureTemplate = new TemplateParser().parse(SampleFeatureTemplates.simmpleFeatureTemplate());

		Step step = featureTemplate.getScenario(0).getStep(0);

		List<String> comments = step.getComments().stream().map(s -> s.getValue()).collect(Collectors.toList());
		assertThat(comments, hasItem("#Step comment"));
		// step.getDocString();
		assertThat(step.getKeyword(), is("Given "));
		assertThat(step.getLine(), is(16));
		assertThat(step.getLineRange().getFirst(), is(14));
		assertThat(step.getLineRange().getLast(), is(16));
		assertThat(step.getName(), is("a step"));
		assertThat(step.getOutlineArgs(), empty());
		assertThat(step.getRows(), nullValue());
	}
}
