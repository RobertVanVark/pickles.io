package nl.devon.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import nl.devon.pickles.preprocessor.Preprocessor;
import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.DelayedVerificationStore;

public class PreprocessorShould {

	/*
	 * FeatureTemplate.toGherkinFeatureFile()
	 *
	 * transform all featuretemplates on the classpath or features setting in CucumberOptions into feature files
	 *
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
	 * Every Scenario should have @Pickles_Initiation
	 *
	 * Every follow up scenario should have @Pickles_Verification
	 *
	 * Tags should be copied from Scenario to Verification scenarios
	 *
	 * Some tags will not be copied to Verification scenarios (configurable). These tags should be moved from Feature to
	 * initiation scenario
	 *
	 * Skip follow up scenarios without delayed verification
	 *
	 * *** dvId in verificaiton scenarios should be retreived from DelayedVerificationStore
	 *
	 * Checksum calculation !
	 *
	 * Construct new id for verification scenarios ?
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
		FeatureTemplate featureTemplate = preprocessor.process(twoThenAfterScenario());

		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(3));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(3));
		assertThat(featureTemplate.getScenarios().get(1).getSteps(), Matchers.hasSize(4));
		assertThat(featureTemplate.getScenarios().get(2).getSteps(), Matchers.hasSize(2));
	}

	@Test
	public void startEachFollowUpScenarioWithGiven() {
		FeatureTemplate featureTemplate = preprocessor.process(twoThenAfterScenario());

		Step firstScenarioStep = featureTemplate.getScenarios().get(1).getSteps().get(0);
		assertThat(firstScenarioStep.getKeyword(), is("Given "));
		assertThat(firstScenarioStep.getName(), is("Test Execution Context is loaded for dvId=1000"));

		Step secondScenarioStep = featureTemplate.getScenarios().get(2).getSteps().get(0);
		assertThat(secondScenarioStep.getKeyword(), is("Given "));
		assertThat(secondScenarioStep.getName(), is("Test Execution Context is loaded for dvId=1001"));
	}

	@Test
	public void copyEachThenAfterToVerificationScenario() {
		FeatureTemplate featureTemplate = preprocessor.process(twoThenAfterScenario());

		Step firstScenarioStep = featureTemplate.getScenarios().get(1).getSteps().get(1);
		assertThat(firstScenarioStep.getKeyword(), is("Then "));
		assertThat(firstScenarioStep.getName(), is("a first delayed outcome"));

		Step secondScenarioStep = featureTemplate.getScenarios().get(2).getSteps().get(1);
		assertThat(secondScenarioStep.getKeyword(), is("Then "));
		assertThat(secondScenarioStep.getName(), is("a second delayed outcome"));
	}

	@Test
	public void copyScenarioNameToVerificationScenarios() {
		FeatureTemplate featureTemplate = preprocessor.process(twoThenAfterScenario());

		Scenario initiationScenario = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(initiationScenario.getName(), is("scenario name"));

		Scenario firstVerification = featureTemplate.getScenarios().get(1).getScenario();
		assertThat(firstVerification.getName(), is("scenario name (dvId=1000)"));

		Scenario secondVerification = featureTemplate.getScenarios().get(2).getScenario();
		assertThat(secondVerification.getName(), is("scenario name (dvId=1001)"));
	}

	@Test
	public void createUniqueVerificationScenarios() {
		FeatureTemplate featureTemplate = preprocessor.process(twoThenAfterScenario());

		Scenario firstVerification = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(firstVerification.getName(), startsWith("scenario name"));

		Scenario secondVerification = featureTemplate.getScenarios().get(0).getScenario();
		assertThat(secondVerification.getName(), startsWith("scenario name"));
	}

	@Test
	public void parseTemplateFile() throws UnsupportedEncodingException, FileNotFoundException, RuntimeException {
		String path = "target/test-classes/features/SimpleBankingScenario.feature";
		FeatureTemplate featureTemplate = preprocessor.parse(path);

		assertThat(featureTemplate.getFeature(), notNullValue());
		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(2));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(8));
		assertThat(featureTemplate.getScenarios().get(1).getSteps(), Matchers.hasSize(7));
	}

	@Test
	public void parseTemplate() {
		FeatureTemplate featureTemplate = preprocessor.process(oneScenarioFeature());

		assertThat(featureTemplate.getFeature(), notNullValue());
		assertThat(featureTemplate.getScenarios(), Matchers.hasSize(1));
		assertThat(featureTemplate.getScenarios().get(0).getSteps(), Matchers.hasSize(3));
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

	private class DummyDelayedVerificationStore implements DelayedVerificationStore {

		private Integer nextId = 1000;

		@Override
		public void create(DelayedVerification verification) {
		}

		@Override
		public DelayedVerification read(String dvId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void update(DelayedVerification verification) {
			// TODO Auto-generated method stub

		}

		@Override
		public List<DelayedVerification> readAllForChecksum(String checksum) {
			DelayedVerification verification = new DelayedVerification(nextId.toString(), null, null, null, checksum,
					null);
			nextId++;
			return Arrays.asList(verification);
		}

		@Override
		public List<DelayedVerification> readAllToVerify(String checksum) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
