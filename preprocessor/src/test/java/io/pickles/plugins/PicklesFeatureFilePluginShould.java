package io.pickles.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;

public class PicklesFeatureFilePluginShould {

	@Test
	public void runCucumberWithPlugin() throws IOException {
		ArrayList<String> commandlineParams = new ArrayList<>();
		commandlineParams.addAll(Arrays.asList("-p", "io.pickles.plugins.PicklesFeatureFilePlugin"));
		commandlineParams.addAll(Arrays.asList("-g", "classpath:io.pickles"));
		commandlineParams.addAll(Arrays.asList("classpath:io/pickles/preprocessor"));

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		ResourceLoader resourceLoader = new MultiLoader(contextClassLoader);
		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, contextClassLoader);
		RuntimeOptions runtimeOptions = new RuntimeOptions(commandlineParams);
		Runtime runtime = new Runtime(resourceLoader, classFinder, contextClassLoader, runtimeOptions);
		runtime.run();
	}
}
