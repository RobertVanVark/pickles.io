package nl.devon.pickles.plugins.stubsteps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StubSteps {

	@Given("^a (.*) account$")
	public void anyGiven(String accountType) {

	}

	@When("^I transfer .*")
	public void anyWhen() {

	}

	@Then("^the status is updated$")
	public void theStatusIsUpdated() {

	}

	@Then("^the (.*) account is .*$")
	public void accountIsUpdated(String accountType) {

	}
}
