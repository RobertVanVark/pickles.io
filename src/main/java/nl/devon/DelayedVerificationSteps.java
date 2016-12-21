package nl.devon;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.joda.time.DateTime;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

public class DelayedVerificationSteps {

    private DelayedVerification verification;
    private DelayedVerificationStore storage;

    public DelayedVerificationSteps() throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath()));

        Set<Class<?>> stores = reflections.getTypesAnnotatedWith(DVStore.class);
        if (stores.size() == 1) {
            for (Class<?> store : stores) {
                Object obj = null;
                obj = store.newInstance();
                if (obj instanceof DelayedVerificationStore) {
                    storage = (DelayedVerificationStore) obj;
                }
            }
        }
    }

    DelayedVerificationSteps(DelayedVerificationStore store) {
        storage = store;
    }

    @Then("^after (.*) \\(dv-checksum=(.+)\\)$")
    public void initiateDelayedVerification(String expression, String checksum) {
        verification = new DelayedVerification(DateTime.now(), checksum);
        storage.save(verification);
    }

    @Given("^Test Execution Context is loaded with dv-id=(.+)$")
    public void testExecutionContextIsLoadedWithDvId(String dvId) {
        storage.load(dvId);
    }

    public DelayedVerification getDelayedVerification() {
        return verification;
    }
}
