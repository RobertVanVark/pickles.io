package nl.devon;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Set;

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
	 * call all TestExecutionContexts
	 *
	 */

	DelayedVerificationStorage storage;

	@Test
	public void createDelayedVerification() {
		DelayedVerificationSteps steps = givenStepsStoring(1);

		steps.initiateDelayedVerification("step expression", "checksum");

		DelayedVerification verification = steps.getDelayedVerification();
		assertThat(verification, notNullValue());
		assertThat(verification.getScenarioChecksum(), is("checksum"));
	}

	@Test
	public void createUniqueDelayedVerifications() {
		DelayedVerificationSteps steps = givenStepsStoring(2);

		steps.initiateDelayedVerification("", "");
		DelayedVerification first = steps.getDelayedVerification();

		steps.initiateDelayedVerification("", "");
		DelayedVerification second = steps.getDelayedVerification();

		assertThat(first, not(second));
	}

	@Test
	public void storeDelayedVerification() {
		DelayedVerificationSteps steps = givenStepsStoring(1);

		steps.initiateDelayedVerification("", "");

		verify(storage);
	}

	@Test
	public void matchThenAfterExpression() {
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage("nl.devon"))
						.setUrls(ClasspathHelper.forPackage("nl.devon")).setScanners(new MethodAnnotationsScanner()));

		Set<Method> methods = reflections.getMethodsAnnotatedWith(Then.class);
		Object[] result = methods.stream()
				.filter(m -> m.getAnnotation(Then.class).value().equals("^after (.*) \\(dv-checksum=(.+)\\)$"))
				.toArray();

		assertThat(result.length, is(1));
	}

	@Test
	public void matchGivenAfterExpression() {
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage("nl.devon"))
						.setUrls(ClasspathHelper.forPackage("nl.devon")).setScanners(new MethodAnnotationsScanner()));

		Set<Method> methods = reflections.getMethodsAnnotatedWith(Given.class);
		Object[] result = methods.stream().filter(
				m -> m.getAnnotation(Given.class).value().equals("^Test Execution Context is loaded with dv-id=(.+)$"))
				.toArray();

		assertThat(result.length, is(1));
	}

	private DelayedVerificationSteps givenStepsStoring(int nrDvStored) {
		storage = mock(DelayedVerificationStorage.class);
		expect(storage.store(anyObject(DelayedVerification.class))).andReturn(true).times(nrDvStored);
		replay(storage);

		return new DelayedVerificationSteps(storage);
	}

}
