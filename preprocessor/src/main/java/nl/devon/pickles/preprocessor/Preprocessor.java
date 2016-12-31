package nl.devon.pickles.preprocessor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.parser.Parser;
import gherkin.util.FixJava;
import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.preprocessor.model.ScenarioTemplate;
import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.DelayedVerificationStore;
import nl.devon.pickles.steps.TimeOffsetDelay;

public class Preprocessor {

	private DelayedVerificationStore store;

	public void setDelayedVerificationStore(DelayedVerificationStore store) {
		this.store = store;
	}

	public FeatureTemplate process(List<String> lines) {
		String featureUri = "";
		String gherkin = String.join("\n", lines);
		FeatureTemplate featureTemplate = parseGherkin(featureUri, gherkin);
		return transform(featureTemplate);
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

	private FeatureTemplate transform(FeatureTemplate originalFeature) {
		FeatureTemplate transformedFeature = new FeatureTemplate();
		transformedFeature.setFeature(originalFeature.getFeature());

		return transformScenarios(originalFeature, transformedFeature);
	}

	private FeatureTemplate transformScenarios(FeatureTemplate originalFeature, FeatureTemplate transformedFeature) {
		for (ScenarioTemplate originalScenario : originalFeature.getScenarios()) {

			ScenarioTemplate transformedScenario = new ScenarioTemplate();
			transformedScenario.setSCenario(originalScenario.getScenario());
			transformedFeature.addScenario(transformedScenario);

			transformSteps(originalScenario, transformedFeature);
		}

		return transformedFeature;
	}

	private void transformSteps(ScenarioTemplate originalScenario, FeatureTemplate transformedFeature) {
		for (Step step : originalScenario.getSteps()) {
			transformedFeature.getCurrentScenario().addStep(step);
			if (isThenAfter(step)) {
				List<DelayedVerification> verifications = store.readAllForChecksum("dummy checksum");
				for (DelayedVerification verification : verifications) {
					transformedFeature.addScenario(verificationScenarioFrom(originalScenario, verification));
					transformedFeature.getCurrentScenario().addStep(verificationGivenStep(verification));
					transformedFeature.getCurrentScenario().addStep(thenStepFrom(step));
				}
			}
		}
	}

	private boolean isThenAfter(Step step) {
		return "Then ".equals(step.getKeyword()) && step.getName().startsWith("after ");
	}

	private ScenarioTemplate verificationScenarioFrom(ScenarioTemplate originalScenario,
			DelayedVerification verification) {
		Scenario scenario = originalScenario.getScenario();
		String name = scenario.getName() + " (dvId=" + verification.getId() + ")";
		Scenario copy = new Scenario(Collections.emptyList(), null, scenario.getKeyword(), name, "", scenario.getLine(),
				scenario.getId());

		ScenarioTemplate transformedScenario = new ScenarioTemplate();
		transformedScenario.setSCenario(copy);
		return transformedScenario;
	}

	private Step verificationGivenStep(DelayedVerification verification) {
		List<Comment> comments = Collections.emptyList();
		String keyword = "Given ";
		String name = "Test Execution Context is loaded for dvId=" + verification.getId();
		Integer line = 1;

		return new Step(comments, keyword, name, line, null, null);
	}

	private Step thenStepFrom(Step thenAfter) {
		List<Comment> comments = Collections.emptyList();
		String keyword = "Then ";
		String name = thenAfter.getName().substring("after ".length());
		name = name.replaceAll(TimeOffsetDelay.EXPRESSION + " ", "");
		Integer line = 2;

		return new Step(comments, keyword, name, line, null, null);
	}
}
