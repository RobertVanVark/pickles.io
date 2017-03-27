package io.pickles.preprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.google.common.collect.Range;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;
import io.pickles.steps.DelayedVerification;
import io.pickles.steps.DelayedVerificationStore;
import io.pickles.steps.delays.DelayFactory;

/*
 * MethodObject pattern as we're constantly passing around templateSCenario, templateFeature, etc.
 */

public class TemplateTransformer {

	private static final String INITIATION_TAG = "@PicklesInitiation";
	private static final String VERIFICATION_TAG = "@PicklesVerification";

	private FeatureModel originalFeature;
	private FeatureModel transformedFeature;
	private DelayedVerificationStore store;
	private boolean isDryRun;

	public TemplateTransformer(FeatureModel featureTemplate, DelayedVerificationStore store, boolean isDryRun) {
		originalFeature = featureTemplate;
		this.store = store;
		this.isDryRun = isDryRun;
	}

	public FeatureModel doIt() {
		transformedFeature = new FeatureModel();
		transformedFeature.setFeature(originalFeature.getFeature());

		return transformScenarios();
	}

	public FeatureModel doIt(String splittedInitiationTimeExpression) {
		for (ScenarioModel originalScenario : originalFeature.getScenarios()) {
			for (int i = 1; i < originalScenario.getSteps().size(); i++) {
				StepModel step = originalScenario.getStep(i - 1);
				StepModel followUp = originalScenario.getStep(i);
				if ("When ".equals(step.getKeyword()) && "Then ".equals(followUp.getKeyword())
						&& !followUp.getName().startsWith("after")) {

					String name = "after " + splittedInitiationTimeExpression + " " + followUp.getName();
					Step thenAfter = new Step(followUp.getComments(), followUp.getKeyword(), name, followUp.getLine(),
							followUp.getRows(), null);
					followUp.setStep(thenAfter);
					originalScenario.getSteps().set(i, followUp);
				}
			}
		}
		return doIt();
	}

	private FeatureModel transformScenarios() {
		for (ScenarioModel originalScenario : originalFeature.getScenarios()) {

			List<Range<Integer>> subscenarioRanges = findSubScenarios(originalScenario);
			String checksum = checksum(originalScenario, subscenarioRanges.get(0).upperEndpoint());
			createInitiationScenario(originalScenario, subscenarioRanges.get(0), checksum);

			subscenarioRanges.remove(0);
			for (Range<Integer> range : subscenarioRanges) {
				List<DelayedVerification> verifications = getDelayedVerificationsToVerify(checksum);
				for (DelayedVerification verification : verifications) {
					checksum = checksum(originalScenario, range.upperEndpoint());
					createVerificationScenario(originalScenario, range, verification, checksum);
				}
			}
		}

		return transformedFeature;
	}

	private List<DelayedVerification> getDelayedVerificationsToVerify(String checksum) {
		if (isDryRun) {
			return Arrays.asList(new DelayedVerification("dry-run-id", DateTime.now(), checksum, "dry run featureUri"));
		}
		return store.readAllToVerify(checksum);
	}

	private List<Range<Integer>> findSubScenarios(ScenarioModel originalScenario) {
		List<Range<Integer>> ranges = new ArrayList<>();
		List<StepModel> steps = originalScenario.getSteps();
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

	private boolean isThenAfter(StepModel step) {
		return "Then ".equals(step.getKeyword()) && step.getName().startsWith("after ");
	}

	private void createInitiationScenario(ScenarioModel originalScenario, Range<Integer> range, String checksum) {
		ScenarioModel transformedScenario = new ScenarioModel(originalScenario.getScenario());
		transformedScenario.addTag(INITIATION_TAG);

		for (int i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
			StepModel step = originalScenario.getStep(i);
			if (isThenAfter(step)) {
				transformedScenario.addStep(thenAfterStepFrom(step, checksum));
			} else {
				transformedScenario.addStep(step);
			}
		}

		transformedFeature.addScenario(transformedScenario);
	}

	private void createVerificationScenario(ScenarioModel originalScenario, Range<Integer> range,
			DelayedVerification verification, String checksum) {
		ScenarioModel transformedScenario = verificationScenarioFrom(originalScenario, verification);

		StepModel originalThenAfterStep = originalScenario.getStep(range.lowerEndpoint());
		transformedScenario.addStep(verificationGivenStep(verification));
		transformedScenario.addStep(thenStepFrom(originalThenAfterStep));

		for (int i = range.lowerEndpoint() + 1; i <= range.upperEndpoint(); i++) {
			StepModel step = originalScenario.getStep(i);
			if (isThenAfter(step)) {
				transformedScenario.addStep(thenAfterStepFrom(step, checksum));
			} else {
				transformedScenario.addStep(step);
			}
		}

		transformedFeature.addScenario(transformedScenario);
	}

	private String checksum(ScenarioModel originalScenario, Integer hasThenPosition) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		write(stream, originalFeature.getName());
		for (int i = 0; i <= hasThenPosition; i++) {
			write(stream, originalScenario.getStep(i).getKeyword());
			write(stream, originalScenario.getStep(i).getName());
		}

		byte[] digest;
		try {
			digest = MessageDigest.getInstance("SHA-1").digest(stream.toByteArray());
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

	private ScenarioModel verificationScenarioFrom(ScenarioModel originalScenario, DelayedVerification verification) {
		Scenario scenario = originalScenario.getScenario();
		List<Comment> comments = Collections.emptyList();
		List<Tag> tags = new ArrayList<>(scenario.getTags());
		String keyword = scenario.getKeyword();
		String name = scenario.getName() + " (dvId=" + verification.getId() + ")";
		String description = "";
		Integer line = scenario.getLine();
		String id = scenario.getId();

		Scenario copy = new Scenario(comments, tags, keyword, name, description, line, id);

		ScenarioModel transformedScenario = new ScenarioModel(copy);
		transformedScenario.addTag(VERIFICATION_TAG);

		return transformedScenario;
	}

	private StepModel verificationGivenStep(DelayedVerification verification) {
		List<Comment> comments = Collections.emptyList();
		String keyword = "Given ";
		String name = "Test Execution Context is loaded for dvId=" + verification.getId();
		Integer line = 1;

		Step step = new Step(comments, keyword, name, line, null, null);
		return new StepModel(step);
	}

	private StepModel thenStepFrom(StepModel thenAfter) {
		List<Comment> comments = Collections.emptyList();
		String keyword = "Then ";
		String name = thenAfter.getName();
		name = name.replaceAll(DelayFactory.DELAY_EXPRESSION + " ", "");
		Integer line = 2;
		List<DataTableRow> rows = thenAfter.getRows();

		Step step = new Step(comments, keyword, name, line, rows, null);
		return new StepModel(step);
	}

	private StepModel thenAfterStepFrom(StepModel thenAfter, String checksum) {
		List<Comment> comments = Collections.emptyList();
		String keyword = "Then ";
		Integer line = thenAfter.getLine();
		String dvId = UUID.randomUUID().toString();
		String name = thenAfter.getName() + " (dvChecksum=" + checksum + ", dvId=" + dvId + ", dvFeatureUri="
				+ originalFeature.getTemplateHashKey() + ")";

		Step step = new Step(comments, keyword, name, line, null, null);
		return new StepModel(step);
	}
}
