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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.Resource;
import cucumber.runtime.io.ResourceLoader;
import io.pickles.preprocessor.Preprocessor;
import io.pickles.preprocessor.PreprocessorException;
import io.pickles.reporting.ReportingStore;
import io.pickles.steps.DelayedVerificationStore;

public class PicklesCucumberRunner extends Runner {

	private static final Logger LOGGER = LoggerFactory.getLogger(PicklesCucumberRunner.class);

	private Class<?> clazz;
	private DelayedVerificationStore delayedVerificationStore;
	private ReportingStore reportingStore;

	private Cucumber cucumber;

	public PicklesCucumberRunner(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setDelayedVerificationStore(DelayedVerificationStore delayedVerificationstore) {
		delayedVerificationStore = delayedVerificationstore;
	}

	public void setReportingStore(ReportingStore reportingStore) {
		this.reportingStore = reportingStore;
	}

	@Override
	public Description getDescription() {
		return cucumber.getDescription();
	}

	@Override
	public void run(RunNotifier notifier) {

		LOGGER.info("Starting preprocessing of feature templates...");
		long startTime = System.currentTimeMillis();

		Preprocessor preprocessor = new Preprocessor();
		preprocessor.setDelayedVerificationStore(delayedVerificationStore);
		preprocessor.setReportingStore(reportingStore);
		if (getRuntimeOptions().isDryRun()) {
			preprocessor.setDryRun();
		}

		for (File featureTemplate : getFeatureTemplateFiles()) {
			preprocessor.process(featureTemplate);
		}

		LOGGER.info("Done in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds.");

		try {
			cucumber = new Cucumber(clazz);
		} catch (InitializationError | IOException e) {
			throw new PreprocessorException("", e);
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
		RuntimeOptions runtimeOptions = getRuntimeOptions();
		return runtimeOptions.getFeaturePaths();
	}

	private RuntimeOptions getRuntimeOptions() {
		RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
		RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
		return runtimeOptions;
	}
}
