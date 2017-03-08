package io.pickles.junit;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.Resource;
import cucumber.runtime.io.ResourceLoader;
import io.pickles.preprocessor.PicklesPreprocessorException;
import io.pickles.preprocessor.Preprocessor;
import io.pickles.steps.DelayedVerificationStore;

public class PicklesCucumberRunner extends Runner {

	private Class<?> clazz;
	private DelayedVerificationStore store;

	private Cucumber cucumber;

	public PicklesCucumberRunner(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setDelayedVerificationStore(DelayedVerificationStore store) {
		this.store = store;
	}

	@Override
	public Description getDescription() {
		return cucumber.getDescription();
	}

	@Override
	public void run(RunNotifier notifier) {

		System.out.println("Starting preprocessing of feature templates...");
		long startTime = System.currentTimeMillis();

		Preprocessor preprocessor = new Preprocessor();
		preprocessor.setDelayedVerificationStore(store);

		for (File featureTemplate : getFeatureTemplateFiles()) {
			for (String line : preprocessor.process(featureTemplate)) {
				System.out.println(line);
			}
		}

		System.out.println("Done in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds.");

		try {
			cucumber = new Cucumber(clazz);
		} catch (InitializationError | IOException e) {
			throw new PicklesPreprocessorException("", e);
		}

		cucumber.run(notifier);
	}

	private List<File> getFeatureTemplateFiles() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ResourceLoader resourceLoader = new MultiLoader(classLoader);

		Set<File> files = new HashSet<>();
		for (String featurePath : getFeaturePaths()) {
			for (Resource resource : resourceLoader.resources(featurePath, "featuretemplate")) {
				files.add(new File(resource.getAbsolutePath()));
			}
		}

		return files.stream().collect(Collectors.toList());
	}

	private List<String> getFeaturePaths() {
		RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
		RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
		return runtimeOptions.getFeaturePaths();
	}
}
