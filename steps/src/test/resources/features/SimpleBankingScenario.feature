@FeatureTag
Feature: Simple banking scenario
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

	@ScenarioTag2a @ScenarioTag2b
	@ScenarioTag3
	Scenario Outline: Transfer money outline
	
	#Comment
	Given a creditor account
	And a debtor account
	When I transfer EUR 2.00
	Then after 02:00 hr the creditor account is credited with EUR 2.00
	And the debtor account is debited with <amount>
	Then billing information is generated
	Then after 1:00 hr reporting information is generated
	Then the status is updated
	
	Examples:
	# More Comment
	| amount |
	| EUR 2.00 |
	