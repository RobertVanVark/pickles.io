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
import gherkin.formatter.model.Tag;
import nl.devon.pickles.preprocessor.model.FeatureModel;
import nl.devon.pickles.preprocessor.model.StepModel;
import nl.devon.pickles.preprocessor.stubs.DummyDelayedVerificationStore;
import nl.devon.pickles.preprocessor.stubs.SampleFeatureTemplates;

public class TemplateTransformerShould {

	/*
	 * Construct new id for verification scenarios ?
	 */

	@Test
	public void splitAtEveryThenAfter() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(3));
		assertThat(featureTemplate.getScenario(0).getSteps(), Matchers.hasSize(3));
		assertThat(featureTemplate.getScenario(1).getSteps(), Matchers.hasSize(4));
		assertThat(featureTemplate.getScenario(2).getSteps(), Matchers.hasSize(2));
	}

	@Test
	public void appendChecksumToEachThenAfter() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		assertThat(featureTemplate.getScenario(0).getLastStep().getName(), is(
				"after 02:00 hr a first delayed outcome (dvChecksum=5763855420898474544005950582865379151478164379053680340472962992051329764337)"));

		assertThat(featureTemplate.getScenario(1).getLastStep().getName(), is(
				"after 01:00 hr a second delayed outcome (dvChecksum=46358382589510680607637515553784553654583731010741977208799246255849153888211)"));

	}

	@Test
	public void startEachFollowUpScenarioWithGiven() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		StepModel firstScenarioStep = featureTemplate.getScenario(1).getStep(0);
		assertThat(firstScenarioStep.getKeyword(), is("Given "));
		assertThat(firstScenarioStep.getName(), is("Test Execution Context is loaded for dvId=1000"));

		StepModel secondScenarioStep = featureTemplate.getScenario(2).getStep(0);
		assertThat(secondScenarioStep.getKeyword(), is("Given "));
		assertThat(secondScenarioStep.getName(), is("Test Execution Context is loaded for dvId=1001"));
	}

	@Test
	public void copyEachThenAfterToVerificationScenario() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		StepModel firstScenarioStep = featureTemplate.getScenario(1).getStep(1);
		assertThat(firstScenarioStep.getKeyword(), is("Then "));
		assertThat(firstScenarioStep.getName(), is("a first delayed outcome"));

		StepModel secondScenarioStep = featureTemplate.getScenario(2).getStep(1);
		assertThat(secondScenarioStep.getKeyword(), is("Then "));
		assertThat(secondScenarioStep.getName(), is("a second delayed outcome"));
	}

	@Test
	public void createUniqueVerificationScenarios() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		Scenario initiationScenario = featureTemplate.getScenario(0).getScenario();
		assertThat(initiationScenario.getName(), is("scenario name"));

		Scenario firstVerification = featureTemplate.getScenario(1).getScenario();
		assertThat(firstVerification.getName(), is("scenario name (dvId=1000)"));

		Scenario secondVerification = featureTemplate.getScenario(2).getScenario();
		assertThat(secondVerification.getName(), is("scenario name (dvId=1001)"));
	}

	@Test
	public void copyScenarioNameToVerificationScenarios() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		Scenario firstVerification = featureTemplate.getScenario(0).getScenario();
		assertThat(firstVerification.getName(), startsWith("scenario name"));

		Scenario secondVerification = featureTemplate.getScenario(0).getScenario();
		assertThat(secondVerification.getName(), startsWith("scenario name"));
	}

	@Test
	public void copyTagsToVerificationScenarios() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		List<Tag> tags = featureTemplate.getScenario(0).getScenario().getTags();
		List<Tag> firstTags = featureTemplate.getScenario(1).getScenario().getTags();
		List<Tag> secondTags = featureTemplate.getScenario(2).getScenario().getTags();
		for (Tag tag : tags) {
			if (!"@PicklesInitiation".equals(tag.getName())) {
				assertThat(firstTags, hasItem(tag));
				assertThat(secondTags, hasItem(tag));
			}
		}
	}

	@Test
	public void addInitiationTagToScenario() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		List<Tag> tags = featureTemplate.getScenario(0).getScenario().getTags();
		List<String> tagNames = tags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(tagNames, hasItem("@PicklesInitiation"));
	}

	@Test
	public void addVerificationTagToScenario() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 1);

		List<Tag> firstTags = featureTemplate.getScenario(1).getScenario().getTags();
		List<String> firstTagNames = firstTags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(firstTagNames, hasItem("@PicklesVerification"));

		List<Tag> secondTags = featureTemplate.getScenario(2).getScenario().getTags();
		List<String> secondTagNames = secondTags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(secondTagNames, hasItem("@PicklesVerification"));
	}

	@Test
	public void skipVerificationScenarioWithoutDvs() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 0);

		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(1));
		assertThat(featureTemplate.getScenario(0).getSteps(), Matchers.hasSize(3));
	}

	private FeatureModel transform(List<String> templateLines, int nrDvs) {
		FeatureModel featureTemplate = new TemplateParser().parse(templateLines);
		TemplateTransformer transformer = new TemplateTransformer(featureTemplate,
				new DummyDelayedVerificationStore(nrDvs));
		return transformer.doIt();
	}

	@Test
	public void addVerificationScenarioForEachDelayedVerification() {
		FeatureModel featureTemplate = transform(SampleFeatureTemplates.twoThenAfterScenario(), 2);

		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(5));
		assertThat(featureTemplate.getScenario(0).getSteps(), Matchers.hasSize(3));

		assertThat(featureTemplate.getScenario(1).getSteps(), Matchers.hasSize(4));
		assertThat(firstStepOf(featureTemplate, 1).getName(), is("Test Execution Context is loaded for dvId=1000"));

		assertThat(featureTemplate.getScenario(2).getSteps(), Matchers.hasSize(4));
		assertThat(firstStepOf(featureTemplate, 2).getName(), is("Test Execution Context is loaded for dvId=1001"));

		assertThat(featureTemplate.getScenario(3).getSteps(), Matchers.hasSize(2));
		assertThat(firstStepOf(featureTemplate, 3).getName(), is("Test Execution Context is loaded for dvId=1002"));

		assertThat(featureTemplate.getScenario(4).getSteps(), Matchers.hasSize(2));
		assertThat(firstStepOf(featureTemplate, 4).getName(), is("Test Execution Context is loaded for dvId=1003"));
	}

	private StepModel firstStepOf(FeatureModel featureTemplate, int scenarioId) {
		return featureTemplate.getScenario(scenarioId).getStep(0);
	}

}
