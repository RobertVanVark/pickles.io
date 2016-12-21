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

    public DelayedVerificationSteps() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath()));

        Set<Class<?>> stores = reflections.getTypesAnnotatedWith(DVStore.class);
        if (stores.size() == 1) {
            for (Class<?> store : stores) {
                try {
                    Object obj = store.newInstance();
                    if (obj instanceof DelayedVerificationStore) {
                        storage = (DelayedVerificationStore) obj;
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public DelayedVerificationSteps(DelayedVerificationStore store) {
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
        System.out.println("Load Test Execution Context with id=" + dvId);
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath()));

//        Set<Class<?>> callbacks = reflections.getTypesAnnotatedWith(Loaded.class);
//        for (Class<?> callback : callbacks) {
//            try {
//                Object obj = callback.newInstance();
//                if (obj instanceof DelayedVerificationStore) {
//                    storage = (DelayedVerificationStore) obj;
//                }
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public DelayedVerification getDelayedVerification() {
        return verification;
    }
}
