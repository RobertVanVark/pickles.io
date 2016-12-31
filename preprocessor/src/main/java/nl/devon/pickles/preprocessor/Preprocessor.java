package nl.devon.pickles.preprocessor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import gherkin.parser.Parser;
import gherkin.util.FixJava;
import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.steps.DelayedVerificationStore;

public class Preprocessor {

	private DelayedVerificationStore store;

	public void setDelayedVerificationStore(DelayedVerificationStore store) {
		this.store = store;
	}

	public FeatureTemplate process(List<String> lines) {
		String featureUri = "";
		String gherkin = String.join("\n", lines);
		FeatureTemplate featureTemplate = parseGherkin(featureUri, gherkin);
		return new TransformMethodObject(featureTemplate, store).doIt();
	}

	public FeatureTemplate parse(String path) {
		String gherkin;
		try {
			gherkin = FixJava.readReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException | RuntimeException ex) {
			throw new PicklesPreprocessorException("Could not read feature template file: " + path, ex);
		}

		return parseGherkin(path, gherkin);
	}

	private FeatureTemplate parseGherkin(String featureUri, String gherkin) {
		StringBuilder output = new StringBuilder();
		FeatureTemplate featureTemplate = new FeatureTemplate();
		TemplateFormatter formatter = new TemplateFormatter(output, featureTemplate);
		Parser parser = new Parser(formatter);
		parser.parse(gherkin, featureUri, 0);

		return featureTemplate;
	}
}
