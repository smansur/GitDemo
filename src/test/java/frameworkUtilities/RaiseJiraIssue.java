package frameworkUtilities;

import static io.restassured.RestAssured.given;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class RaiseJiraIssue extends RequestSpec_Prop_Encryption_JsonFetchData{
	String JiraID=null;
	public String readJsonFile(String path) throws IOException {
		return new String (Files.readAllBytes(Paths.get(path)));
	}
	public void createJiraIssue(String testcasename) throws IOException {
		
		String jsonBody = readJsonFile("src\\test\\java\\jsonFiles\\CreateJiraIssue.json");
		Configuration configuration = Configuration.builder()
									 .jsonProvider(new JacksonJsonNodeJsonProvider())
									 .mappingProvider(new JacksonMappingProvider()).build();
		DocumentContext json = com.jayway.jsonpath.JsonPath.using(configuration).parse(jsonBody);
		String jiraJson = json.set("fields.summary",testcasename).jsonString();
		
		String AuthToken = encryptLoginDetails(readProperties("JiraAccountID"), readProperties("JiraAccountToken"));
		RestAssured.baseURI = readProperties("JiraBaseURL");
		String response = given().log().all().header("Content-Type","application/json")
		.header("Authorization","Basic "+AuthToken)
		.body(jiraJson)
		.when().post(readProperties("JiraTicketRaiseAPI"))
		.then().log().all().assertThat().statusCode(201).extract().response().asString();
		JsonPath js= new JsonPath(response);
		JiraID= js.getString("key");
		System.out.println(JiraID);
		
	}
//	public static void main (String args[]) throws IOException {
//		RaiseJiraIssue jira=new RaiseJiraIssue();
//		
//		jira.createJiraIssue("Login to dress team failed");
//	}
}
