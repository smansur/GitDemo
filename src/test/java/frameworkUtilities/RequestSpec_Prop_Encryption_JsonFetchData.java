package frameworkUtilities;
import static org.hamcrest.Matchers.lessThan;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Base64;
import java.util.Properties;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class RequestSpec_Prop_Encryption_JsonFetchData {
	
	static RequestSpecification reqSpec;
	static RequestSpecification reqSpecDBuild;
	public RequestSpecification reqSpecBuilder() throws IOException {
		
		if(reqSpec == null) {
		PrintStream stream = new PrintStream(new FileOutputStream("LogginDreesTeam.txt"));
		reqSpec = new RequestSpecBuilder().setBaseUri(readProperties("DreesTeamBaseURL"))
		.setContentType(ContentType.JSON)
		.addFilter(RequestLoggingFilter.logRequestTo(stream))
		.addFilter(ResponseLoggingFilter.logResponseTo(stream))
		.build();
		return reqSpec;
		}else {
			return reqSpec;
		}
		
	}
	public RequestSpecification reqSpecBuilderDBuild() throws IOException {
		
		if(reqSpecDBuild == null) {
		PrintStream stream = new PrintStream(new FileOutputStream("LogginDBuild.txt"));
		reqSpecDBuild = new RequestSpecBuilder().setBaseUri(readProperties("DreesBuildBaseURL"))
		.setContentType(ContentType.JSON)
		.addFilter(RequestLoggingFilter.logRequestTo(stream))
		.addFilter(ResponseLoggingFilter.logResponseTo(stream))
		.build();
		return reqSpecDBuild;
		}else {
			return reqSpecDBuild;
		}
	}
	public ResponseSpecification resSpecBuilder() throws NumberFormatException, IOException {
		long reponseTime= Long.parseLong(readProperties("ExpectedResponseTime"));
		ResponseSpecification resSpec = new ResponseSpecBuilder().expectStatusCode(200).expectResponseTime(lessThan(reponseTime)).build();
		return resSpec;
	}
	
	public static String readProperties(String key) throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("C:\\Users\\USER\\Desktop\\Selenium\\CostrategixPresentation\\src\\test\\java\\frameworkUtilities\\glober.properties");
		prop.load(fis);
		return prop.getProperty(key);
		
	}
	public String jsonExtractVairable(Response response, String path) {
		JsonPath js = new JsonPath(response.asString());
		return js.get(path);
	}
	public int jsonExtractVairableInt(Response response, String path) {
		JsonPath js = new JsonPath(response.asString());
		return js.getInt(path);
	}
	public boolean jsonExtractVairableBoolean(Response response, String path) {
		JsonPath js = new JsonPath(response.asString());
		return js.getBoolean(path);
	}
	public String encryptLoginDetails(String username, String password) {
		String data = username+":"+password;
		String BasicBase64format= Base64.getEncoder().encodeToString(data.getBytes());
		return BasicBase64format;
	}
}
