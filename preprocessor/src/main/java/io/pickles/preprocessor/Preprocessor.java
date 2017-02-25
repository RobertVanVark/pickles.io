package io.pickles.preprocessor;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import io.pickles.preprocessor.model.FeatureModel;
import io.pickles.steps.DelayedVerificationStore;

public class Preprocessor {

	private DelayedVerificationStore store;

	public void setDelayedVerificationStore(DelayedVerificationStore store) {
		this.store = store;
	}

	public List<String> process(String uri, List<String> lines) {
		FeatureModel original = new TemplateParser().parse(uri, lines);
		FeatureModel transformed = new TemplateTransformer(original, store).doIt();
		return transformed.toGherkinList();
	}

	public List<String> process(String path) throws IOException {
		List<String> templateLines = Files.readAllLines(Paths.get(URI.create(path)));
		return process(path, templateLines);
	}
}
