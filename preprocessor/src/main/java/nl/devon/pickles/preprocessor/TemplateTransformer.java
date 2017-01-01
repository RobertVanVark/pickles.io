package nl.devon.pickles.preprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import nl.devon.pickles.preprocessor.model.FeatureTemplate;
import nl.devon.pickles.preprocessor.model.ScenarioTemplate;
import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.DelayedVerificationStore;
import nl.devon.pickles.steps.TimeOffsetDelay;

/*
 * MethodObject pattern as we're constantly passing around templateSCenario, templateFeature, etc.
 */

public class TemplateTransformer {

	private static final String INITIATION_TAG = "@Pickles_Initiation";
	private static final String VERIFICATION_TAG = "@Pickles_Verification";

	private FeatureTemplate originalFeatureTemplate;
	private DelayedVerificationStore store;

	public TemplateTransformer(FeatureTemplate featureTemplate, DelayedVerificationStore store) {
		originalFeatureTemplate = featureTemplate;
		this.store = store;
	}

	public FeatureTemplate doIt() {
		FeatureTemplate transformedFeature = new FeatureTemplate();
		transformedFeature.setFeature(originalFeatureTemplate.getFeature());

		return transformScenarios(originalFeatureTemplate, transformedFeature);
	}

	private FeatureTemplate transformScenarios(FeatureTemplate originalFeature, FeatureTemplate transformedFeature) {
		for (ScenarioTemplate originalScenario : originalFeature.getScenarios()) {

			ScenarioTemplate transformedScenario = new ScenarioTemplate();
			transformedScenario.setSCenario(originalScenario.getScenario());
			transformedFeature.addScenario(transformedScenario);

			transformSteps(originalScenario, transformedFeature);

			addInitiationTag(transformedScenario);
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

	private void addInitiationTag(ScenarioTemplate transformedScenario) {
		Tag initiationTag = new Tag(INITIATION_TAG, 0);
		transformedScenario.getScenario().getTags().add(initiationTag);
	}

	private boolean isThenAfter(Step step) {
		return "Then ".equals(step.getKeyword()) && step.getName().startsWith("after ");
	}

	private ScenarioTemplate verificationScenarioFrom(ScenarioTemplate originalScenario,
			DelayedVerification verification) {
		Scenario scenario = originalScenario.getScenario();
		List<Comment> comments = Collections.emptyList();
		List<Tag> tags = new ArrayList<Tag>(scenario.getTags());
		tags.add(new Tag(VERIFICATION_TAG, 0));
		String keyword = scenario.getKeyword();
		String name = scenario.getName() + " (dvId=" + verification.getId() + ")";
		String description = "";
		Integer line = scenario.getLine();
		String id = scenario.getId();

		Scenario copy = new Scenario(comments, tags, keyword, name, description, line, id);

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
