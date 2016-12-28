package nl.devon;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import nl.devon.stubs.StubDelayedVerificationStore;
import nl.devon.stubs.StubExecutionContext;
import nl.devon.stubs.StubTestData;

public class DelayedVerificationStepsShould {

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

	@Before
	public void givenTimeFixedAtTwelve() {
		DateTimeUtils.setCurrentMillisFixed(twelve().getMillis());
	}

	@After
	public void givenDateTimeReset() {
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void matchThenAfterExpression() {
		Set<Method> methods = reflections().getMethodsAnnotatedWith(Then.class);
		Object[] result = methods.stream()
				.filter(m -> m.getAnnotation(Then.class).value().equals(
						"^after (" + DelayedVerificationSteps.DELAY_EXPRESSION + ") (.*) \\(dv-checksum=(\\w{32})\\)$"))
				.toArray();

		assertThat(result.length, is(1));
	}

	@Test
	public void createDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("02:00 hr", "step expression", "checksum");

		DelayedVerification verification = executionContext.get();
		assertThat(verification.getScenarioChecksum(), is("checksum"));
		assertThat(verification.getVerifyAt(), is(fourteen()));
	}

	@Test
	public void saveDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("00:00 hr", "", "checksum");
		assertThat(verificationStore.getNrSavesCalled(), is(1));
	}

	@Test
	public void saveTestDataInThenAfter() {
		steps.initiateDelayedVerification("00:00 hr", "", "checksum");
		assertThat(testData.getNrSavesCalled(), is(1));
	}

	@Test
	public void matchGivenExpression() {
		Set<Method> methods = reflections().getMethodsAnnotatedWith(Given.class);
		Object[] result = methods.stream().filter(
				m -> m.getAnnotation(Given.class).value().equals("^Test Execution Context is loaded with dv-id=(.+)$"))
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

	private DateTime twelve() {
		return midnight().withHourOfDay(12);
	}

	private DateTime fourteen() {
		return midnight().withHourOfDay(14);
	}

	private DateTime midnight() {
		return DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
	}

	private Reflections reflections() {
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage("nl.devon"))
						.setUrls(ClasspathHelper.forPackage("nl.devon")).setScanners(new MethodAnnotationsScanner()));
		return reflections;
	}

}
