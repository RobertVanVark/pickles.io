package nl.devon.pickles.preprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Range;

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

	private static final String INITIATION_TAG = "@PicklesInitiation";
	private static final String VERIFICATION_TAG = "@PicklesVerification";

	private FeatureTemplate originalFeature;
	private FeatureTemplate transformedFeature;
	private DelayedVerificationStore store;

	public TemplateTransformer(FeatureTemplate featureTemplate, DelayedVerificationStore store) {
		originalFeature = featureTemplate;
		this.store = store;
	}

	public FeatureTemplate doIt() {
		transformedFeature = new FeatureTemplate();
		transformedFeature.setFeature(originalFeature.getFeature());

		return transformScenarios();
	}

	private FeatureTemplate transformScenarios() {
		for (ScenarioTemplate originalScenario : originalFeature.getScenarios()) {

			List<Range<Integer>> subscenarioRanges = findSubScenarios(originalScenario);
			createInitiationScenario(originalScenario, subscenarioRanges.get(0));

			subscenarioRanges.remove(0);
			for (Range<Integer> range : subscenarioRanges) {
				List<DelayedVerification> verifications = store.readAllForChecksum("dummy checksum");
				for (DelayedVerification verification : verifications) {
					createVerificationScenario(originalScenario, range, verification);
				}
			}
		}

		return transformedFeature;
	}

	private List<Range<Integer>> findSubScenarios(ScenarioTemplate originalScenario) {
		List<Range<Integer>> ranges = new ArrayList<>();
		List<Step> steps = originalScenario.getSteps();
		Integer start = 0;
		for (int i = 0; i < steps.size(); i++) {
			if (isThenAfter(steps.get(i))) {
				ranges.add(Range.closed(start, i));
				start = i;
			}
		}
		ranges.add(Range.closed(start, steps.size() - 1));

		return ranges;
	}

	private boolean isThenAfter(Step step) {
		return "Then ".equals(step.getKeyword()) && step.getName().startsWith("after ");
	}

	private void createInitiationScenario(ScenarioTemplate originalScenario, Range<Integer> range) {
		ScenarioTemplate transformedScenario = new ScenarioTemplate();
		transformedScenario.setSCenario(originalScenario.getScenario());
		transformedScenario.addTag(INITIATION_TAG);

		List<Step> originalSteps = originalScenario.getSteps();
		for (int i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
			transformedScenario.addStep(originalSteps.get(i));
		}

		transformedFeature.addScenario(transformedScenario);
	}

	private void createVerificationScenario(ScenarioTemplate originalScenario, Range<Integer> range,
			DelayedVerification verification) {
		ScenarioTemplate transformedScenario = verificationScenarioFrom(originalScenario, verification);

		Step originalThenAfterStep = originalScenario.getSteps().get(range.lowerEndpoint());
		transformedScenario.addStep(verificationGivenStep(verification));
		transformedScenario.addStep(thenStepFrom(originalThenAfterStep));

		for (int i = range.lowerEndpoint() + 1; i <= range.upperEndpoint(); i++) {
			transformedScenario.addStep(originalScenario.getSteps().get(i));
		}

		transformedFeature.addScenario(transformedScenario);
	}

	private ScenarioTemplate verificationScenarioFrom(ScenarioTemplate originalScenario,
			DelayedVerification verification) {
		Scenario scenario = originalScenario.getScenario();
		List<Comment> comments = Collections.emptyList();
		List<Tag> tags = new ArrayList<Tag>(scenario.getTags());
		String keyword = scenario.getKeyword();
		String name = scenario.getName() + " (dvId=" + verification.getId() + ")";
		String description = "";
		Integer line = scenario.getLine();
		String id = scenario.getId();

		Scenario copy = new Scenario(comments, tags, keyword, name, description, line, id);

		ScenarioTemplate transformedScenario = new ScenarioTemplate();
		transformedScenario.setSCenario(copy);
		transformedScenario.addTag(VERIFICATION_TAG);

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
