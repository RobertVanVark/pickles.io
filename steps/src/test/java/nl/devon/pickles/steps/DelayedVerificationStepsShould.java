package nl.devon.pickles.steps;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import nl.devon.pickles.steps.delays.DelayFactory;
import nl.devon.pickles.steps.stubs.StubDelayedVerificationStore;
import nl.devon.pickles.steps.stubs.StubExecutionContext;
import nl.devon.pickles.steps.stubs.StubTestData;

public class DelayedVerificationStepsShould extends FixedTimeTest {

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
				.filter(m -> m.getAnnotation(Then.class).value()
						.equals("^after (" + DelayFactory.DELAY_EXPRESSION + ") (.*) \\(dv-checksum=(\\w{32})\\)$"))
				.toArray();

		assertThat(result.length, is(1));
	}

	@Test
	public void createTimeOffsetDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("02:00 hr", "step expression", "checksum");

		DelayedVerification verification = executionContext.get();
		assertThat(verification.getScenarioChecksum(), is("checksum"));
		assertThat(verification.getVerifyAt(), is(twelve()));
	}

	@Test
	public void createBusinessEventDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("Noon", "step expression", "checksum");

		DelayedVerification verification = executionContext.get();
		assertThat(verification.getScenarioChecksum(), is("checksum"));
		assertThat(verification.getVerifyAt(), is(twelve()));
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

	private Reflections reflections() {
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage("nl.devon"))
						.setUrls(ClasspathHelper.forPackage("nl.devon")).setScanners(new MethodAnnotationsScanner()));
		return reflections;
	}

}
