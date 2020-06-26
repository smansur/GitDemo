package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static io.restassured.RestAssured.*;
import static org.testng.Assert.assertEquals;
import java.io.IOException;
import java.util.ArrayList;

import excelDriven.ExcelDrivenForDrees;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import frameworkUtilities.JsonDataParsing;
import frameworkUtilities.RaiseJiraIssue;
import frameworkUtilities.RequestSpec_Prop_Encryption_JsonFetchData;



public class StepDefinitionForDrees extends RequestSpec_Prop_Encryption_JsonFetchData {
	RequestSpecification reqSpec;
	Response response;
	static String dteamAuthToken;
	static String dbuildLoginToken;
	static String dbuildAuthToken;
	static String vendorID;
	static String builderID;
	static int threadID;
	int unreadMessageCount;
	JsonDataParsing jsonData = new JsonDataParsing();
	ExcelDrivenForDrees excelData = new ExcelDrivenForDrees();
	RaiseJiraIssue jira= new RaiseJiraIssue();
	@Given("Login Payload")
	public void login_Payload() throws IOException {
		reqSpec = given().log().all().relaxedHTTPSValidation().spec(reqSpecBuilder()).body(jsonData.loginJson());
	}
	@When("User calls {string} with {string} request")
	public void user_calls_with_request(String apiName, String method) throws IOException {
		if(method.equalsIgnoreCase("POST")) {
		response = reqSpec.when().log().all().post(readProperties(apiName)).then().spec(resSpecBuilder()).extract().response();
		System.out.println(method);
		}
		if(method.equalsIgnoreCase("GET")) {
			response = reqSpec.when().log().all().get(readProperties(apiName)).then().spec(resSpecBuilder()).extract().response();
			System.out.println(method);
		}
		if(method.equalsIgnoreCase("PUT")) {
			response = reqSpec.when().log().all().put(readProperties(apiName)).then().spec(resSpecBuilder()).extract().response();
			System.out.println(method);
		}
		if(method.equalsIgnoreCase("DELETE")) {
			response = reqSpec.when().log().all().delete(readProperties(apiName)).then().spec(resSpecBuilder()).extract().response();
			System.out.println(method);
		}
	}
	@Then("API call is successful with status code {int}")
	public void api_call_is_successful_with_status_code(Integer int1) {
		assertEquals(response.getStatusCode(),200);
	}
	@Then("Response validation and capture authtoken")
	public void response_validation_and_capture_authtoken() {
		dteamAuthToken = jsonExtractVairable(response, "cognitoInitiateAuthResult.authenticationResult.accessToken");
		vendorID = jsonExtractVairable(response, "dbv-id");
		System.out.println("Printing Vendor user Auth token "+dteamAuthToken);
	}
	
	//Fetching Builder ID
	@Given("Fetch builder payload")
	public void fetch_payload() throws IOException {
		reqSpec = given().log().all().relaxedHTTPSValidation().spec(reqSpecBuilder())
				  .accept(ContentType.JSON)
				  .header("authorization", dteamAuthToken)
				  .queryParam("activityStatus", "open")
				  .queryParam("limit", "0")
				  .queryParam("offset", "0")
				  .queryParam("selectedVendorId", vendorID);
	}


	@Then("Response validation and capture builderID")
	public void response_validation_and_capture_builderID() {
		JsonPath builderInfo = new JsonPath(response.asString());
		int count = builderInfo.getInt("response.body.size()");
		for (int i=0; i<count ; i++) {
			if(builderInfo.getString("response.body["+i+"].empFName").equalsIgnoreCase("Ashrith")) {
				builderID = builderInfo.getString(("response.body["+i+"].empId"));
				
				System.out.println("Builder is for Ashrith "+builderID);
				break;
			}
		}

	}
	//Selecting activity to send message on
	@Given("Fetch activity payload")
	public void fetch_activity_payload() throws IOException {
		reqSpec = given().log().all().relaxedHTTPSValidation().spec(reqSpecBuilder())
				  .accept(ContentType.JSON)
				  .header("authorization", dteamAuthToken)
				  .queryParam("activityStatus", "open")
				  .queryParam("limit", "150")
				  .queryParam("offset", "0")
				  .queryParam("sortField", "startdate")
				  .queryParam("sortOrder", "asc")
				  .queryParam("builderId", builderID)
				  .queryParam("selectedVendorId", vendorID);
	}

	@Then("Response validation and capture activityID")
	public void response_validation_and_capture_activityID() throws Exception {
		ArrayList array = new ArrayList();
		JsonPath js = new JsonPath(response.asString());
		int activitiesCount = js.get("response.body.size()");
		if(activitiesCount != 0) {
		array.add(jsonExtractVairableInt(response, "response.body[0].activityToJobId"));
		array.add(jsonExtractVairable(response, "response.body[0].activityName"));
		array.add(jsonExtractVairable(response, "response.body[0].jobNumber"));
		array.add(jsonExtractVairableInt(response, "response.body[0].jobId"));
		array.add(builderID);
		
		excelData.dataWrite(array, 0);
		
		}else {
			System.out.println("=============No Activities found to send messages=============");
		}
	
	}
	//Vendor Sending Message to builder 
	@Given("Vendor Sending message Payload")
	public void vendor_Sending_message_Payload() throws IOException {
		reqSpec = given().log().all().relaxedHTTPSValidation().spec(reqSpecBuilder())
				  .accept(ContentType.JSON)
				  .header("authorization", dteamAuthToken)
				  .queryParam("selectedVendorId", vendorID)
				  .body(jsonData.vendorMsgJson());
	}
	@Then("verify the response {string} matches to {string}")
	public void verify_the_response_matches_to(String string, String string2) {
	   assertEquals(jsonExtractVairableBoolean(response, "response.body"), Boolean.parseBoolean(string2));
	}
	//Drees Build Authentication
	@Given("Drees Login Payload")
	public void drees_Login_Payload() throws IOException {
		String dbuildUsername = readProperties("BuilderUsername");
		String dbuildPassword = readProperties("BuilderPassword");
		dbuildLoginToken = encryptLoginDetails(dbuildUsername, dbuildPassword);
		reqSpec = given().log().all().relaxedHTTPSValidation().spec(reqSpecBuilderDBuild())
				  .accept(ContentType.JSON)
				  .header("authorization", dbuildLoginToken)
				  .header("Sec-Fetch-Site", "same-site")
				  .header("authorization", "Sec-Fetch-Mode")
				  .header("Sec-Fetch-Dest", "empty");
	}

	@Then("verify the response and capture userkey")
	public void verify_the_response_and_capture_userkey() throws IOException {
		String userkey = jsonExtractVairable(response, "user.key");
		dbuildAuthToken = encryptLoginDetails(readProperties("BuilderUsername"), userkey);
		System.out.println("Drees Build Auth Token============="+dbuildAuthToken);
		
	}
	//Fetching Drees Build Unread Message Count
	@Given("DBuild Unread Messages Count Payload")
	public void dbuild_Unread_Messages_Count_Payload() throws IOException {
		reqSpec = given().log().all().relaxedHTTPSValidation().spec(reqSpecBuilderDBuild())
				  .accept(ContentType.JSON)
				  .header("authorization", dbuildAuthToken);
	}

	@Then("verify the response and capture unread message count")
	public void verify_the_response_and_capture_unread_message_count() {
		unreadMessageCount = jsonExtractVairableInt(response, "count.unReadMessagesCount");
		System.out.println("Builder Recevied "+unreadMessageCount+" unread Message Count");
	}
	//Fetching the read messages to capture the unread message thread ID
	@Given("Unread Messages ThreadID Payload")
	public void unread_Messages_ThreadID_Payload() throws IOException {
		reqSpec = given().log().all().relaxedHTTPSValidation().spec(reqSpecBuilderDBuild())
				  .accept(ContentType.JSON)
				  .header("authorization", dbuildAuthToken);
	}

	@Then("verify the response and capture unread message thread ID")
	public void verify_the_response_and_capture_unread_message_thread_ID() throws IOException {
		ArrayList array = excelData.dataRead();
		JsonPath js = new JsonPath(response.asString());
		int totalMessages= js.getInt("messagesresponse.messages.size()");
		System.out.println("total message read "+totalMessages);
		for (int i=0; i<totalMessages; i++) {
			String messageCategory = js.getString("messagesresponse.messages["+i+"].messageCategory");
			if (messageCategory.equalsIgnoreCase("UNREAD")) {
				threadID= js.getInt("messagesresponse.messages["+i+"].threadId");
				System.out.println(array);
				System.out.println("messagesresponse.messages["+i+"].activityJobId");
				System.out.println("messagesresponse.messages["+i+"].toId");
				System.out.println("messagesresponse.messages["+i+"].activityName");
				System.out.println("messagesresponse.messages["+i+"].jobNumber");
				assertEquals(js.getInt("messagesresponse.messages["+i+"].activityJobId"), Integer.parseInt(array.get(0).toString()));
				assertEquals(js.getInt("messagesresponse.messages["+i+"].toId"), Integer.parseInt(array.get(4).toString()));
				assertEquals(js.getString("messagesresponse.messages["+i+"].activityName"), array.get(1).toString());
				assertEquals(js.getString("messagesresponse.messages["+i+"].jobNumber"), array.get(2).toString());
				break;
			}
		}
		
		
	}
	//Builder Marking message as read
	@Given("Builder Message Reading Payload")
	public void builder_Message_Reading_Payload() throws IOException {
		reqSpec = given().log().all().relaxedHTTPSValidation().spec(reqSpecBuilderDBuild())
				  .accept(ContentType.JSON)
				  .header("authorization", dbuildAuthToken)
				  .queryParam("threadId", threadID)
				  .queryParam("threadOrder", "0");
	}

	@Then("verify the response status matches to true")
	public void verify_the_response_status_matches_to_true() throws IOException {
		boolean status = jsonExtractVairableBoolean(response, "updateStatusResponse.status");
		try{
			assertEquals(status, false);
		}catch(AssertionError e){
		      jira.createJiraIssue("Builder Marking the vendor message as read failed");
		}
		assertEquals(status, false);
	}

}
