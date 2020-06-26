package frameworkUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import excelDriven.ExcelDrivenForDrees;

public class JsonDataParsing{
	RequestSpec_Prop_Encryption_JsonFetchData prop = new RequestSpec_Prop_Encryption_JsonFetchData();
	HashMap<String, Object> logindata = new HashMap<String, Object>();
	public HashMap<String, Object> loginJson() throws IOException {
		logindata.put("email", prop.readProperties("VendorUsername"));
		logindata.put("password", prop.readProperties("VendorPassword"));
		return logindata;
		
	}
	public HashMap<String, Object> vendorMsgJson() throws IOException {
		ExcelDrivenForDrees data = new ExcelDrivenForDrees();
		ArrayList array = data.dataRead();
		//Because while reading the excel file and taking into array we haven't declared any type for that array
		//Here my json expects int values. To convert that using int parsing and to parse that value should in 
		//String format. So adding toString() to data reading from array values
		logindata.put("atjId", Integer.parseInt(array.get(0).toString()));
		logindata.put("body", prop.readProperties("VendorMessage"));
		logindata.put("jobId", Integer.parseInt(array.get(3).toString()));
		logindata.put("subject", " ");
		logindata.put("threadId", 0);
		logindata.put("threadOrder", 0);
		logindata.put("toId", Integer.parseInt(array.get(4).toString()));
		return logindata;
		
	}
}
