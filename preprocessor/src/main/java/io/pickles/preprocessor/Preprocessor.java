package io.pickles.preprocessor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.pickles.model.FeatureModel;
import io.pickles.reporting.ReportingStore;
import io.pickles.steps.DelayedVerificationStore;

public class Preprocessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(Preprocessor.class);

	private boolean isDryRun = false;
	private DelayedVerificationStore store;
	private ReportingStore reportingStore;

	String splittedInitiationExpression = null;

	public void setDelayedVerificationStore(DelayedVerificationStore store) {
		this.store = store;
	}

	public void setReportingStore(ReportingStore store) {
		reportingStore = store;
	}

	public void setSplittedInitiation(String timeExpression) {
		if (timeExpression == null) {
			LOGGER.info("Preprocessing featuretemplates with direct verification");
		} else {
			LOGGER.info("Preprocessing featuretemplates with verifying " + timeExpression + " after initiation");
		}

		splittedInitiationExpression = timeExpression;
	}

	public void setDryRun() {
		isDryRun = true;
	}

	List<String> process(String uri, List<String> lines) {
		FeatureModel original = new TemplateParser().parse(uri, lines);
		storeFeatureTemplate(original, uri, lines);
		FeatureModel transformed;
		if (splittedInitiationExpression == null) {
			LOGGER.info("Preprocessing featuretemplates with direct verification");
			transformed = new TemplateTransformer(original, store, isDryRun).doIt();
		} else {
			LOGGER.info("Preprocessing featuretemplates with initiation separate from verification");
			transformed = new TemplateTransformer(original, store, isDryRun).doIt(splittedInitiationExpression);
		}
		return transformed.toGherkinList();
	}

	public List<String> process(Path path) {
		List<String> templateLines = readFeatureTemplate(path);
		List<String> featureLines = process(path.toString(), templateLines);
		writeFeature(path, featureLines);
		return templateLines;
	}

	private List<String> readFeatureTemplate(Path path) {
		List<String> templateLines;
		try {
			templateLines = Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new PreprocessorException("Could not read feature template file : " + path.toString(), e);
		}
		return templateLines;
	}

	private void writeFeature(Path templatePath, List<String> linesToFile) {

		String pathString = templatePath.toString();
		File featureFile = new File(pathString.substring(0, pathString.lastIndexOf('.')) + ".feature");
		Path featurePath = Paths.get(featureFile.toURI());
		try {
			Files.write(featurePath, linesToFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new PreprocessorException("Could not write feature file : " + featureFile.getAbsolutePath(), e);
		}
	}

	public List<String> process(String path) throws IOException {
		return process(Paths.get(URI.create(path)));
	}

	public List<String> process(File templateFile) {
		return process(templateFile.toPath());
	}

	private void storeFeatureTemplate(FeatureModel template, String uri, List<String> lines) {
		if (!isDryRun) {
			reportingStore.create(template, uri, lines);
		}
	}
}
