package io.pickles.steps;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.pickles.steps.delays.DelayFactory;
import io.pickles.steps.delays.BaseFixedTimeTest;
import io.pickles.steps.stubs.StubDelayedVerificationStore;
import io.pickles.steps.stubs.StubExecutionContext;
import io.pickles.steps.stubs.StubTestData;

public class DelayedVerificationStepsShould extends BaseFixedTimeTest {

	/*
	 * match Then after Business-Event .*
	 *
	 * delegate timing for Business Events to consumer application
	 *
	 */

	private DelayedVerificationSteps steps;
	private TestExecutionContext executionContext;
	private StubTestData testData;
	private StubDelayedVerificationStore verificationStore;

	@Before
	public void givenStepsWithExecutionContextAndTestData() {
		steps = new DelayedVerificationSteps();

		verificationStore = new StubDelayedVerificationStore();
		steps.setDelayedVerificationStore(verificationStore);

		executionContext = new StubExecutionContext();
		steps.setTestExecutionContext(executionContext);

		testData = new StubTestData();
		steps.setPersistableTestData(testData);
	}

	@Test
	public void matchThenAfterExpression() {
		Set<Method> methods = reflections().getMethodsAnnotatedWith(Then.class);
		Object[] result = methods.stream()
				.filter(m -> m.getAnnotation(Then.class).value().equals("^(" + DelayFactory.DELAY_EXPRESSION + ") (.*) " //
						+ "\\(dvChecksum=(.+), dvId=(.+), dvFeatureUri=(.+)\\)$"))
				.toArray();

		assertThat(result.length, is(1));
	}

	@Test
	public void createTimeOffsetDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("02:00 hr", "step expression", "checksum", "id", "features/bdd.feature");

		DelayedVerification verification = executionContext.get();
		assertThat(verification.getScenarioChecksum(), is("checksum"));
		assertThat(verification.getVerifyAt(), is(twelve()));
	}

	@Test
	public void createBusinessEventDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("Noon", "step expression", "checksum", "id", "features/bdd.feature");

		DelayedVerification verification = executionContext.get();
		assertThat(verification.getScenarioChecksum(), is("checksum"));
		assertThat(verification.getVerifyAt(), is(twelve()));
	}

	@Test
	public void saveDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("00:00 hr", "", "checksum", "id", "features/bdd.feature");
		assertThat(verificationStore.getNrSavesCalled(), is(1));
	}

	@Test
	public void useProvidedChecksumInThenAfter() {
		steps.initiateDelayedVerification("00:00 hr", "", "checksum to be saved", "id", "features/bdd.feature");
		assertThat(verificationStore.getDvSaved().getScenarioChecksum(), is("checksum to be saved"));
	}

	@Test
	public void useProvidedIdInThenAfter() {
		steps.initiateDelayedVerification("00:00 hr", "", "checksum", "id to be saved", "features/bdd.feature");
		assertThat(verificationStore.getDvSaved().getId(), is("id to be saved"));
	}

	@Test
	public void useProvidedFeatureUriInThenAfter() {
		steps.initiateDelayedVerification("00:00 hr", "", "checksum", "id", "uri to be saved");
		assertThat(verificationStore.getDvSaved().getFeatureUri(), is("uri to be saved"));
	}

	@Test
	public void saveTestDataInThenAfter() {
		steps.initiateDelayedVerification("00:00 hr", "", "checksum", "id", "features/bdd.feature");
		assertThat(testData.getNrSavesCalled(), is(1));
	}

	@Test
	public void matchGivenExpression() {
		Set<Method> methods = reflections().getMethodsAnnotatedWith(Given.class);
		Object[] result = methods.stream().filter(
				m -> m.getAnnotation(Given.class).value().equals("^Test Execution Context is loaded for dvId=(.+)$"))
				.toArray();

		assertThat(result.length, is(1));
	}

	@Test
	public void loadDelayedVerificationInGiven() {
		String dvId = "loadId";
		steps.testExecutionContextIsLoadedForDvId(dvId);

		assertThat(verificationStore.getNrLoadsCalled(), is(1));
	}

	@Test
	public void setTestExeuctionContextInGiven() {
		String dvId = "loadId";
		steps.testExecutionContextIsLoadedForDvId(dvId);

		assertThat(executionContext.get().getId(), is(dvId));
	}

	@Test
	public void loadTestDataInGiven() {
		steps.testExecutionContextIsLoadedForDvId("dvId");
		assertThat(testData.getNrLoadsCalled(), is(1));
	}

	private Reflections reflections() {
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage("io.pickles"))
						.setUrls(ClasspathHelper.forPackage("io.pickles")).setScanners(new MethodAnnotationsScanner()));
		return reflections;
	}

	@Test
	public void matchThenAfterExamples() {
		Set<Method> methods = reflections().getMethodsAnnotatedWith(Then.class);
		List<String> expressions = methods.stream().map(m -> m.getAnnotation(Then.class).value())
				.collect(Collectors.toList());

		int matches = 0;
		for (String expression : expressions) {
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(
					"after 2:00 hr billing information is generated (dvChecksum=28689529752417372157209393638731288334397142686961947502620568881217480631817, dvId=1, dvFeatureUri=features/SimpleBankingScenario.feature)");
			if (matcher.matches()) {
				matches++;
			}
		}

		assertThat(matches, is(1));
	}

}
