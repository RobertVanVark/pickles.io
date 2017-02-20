@FeatureTag
Feature: Simple banking feature
	Background information

	@ScenarioTag1
	Scenario: Transfer money
	
	Given a creditor account
	And a debtor account
	When I transfer EUR 2.00
	Then the creditor account is credited with EUR 2.00
	And the debtor account is debited with EUR 2.00
	Then billing information is generated
	Then reporting information is generated
	Then the status is updated