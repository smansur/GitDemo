Feature: Verifying Builder And Vendor Messaging APIs

@Messages @Sanity
Scenario: Vendor Login to Drees team
	Given Login Payload
	When User calls "DreesTeamLoginAPI" with "POST" request
	Then API call is successful with status code 200
	And Response validation and capture authtoken
	
@Messages @Sanity	 
Scenario: Fetching Builder ID to filter the activities
	Given Fetch builder payload
	When User calls "FetchingBuilderNameAPI" with "GET" request 
	Then API call is successful with status code 200
	And Response validation and capture builderID
	
@Messages @Sanity
Scenario: Fetching the activity ID to send a message
	Given Fetch activity payload
	When User calls "FetchingActivityIdAPI" with "GET" request
	Then API call is successful with status code 200
	And Response validation and capture activityID
	
@Messages @Sanity	
Scenario: Vendor Sending message to builder
	Given Vendor Sending message Payload
	When User calls "VendorSendingMessageAPI" with "POST" request
	Then API call is successful with status code 200
	And verify the response "body" matches to "true"
	
@Messages @Sanity	
Scenario: Verifying Drees Build Login
	Given Drees Login Payload
	When User calls "DreesBuildAuthenticateAPI" with "GET" request
	Then API call is successful with status code 200
	And verify the response and capture userkey
@Messages @Sanity	
Scenario: Drees Build Fetching Unread Message Count
	Given DBuild Unread Messages Count Payload
	When User calls "DreesBuildUnreadMessageCountAPI" with "GET" request
	Then API call is successful with status code 200
	And verify the response and capture unread message count
	
@Messages @Sanity
Scenario: Fetching The Read Messages To Find The Unread Message Thread ID
	Given Unread Messages ThreadID Payload
	When User calls "DreesBuildReadMessagesAPI" with "GET" request
	Then API call is successful with status code 200
	And verify the response and capture unread message thread ID

@Messages @Sanity
Scenario: Builder Marking the vendor message as read
	Given Builder Message Reading Payload
	When User calls "DreesBuildMessageMarkAsReadAPI" with "POST" request
	Then API call is successful with status code 200
	And verify the response status matches to true
	
