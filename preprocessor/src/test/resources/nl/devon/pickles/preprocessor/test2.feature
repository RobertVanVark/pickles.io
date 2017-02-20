#Feature comment
Feature: More elaborate banking feature
	Background information

	#Scenario Comment
	@ScenarioTag2a @ScenarioTag2b
	@ScenarioTag3
	Scenario: Transfer extra money with delayed verifications
	
	#Line comment 1
	#Line comment 2
	Given a creditor account
	And a debtor account
	When I transfer EUR 2.00
	Then after 02:00 hr the creditor account is credited with EUR 2.00 (dv-checksum=12345678901234567890123456789012)
	Then billing information is generated
	Then after 1:00 hr reporting information is generated (dv-checksum=01234567890123456789012345678901)
	Then the status is updated
	