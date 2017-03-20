package io.pickles.preprocessor;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import gherkin.parser.Parser;
import gherkin.util.FixJava;
import io.pickles.model.FeatureModel;

public class TemplateParser {

	public FeatureModel parse(String uri, List<String> lines) {
		String gherkin = String.join("\n", lines);
		String hashKey = checksum(lines);
		FeatureModel model = parseGherkin(uri, gherkin);
		model.setTemplateHashKey(hashKey);
		return model;
	}

	public FeatureModel parse(String path) {
		String gherkin;
		try (InputStreamReader stream = new InputStreamReader(new FileInputStream(path), "UTF-8")) {
			gherkin = FixJava.readReader(stream);
		} catch (RuntimeException | IOException ex) {
			throw new PreprocessorException("Could not read feature template file: " + path, ex);
		}

		return parseGherkin(path, gherkin);
	}

	public FeatureModel parseGherkin(String featureUri, String gherkin) {
		TemplateFormatter formatter = new TemplateFormatter(new StringBuilder());
		Parser parser = new Parser(formatter);
		parser.parse(gherkin, featureUri, 0);

		return formatter.getFeatures().get(0);
	}

	private String checksum(List<String> lines) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		for (String line : lines) {
			write(stream, line);
		}

		byte[] digest;
		try {
			digest = MessageDigest.getInstance("SHA-1").digest(stream.toByteArray());
		} catch (NoSuchAlgorithmException ex) {
			throw new PreprocessorException(ex.getMessage(), ex);
		}

		return new BigInteger(1, digest).toString();
	}

	private void write(ByteArrayOutputStream stream, String value) {
		try {
			stream.write(value.getBytes("UTF-8"));
		} catch (IOException ex) {
			throw new TemplateTransformerException("Error calculating checksum for " + value, ex);
		}
	}

}
