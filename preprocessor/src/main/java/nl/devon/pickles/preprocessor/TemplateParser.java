package nl.devon.pickles.preprocessor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import gherkin.parser.Parser;
import gherkin.util.FixJava;
import nl.devon.pickles.preprocessor.model.FeatureModel;

public class TemplateParser {

	public FeatureModel parse(List<String> lines) {
		String featureUri = "";
		String gherkin = String.join("\n", lines);
		return parseGherkin(featureUri, gherkin);
	}

	public FeatureModel parse(String path) {
		String gherkin;
		try {
			gherkin = FixJava.readReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException | RuntimeException ex) {
			throw new PicklesPreprocessorException("Could not read feature template file: " + path, ex);
		}

		return parseGherkin(path, gherkin);
	}

	private FeatureModel parseGherkin(String featureUri, String gherkin) {
		TemplateFormatter formatter = new TemplateFormatter(new StringBuilder());
		Parser parser = new Parser(formatter);
		parser.parse(gherkin, featureUri, 0);

		return formatter.getFeatures().get(0);
	}
}
