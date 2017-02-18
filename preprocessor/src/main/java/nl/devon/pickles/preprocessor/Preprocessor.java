package nl.devon.pickles.preprocessor;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import nl.devon.pickles.preprocessor.model.FeatureModel;
import nl.devon.pickles.steps.DelayedVerificationStore;

public class Preprocessor {

	private DelayedVerificationStore store;

	public void setDelayedVerificationStore(DelayedVerificationStore store) {
		this.store = store;
	}

	public List<String> process(List<String> lines) {
		FeatureModel original = new TemplateParser().parse(lines);
		FeatureModel transformed = new TemplateTransformer(original, store).doIt();
		return transformed.toGherkinList();
	}

	public List<String> process(String path) throws IOException {
		List<String> templateLines = Files.readAllLines(Paths.get(URI.create(path)));
		return process(templateLines);
	}
}
