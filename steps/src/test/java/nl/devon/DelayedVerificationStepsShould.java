package nl.devon;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DelayedVerificationStepsShould {

	/*
	 *
	 * match Then after 2:00 hr .*
	 *
	 * call TestExecutionContext to persist itself
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
				.filter(m -> m.getAnnotation(Then.class).value().equals("^after (.*) \\(dv-checksum=(.+)\\)$"))
				.toArray();

		assertThat(result.length, is(1));
	}

	@Test
	public void createDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("step expression", "checksum");

		DelayedVerification verification = executionContext.get();
		assertThat(verification, notNullValue());
		assertThat(verification.getScenarioChecksum(), is("checksum"));
	}

	@Test
	public void createUniqueDelayedVerificationsInThenAfter() {
		steps.initiateDelayedVerification("", "");
		DelayedVerification first = executionContext.get();

		steps.initiateDelayedVerification("", "");
		DelayedVerification second = executionContext.get();

		assertThat(first, not(second));
	}

	@Test
	public void saveDelayedVerificationInThenAfter() {
		steps.initiateDelayedVerification("", "checksum");
		assertThat(verificationStore.getNrSavesCalled(), is(1));
	}

	@Test
	public void saveTestDataInThenAfter() {
		steps.initiateDelayedVerification("", "checksum");
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
		steps.testExecutionContextIsLoadedWithDvId(dvId);

		assertThat(verificationStore.getNrLoadsCalled(), is(1));
	}

	@Test
	public void setTestExeuctionContextInGiven() {
		String dvId = "loadId";
		steps.testExecutionContextIsLoadedWithDvId(dvId);

		assertThat(executionContext.get().getId(), is(dvId));
	}

	@Test
	public void loadTestDataInGiven() {
		steps.testExecutionContextIsLoadedWithDvId("dvId");
		assertThat(testData.getNrLoadsCalled(), is(1));
	}

	private Reflections reflections() {
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage("nl.devon"))
						.setUrls(ClasspathHelper.forPackage("nl.devon")).setScanners(new MethodAnnotationsScanner()));
		return reflections;
	}

	private class StubExecutionContext implements TestExecutionContext {

		private DelayedVerification verification;

		@Override
		public void set(DelayedVerification verification) {
			this.verification = verification;
		}

		@Override
		public DelayedVerification get() {
			return verification;
		}
	}

	private class StubTestData implements PersistableTestData {

		private int nrSaves = 0;
		private int nrLoads = 0;

		@Override
		public void save(DelayedVerification delayedVerification) {
			nrSaves++;
		}

		@Override
		public void load(DelayedVerification delayedVerification) {
			nrLoads++;
		}

		public int getNrSavesCalled() {
			return nrSaves;
		}

		public int getNrLoadsCalled() {
			return nrLoads;
		}
	}

	private class StubDelayedVerificationStore implements DelayedVerificationStore {

		int nrLoads = 0;
		int nrSaves = 0;

		@Override
		public void save(DelayedVerification verification) {
			nrSaves++;
		}

		@Override
		public DelayedVerification load(String dvId) {
			nrLoads++;
			return new DelayedVerification(dvId, DateTime.now(), DateTime.now(), null, "stubChecksum", "stubFeature");
		}

		public int getNrLoadsCalled() {
			return nrLoads;
		}

		public int getNrSavesCalled() {
			return nrSaves;
		}
	}

}
