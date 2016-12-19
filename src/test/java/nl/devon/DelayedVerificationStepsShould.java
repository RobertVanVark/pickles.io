package nl.devon;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.Set;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DelayedVerificationStepsShould {

    /*
       **** Step created Delayed Verificaton
       *
       **** create unique Delayed Verifications
       *
       **** match Then after
       *
       * Step can be used in consumer application
       *
       * match Then after 2:00 hr .*
       *
       * call TestExecutionContext to persist itself
       *
       * call all TestExecutionContexts
       *
       *
     */

    @Test
    public void createDelayedVerification() {
        DelayedVerificationSteps steps = new DelayedVerificationSteps();
        steps.initiateDelayedVerification("","");

        assertThat(steps.getDelayedVerification(), is("1"));
    }

    @Test
    public void createUniqueDelayedVerifications() {
        DelayedVerificationSteps steps = new DelayedVerificationSteps();

        steps.initiateDelayedVerification("","");
        String first = steps.getDelayedVerification();

        steps.initiateDelayedVerification("","");
        String second = steps.getDelayedVerification();

        assertThat(first, not(second));
    }

    @Test
    public void matchThenAfterExpression() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().includePackage("nl.devon"))
                .setUrls(ClasspathHelper.forPackage("nl.devon"))
                .setScanners(new MethodAnnotationsScanner()));

        Set<Method> methods = reflections.getMethodsAnnotatedWith(Then.class);
        Object[] result = methods.stream().filter(m -> m.getAnnotation(Then.class).value().equals("^after (.*) \\(dv-checksum=(.+)\\)$")).toArray();

        assertThat(result.length, is(1));
    }

    @Test
    public void matchGivenAfterExpression() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().includePackage("nl.devon"))
                .setUrls(ClasspathHelper.forPackage("nl.devon"))
                .setScanners(new MethodAnnotationsScanner()));

        Set<Method> methods = reflections.getMethodsAnnotatedWith(Given.class);
        Object[] result = methods.stream().filter(m -> m.getAnnotation(Given.class).value().equals("^Test Execution Context is loaded with dv-id=(.+)$")).toArray();

        assertThat(result.length, is(1));
    }


}
