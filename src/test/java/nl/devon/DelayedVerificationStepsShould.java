package nl.devon;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Set;

import org.joda.time.DateTime;
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

    DelayedVerificationStore storage;

    @Test
    public void createDelayedVerification() {
        DelayedVerificationSteps steps = stepsWithMockStoringTimes();

        steps.initiateDelayedVerification("step expression", "checksum");

        DelayedVerification verification = steps.getDelayedVerification();
        assertThat(verification, notNullValue());
        assertThat(verification.getScenarioChecksum(), is("checksum"));
    }

    @Test
    public void createUniqueDelayedVerifications() {
        DelayedVerificationSteps steps = stepsWithMockStoringTimes();

        steps.initiateDelayedVerification("", "");
        DelayedVerification first = steps.getDelayedVerification();

        steps.initiateDelayedVerification("", "");
        DelayedVerification second = steps.getDelayedVerification();

        assertThat(first, not(second));
    }

    @Test
    public void storeDelayedVerification() {
        DelayedVerificationSteps steps = stepsWithMockStoringTimes();

        steps.initiateDelayedVerification("", "");

        verify(storage, times(1)).save(any(DelayedVerification.class));
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

    @Test
    public void loadDelayedVerification() {
        String dvId = "an-id";
        DelayedVerificationSteps steps = stepsWithMockLoad(dvId);

        steps.testExecutionContextIsLoadedWithDvId(dvId);
        verify(storage).load(dvId);
    }

    private DelayedVerificationSteps stepsWithMockLoad(String dvId) {
        storage = mock(DelayedVerificationStore.class);
        DelayedVerification verification = new DelayedVerification(DateTime.now(), DateTime.now(), "", dvId);
        when(storage.load(dvId)).thenReturn(verification);

        return new DelayedVerificationSteps(storage);
    }

    private DelayedVerificationSteps stepsWithMockStoringTimes() {
        storage = mock(DelayedVerificationStore.class);

        return new DelayedVerificationSteps(storage);
    }

}
