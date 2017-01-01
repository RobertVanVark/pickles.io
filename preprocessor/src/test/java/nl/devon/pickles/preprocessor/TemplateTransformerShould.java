package nl.devon.pickles.preprocessor;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.preprocessor.stubs.DummyDelayedVerificationStore;

public class TemplateTransformerShould {

	/*
	 * *** Every Then after should result in new follow up scenario
	 *
	 * Every ending Then after should be appended with (dvChecksum=......,dvFeature=featurefilename)
	 *
	 * *** Every follow up scenario should start with Given delayed verification.... (dvId=1)
	 *
	 * *** Every follow up scenario should have Then copied from Then after
	 *
	 * *** Every follow up scenario name should be unique (by adding dvId in name?)
	 *
	 * *** Every follow up scenario name should be the same as the initiation scenario appended with dvId
	 *
	 * *** Every Scenario should have @Pickles_Initiation
	 *
	 * *** Every follow up scenario should have @Pickles_Verification
	 *
	 * *** Tags should be copied from Scenario to Verification scenarios
	 *
	 * Some tags will not be copied to Verification scenarios (configurable). These tags should be moved from Feature to
	 * initiation scenario
	 *
	 * *** dvId in verificaiton scenarios should be retreived from DelayedVerificationStore
	 *
	 * Checksum calculation !
	 *
	 * Construct new id for verification scenarios ?
	 *
	 * Skip follow up scenarios without delayed verification
	 *
	 * Should add verification scenario for each dvId for that checksum
	 *
	 * Each verification scenario (for different dvId) should have same steps :-)
	 *
	 */

	private Preprocessor preprocessor;

	@Before
	public void givenDummyDelayedVerificationStore() {
		preprocessor = new Preprocessor();
		preprocessor.setDelayedVerificationStore(new DummyDelayedVerificationStore());
	}

	@Test
	public void splitAtEveryThenAfter() {
		FeatureTemplate featureTemplate = transform(twoThenAfterScenario());

		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(3));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(3));
		assertThat(featureTemplate.getScenarios().get(1).getSteps(), Matchers.hasSize(4));
		assertThat(featureTemplate.getScenarios().get(2).getSteps(), Matchers.hasSize(2));
	}

	@Test
	public void startEachFollowUpScenarioWithGiven() {
		FeatureTemplate featureTemplate = transform(twoThenAfterScenario());

		Step firstScenarioStep = featureTemplate.getScenarios().get(1).getSteps().get(0);
		assertThat(firstScenarioStep.getKeyword(), is("Given "));
		assertThat(firstScenarioStep.getName(), is("Test Execution Context is loaded for dvId=1000"));

		Step secondScenarioStep = featureTemplate.getScenarios().get(2).getSteps().get(0);
		assertThat(secondScenarioStep.getKeyword(), is("Given "));
		assertThat(secondScenarioStep.getName(), is("Test Execution Context is loaded for dvId=1001"));
	}

	@Test
	public void copyEachThenAfterToVerificationScenario() {
		FeatureTemplate featureTemplate = transform(twoThenAfterScenario());

		Step firstScenarioStep = featureTemplate.getScenarios().get(1).getSteps().get(1);
		assertThat(firstScenarioStep.getKeyword(), is("Then "));
		assertThat(firstScenarioStep.getName(), is("a first delayed outcome"));

		Step secondScenarioStep = featureTemplate.getScenarios().get(2).getSteps().get(1);
		assertThat(secondScenarioStep.getKeyword(), is("Then "));
		assertThat(secondScenarioStep.getName(), is("a second delayed outcome"));
	}

	@Test
	public void copyScenarioNameToVerificationScenarios() {
		FeatureTemplate featureTemplate = transform(twoThenAfterScenario());

		Scenario initiationScenario = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(initiationScenario.getName(), is("scenario name"));

		Scenario firstVerification = featureTemplate.getScenarios().get(1).getScenario();
		assertThat(firstVerification.getName(), is("scenario name (dvId=1000)"));

		Scenario secondVerification = featureTemplate.getScenarios().get(2).getScenario();
		assertThat(secondVerification.getName(), is("scenario name (dvId=1001)"));
	}

	@Test
	public void createUniqueVerificationScenarios() {
		FeatureTemplate featureTemplate = transform(twoThenAfterScenario());

		Scenario firstVerification = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(firstVerification.getName(), startsWith("scenario name"));

		Scenario secondVerification = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(secondVerification.getName(), startsWith("scenario name"));
	}

	@Test
	public void copyTagsToVerificationScenarios() {
		FeatureTemplate featureTemplate = transform(twoThenAfterScenario());

		List<Tag> tags = featureTemplate.getScenarios().get(0).getScenario().getTags();
		List<Tag> firstTags = featureTemplate.getScenarios().get(1).getScenario().getTags();
		List<Tag> secondTags = featureTemplate.getScenarios().get(2).getScenario().getTags();
		for (Tag tag : tags) {
			if (!"@Pickles_Initiation".equals(tag.getName())) {
				assertThat(firstTags, hasItem(tag));
				assertThat(secondTags, hasItem(tag));
			}
		}
	}

	@Test
	public void addInitiationTagToScenario() {
		FeatureTemplate featureTemplate = transform(twoThenAfterScenario());

		List<Tag> tags = featureTemplate.getScenarios().get(0).getScenario().getTags();
		List<String> tagNames = tags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(tagNames, hasItem("@Pickles_Initiation"));
	}

	@Test
	public void addVerificationTagToScenario() {
		FeatureTemplate featureTemplate = transform(twoThenAfterScenario());

		List<Tag> firstTags = featureTemplate.getScenarios().get(1).getScenario().getTags();
		List<String> firstTagNames = firstTags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(firstTagNames, hasItem("@Pickles_Verification"));

		List<Tag> secondTags = featureTemplate.getScenarios().get(2).getScenario().getTags();
		List<String> secondTagNames = secondTags.stream().map(t -> t.getName()).collect(Collectors.toList());
		assertThat(secondTagNames, hasItem("@Pickles_Verification"));
	}

	private FeatureTemplate transform(List<String> scenario) {
		FeatureTemplate featureTemplate = new TemplateParser().parse(scenario);
		TemplateTransformer transformer = new TemplateTransformer(featureTemplate, new DummyDelayedVerificationStore());
		return transformer.doIt();
	}

	private List<String> twoThenAfterScenario() {
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
		template.add("Then after 02:00 hr a first delayed outcome");
		template.add("Then some direct verification");
		template.add("Then after 01:00 hr a second delayed outcome");

		return template;
	}
}
