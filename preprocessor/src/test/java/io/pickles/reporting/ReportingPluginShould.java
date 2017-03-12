package io.pickles.reporting;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.reporting.stubs.StubReportingStore;

public class ReportingPluginShould {

	@BeforeClass
	public static void initializeStubReportingStore() throws IOException {
		StubReportingStore.initialize();
		runPlugin();
	}

	private static void runPlugin() throws IOException {
		ArrayList<String> commandlineParams = new ArrayList<>();
		commandlineParams.addAll(Arrays.asList("-p", "io.pickles.reporting.stubs.LocalReportingPlugin"));
		commandlineParams.addAll(Arrays.asList("-g", "classpath:io.pickles"));
		commandlineParams.addAll(Arrays.asList("classpath:io/pickles/reporting"));

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		ResourceLoader resourceLoader = new MultiLoader(contextClassLoader);
		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, contextClassLoader);
		RuntimeOptions runtimeOptions = new RuntimeOptions(commandlineParams);
		Runtime runtime = new Runtime(resourceLoader, classFinder, contextClassLoader, runtimeOptions);
		runtime.run();
	}

	@Test
	public void createAndUpdateTestRuns() throws IOException {
		assertThat(StubReportingStore.createdTestRuns(), hasSize(1));
		assertThat(StubReportingStore.updatedTestRuns(), hasSize(1));
	}

	@Test
	public void createFeatures() throws IOException {
		assertThat(StubReportingStore.createdFeatures(), hasSize(2));
	}

	@Test
	public void createScenarios() throws IOException {
		assertThat(StubReportingStore.createdScenarios(), hasSize(4));
	}

	@Test
	public void createSteps() throws IOException {
		assertThat(StubReportingStore.createdSteps(), hasSize(18));
	}

	@Test
	public void takeNextDelayedVerificationsFromThenAfterSteps() throws IOException {
		List<ScenarioModel> scenarios = StubReportingStore.createdScenarios();

		List<ScenarioModel> nonInitiating = scenarios.stream().filter(s -> s.getNextDvId() == null)
				.collect(Collectors.toList());
		assertThat(nonInitiating, hasSize(2));
	}

	@Test
	public void takePreviousDelayedVerificationsFromScenarioNames() throws IOException {
		List<ScenarioModel> scenarios = StubReportingStore.createdScenarios();

		List<ScenarioModel> followup = scenarios.stream().filter(s -> s.getTriggeringDvId() == null)
				.collect(Collectors.toList());
		assertThat(followup, hasSize(2));
	}

	@Test
	public void setStartingAtInAllFeatures() throws IOException {
		List<FeatureModel> features = StubReportingStore.createdFeatures();

		List<FeatureModel> startedAtFeatures = features.stream().filter(f -> f.getStartedAt() != null)
				.collect(Collectors.toList());
		assertThat(startedAtFeatures.size(), equalTo(features.size()));
	}

	@Test
	public void setFinishedAtInAllFeaures() throws IOException {
		List<FeatureModel> features = StubReportingStore.createdFeatures();

		List<FeatureModel> finishedFeatures = features.stream().filter(f -> f.getFinishedAt() != null)
				.collect(Collectors.toList());
		assertThat(finishedFeatures.size(), equalTo(features.size()));
	}

	@Test
	public void setStartingAtInAllScenarios() throws IOException {
		List<ScenarioModel> scenarios = StubReportingStore.createdScenarios();

		List<ScenarioModel> startedScenarios = scenarios.stream().filter(s -> s.getStartedAt() != null)
				.collect(Collectors.toList());
		assertThat(startedScenarios.size(), equalTo(scenarios.size()));
	}

	@Test
	public void setFinishedAtInAllScenarios() throws IOException {
		List<ScenarioModel> scenarios = StubReportingStore.createdScenarios();

		List<ScenarioModel> finishedScenarios = scenarios.stream().filter(s -> s.getFinishedAt() != null)
				.collect(Collectors.toList());
		assertThat(finishedScenarios.size(), equalTo(scenarios.size()));
	}

}
