Feature: MSome standard feature

	Scenario: Transfer extra money with delayed verifications
	
	Given a creditor account
	And a debtor account
	When I transfer EUR 2.00
	Then the creditor account is credited with EUR 2.00 
		| account | amount |
		| IBANNR | EUR 2.00 |
		| IBANNR | GBP 0.99 |
	Then billing information is generated
	Then reporting information is generated
	Then the status is updated
	