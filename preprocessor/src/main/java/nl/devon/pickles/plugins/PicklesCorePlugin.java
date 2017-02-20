package nl.devon.pickles.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gherkin.formatter.Reporter;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import nl.devon.pickles.preprocessor.TemplateFormatter;
import nl.devon.pickles.preprocessor.model.StepModel;

public abstract class PicklesCorePlugin extends TemplateFormatter implements Reporter {

	private static final Logger LOGGER = LoggerFactory.getLogger("nl.devon.pickles.plugin.PicklesCorePlugin");

	public PicklesCorePlugin(Appendable out) {
		super(out);
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
		log("match ");
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

	private void log(String msg) {
		LOGGER.info(msg);
	}
}