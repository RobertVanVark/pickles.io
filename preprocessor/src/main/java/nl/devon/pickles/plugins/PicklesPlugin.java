package nl.devon.pickles.plugins;

import gherkin.formatter.Reporter;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import nl.devon.pickles.preprocessor.TemplateFormatter;
import nl.devon.pickles.preprocessor.model.FeatureModel;
import nl.devon.pickles.preprocessor.model.StepModel;

public class PicklesPlugin extends TemplateFormatter implements Reporter {

	public PicklesPlugin(Appendable out) {
		super(out, new FeatureModel());
	}

	@Override
	public void before(Match match, Result result) {
		// intentionally left blank
		log("before");
	}

	@Override
	public void result(Result result) {
		log("result : " + result.getStatus());
		StepModel stepWithoutResult = lastFeature().getFirstStepWithoutResult();
		stepWithoutResult.setResult(result);
	}

	@Override
	public void after(Match match, Result result) {
		// intentionally left blank
		log("after");
	}

	@Override
	public void match(Match match) {
		log("match " + match.toString());
		StepModel unmatchedStep = lastFeature().getFirstUnmatchedStep();
		unmatchedStep.setMatch(match);
	}

	@Override
	public void embedding(String mimeType, byte[] data) {
		// intentionally left blank
		log("embedding");
	}

	@Override
	public void write(String text) {
		// intentionally left blank
		log("write");
	}

	public void log(String msg) {
		// out.println("*** PicklesPlugin : " + msg);
	}
}
