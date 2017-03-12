Feature: More elaborate banking feature

	Scenario: Transfer extra money with delayed verifications
	
	Given a creditor account
	And a debtor account
	When I transfer EUR 2.00
	Then after 02:00 hr the creditor account is credited with EUR 2.00 (dvCchecksum=12345678901234567890123456789012, dvId=7878787, dvFeatureUri=io/pickles/preprocessor/reporting.feature)
	
	
	Scenario: Transfer extra money with delayed verifications (dvId=7878787)
	
	Given Test Execution Context is loaded for dvId=7878787
	Then the creditor account is credited with EUR 2.00 
		| account | amount |
		| IBANNR | EUR 2.00 |
		|IBANNR| GBP 0.99|
	Then billing information is generated
	Then after 1:00 hr reporting information is generated (dvChecksum=01234567890123456789012345678901, dvId=112233, dvFeatureUri=io/pickles/preprocessor/reporting.feature)
	
	
	Scenario: Transfer extra money with delayed verifications (dvId=112233)
	
	Given Test Execution Context is loaded for dvId=112233
	Then reporting information is generated
	Then the status is updated
	