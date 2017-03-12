package io.pickles.reporting;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import io.pickles.reporting.stubs.StubReportingStore;

public class ReportingPluginShould {

	@Before
	public void clearStubReportingStore() {
		StubReportingStore.initialize();
	}

	@Test
	public void runCucumberWithPlugin() throws IOException {
		ArrayList<String> commandlineParams = new ArrayList<>();
		commandlineParams.addAll(Arrays.asList("-p", "io.pickles.reporting.stubs.LocalReportingPlugin"));
		commandlineParams.addAll(Arrays.asList("-g", "classpath:io.pickles"));
		commandlineParams.addAll(Arrays.asList("classpath:io/pickles/preprocessor/test2.feature"));

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		ResourceLoader resourceLoader = new MultiLoader(contextClassLoader);
		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, contextClassLoader);
		RuntimeOptions runtimeOptions = new RuntimeOptions(commandlineParams);
		Runtime runtime = new Runtime(resourceLoader, classFinder, contextClassLoader, runtimeOptions);
		runtime.run();

		assertThat(StubReportingStore.createdTestRuns(), hasSize(1));
		assertThat(StubReportingStore.updatedTestRuns(), hasSize(1));
		assertThat(StubReportingStore.createdFeatures(), hasSize(1));
	}
}
