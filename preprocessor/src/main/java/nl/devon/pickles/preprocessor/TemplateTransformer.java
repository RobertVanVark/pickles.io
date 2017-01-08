package nl.devon.pickles.preprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
			String checksum = checksum(originalScenario, subscenarioRanges.get(0).upperEndpoint());
			createInitiationScenario(originalScenario, subscenarioRanges.get(0), checksum);

			subscenarioRanges.remove(0);
			for (Range<Integer> range : subscenarioRanges) {
				List<DelayedVerification> verifications = store.readAllForChecksum(checksum);
				for (DelayedVerification verification : verifications) {
					checksum = checksum(originalScenario, range.upperEndpoint());
					createVerificationScenario(originalScenario, range, verification, checksum);
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

	private void createInitiationScenario(ScenarioTemplate originalScenario, Range<Integer> range, String checksum) {
		ScenarioTemplate transformedScenario = new ScenarioTemplate();
		transformedScenario.setSCenario(originalScenario.getScenario());
		transformedScenario.addTag(INITIATION_TAG);

		for (int i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
			Step step = originalScenario.getStep(i);
			if (isThenAfter(step)) {
				transformedScenario.addStep(thenAfterStepFrom(step, checksum));
			} else {
				transformedScenario.addStep(step);
			}
		}

		transformedFeature.addScenario(transformedScenario);
	}

	private void createVerificationScenario(ScenarioTemplate originalScenario, Range<Integer> range,
			DelayedVerification verification, String checksum) {
		ScenarioTemplate transformedScenario = verificationScenarioFrom(originalScenario, verification);

		Step originalThenAfterStep = originalScenario.getStep(range.lowerEndpoint());
		transformedScenario.addStep(verificationGivenStep(verification));
		transformedScenario.addStep(thenStepFrom(originalThenAfterStep));

		for (int i = range.lowerEndpoint() + 1; i <= range.upperEndpoint(); i++) {
			Step step = originalScenario.getStep(i);
			if (isThenAfter(step)) {
				transformedScenario.addStep(thenAfterStepFrom(step, checksum));
			} else {
				transformedScenario.addStep(step);
			}
		}

		transformedFeature.addScenario(transformedScenario);
	}

	private String checksum(ScenarioTemplate originalScenario, Integer hasThenPosition) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		write(stream, originalFeature.getName());
		for (int i = 0; i <= hasThenPosition; i++) {
			write(stream, originalScenario.getStep(i).getKeyword());
			write(stream, originalScenario.getStep(i).getName());
		}

		byte[] digest;
		try {
			digest = MessageDigest.getInstance("SHA-256").digest(stream.toByteArray());
		} catch (NoSuchAlgorithmException ex) {
			throw new TemplateTransformerException(ex.getMessage(), ex);
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
		String name = thenAfter.getName();
		name = name.replaceAll(TimeOffsetDelay.EXPRESSION + " ", "");
		Integer line = 2;

		return new Step(comments, keyword, name, line, null, null);
	}

	private Step thenAfterStepFrom(Step thenAfter, String checksum) {
		List<Comment> comments = Collections.emptyList();
		String keyword = "Then ";
		Integer line = thenAfter.getLine();
		String name = thenAfter.getName() + " (dvChecksum=" + checksum + ")";

		return new Step(comments, keyword, name, line, null, null);
	}
}
