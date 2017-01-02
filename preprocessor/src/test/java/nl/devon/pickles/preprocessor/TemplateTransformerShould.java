package nl.devon.pickles.preprocessor;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Test;

import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.preprocessor.stubs.DummyDelayedVerificationStore;
import nl.devon.pickles.preprocessor.stubs.SampleFeatureTemplates;

public class TemplateTransformerShould {

	/*
	 * Every ending Then after should be appended with (dvChecksum=......,dvFeature=featurefilename)
	 *
	 * Some tags will not be copied to Verification scenarios (configurable). These tags should be moved from Feature to
	 * initiation scenario
	 *
	 * Checksum calculation !
	 *
	 * Construct new id for verification scenarios ?
	 *
	 * after should be part of pattern (extensible to other patterns like at ....)
	 *
	 * strip datatable from ten after in initiation step
	 */

	@Test
	public void splitAtEveryThenAfter() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(3));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(3));
		assertThat(featureTemplate.getScenarios().get(1).getSteps(), Matchers.hasSize(4));
		assertThat(featureTemplate.getScenarios().get(2).getSteps(), Matchers.hasSize(2));
	}

	@Test
	public void startEachFollowUpScenarioWithGiven() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		Step firstScenarioStep = featureTemplate.getScenarios().get(1).getSteps().get(0);
		assertThat(firstScenarioStep.getKeyword(), is("Given "));
		assertThat(firstScenarioStep.getName(), is("Test Execution Context is loaded for dvId=1000"));

		Step secondScenarioStep = featureTemplate.getScenarios().get(2).getSteps().get(0);
		assertThat(secondScenarioStep.getKeyword(), is("Given "));
		assertThat(secondScenarioStep.getName(), is("Test Execution Context is loaded for dvId=1001"));
	}

	@Test
	public void copyEachThenAfterToVerificationScenario() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		Step firstScenarioStep = featureTemplate.getScenarios().get(1).getSteps().get(1);
		assertThat(firstScenarioStep.getKeyword(), is("Then "));
		assertThat(firstScenarioStep.getName(), is("a first delayed outcome"));

		Step secondScenarioStep = featureTemplate.getScenarios().get(2).getSteps().get(1);
		assertThat(secondScenarioStep.getKeyword(), is("Then "));
		assertThat(secondScenarioStep.getName(), is("a second delayed outcome"));
	}

	@Test
	public void createUniqueVerificationScenarios() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		Scenario initiationScenario = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(initiationScenario.getName(), is("scenario name"));

		Scenario firstVerification = featureTemplate.getScenarios().get(1).getScenario();
		assertThat(firstVerification.getName(), is("scenario name (dvId=1000)"));

		Scenario secondVerification = featureTemplate.getScenarios().get(2).getScenario();
		assertThat(secondVerification.getName(), is("scenario name (dvId=1001)"));
	}

	@Test
	public void copyScenarioNameToVerificationScenarios() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		Scenario firstVerification = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(firstVerification.getName(), startsWith("scenario name"));

		Scenario secondVerification = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(secondVerification.getName(), startsWith("scenario name"));
	}

	@Test
	public void copyTagsToVerificationScenarios() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		List<Tag> tags = featureTemplate.getScenarios().get(0).getScenario().getTags();
		List<Tag> firstTags = featureTemplate.getScenarios().get(1).getScenario().getTags();
		List<Tag> secondTags = featureTemplate.getScenarios().get(2).getScenario().getTags();
		for (Tag tag : tags) {
			if (!"@PicklesInitiation".equals(tag.getName())) {
				assertThat(firstTags, hasItem(tag));
				assertThat(secondTags, hasItem(tag));
			}
		}
	}

	@Test
	public void addInitiationTagToScenario() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		List<Tag> tags = featureTemplate.getScenarios().get(0).getScenario().getTags();
		List<String> tagNames = tags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(tagNames, hasItem("@PicklesInitiation"));
	}

	@Test
	public void addVerificationTagToScenario() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		List<Tag> firstTags = featureTemplate.getScenarios().get(1).getScenario().getTags();
		List<String> firstTagNames = firstTags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(firstTagNames, hasItem("@PicklesVerification"));

		List<Tag> secondTags = featureTemplate.getScenarios().get(2).getScenario().getTags();
		List<String> secondTagNames = secondTags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(secondTagNames, hasItem("@PicklesVerification"));
	}

	@Test
	public void skipVerificationScenarioWithoutDvs() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 0);

		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(1));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(3));
	}

	private FeatureTemplate transform(List<String> templateLines, int nrDvs) {
		FeatureTemplate featureTemplate = new TemplateParser().parse(templateLines);
		TemplateTransformer transformer = new TemplateTransformer(featureTemplate,
				new DummyDelayedVerificationStore(nrDvs));
		return transformer.doIt();
	}

	@Test
	public void addVerificationScenarioForEachDelayedVerification() {
		FeatureTemplate featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 2);

		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(5));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(3));

		assertThat(featureTemplate.getScenarios().get(1).getSteps(), Matchers.hasSize(4));
		assertThat(firstStepOf(featureTemplate, 1).getName(), is("Test Execution Context is loaded for dvId=1000"));

		assertThat(featureTemplate.getScenarios().get(2).getSteps(), Matchers.hasSize(4));
		assertThat(firstStepOf(featureTemplate, 2).getName(), is("Test Execution Context is loaded for dvId=1001"));

		assertThat(featureTemplate.getScenarios().get(3).getSteps(), Matchers.hasSize(2));
		assertThat(firstStepOf(featureTemplate, 3).getName(), is("Test Execution Context is loaded for dvId=1002"));

		assertThat(featureTemplate.getScenarios().get(4).getSteps(), Matchers.hasSize(2));
		assertThat(firstStepOf(featureTemplate, 4).getName(), is("Test Execution Context is loaded for dvId=1003"));
	}

	private Step firstStepOf(FeatureTemplate featureTemplate, int scenarioId) {
		return featureTemplate.getScenarios().get(scenarioId).getSteps().get(0);
	}

}
