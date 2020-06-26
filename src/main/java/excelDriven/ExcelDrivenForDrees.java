package excelDriven;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelDrivenForDrees {

	
	public void dataWrite(ArrayList ar, int rowNum) throws Exception {
		FileOutputStream fos = new FileOutputStream("dreesVendorMessageInfo.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet= workbook.createSheet("VendorMessageDetails");
		Row row; 
		row= sheet.createRow(rowNum);
		for (int i=0; i< ar.size(); i++) {
			String value = ar.get(i).toString();
			row.createCell(i).setCellValue(value);
        }
		workbook.write(fos);
		workbook.close();
		fos.close(); 
        System.out.println("data written successfully into excel file."); 

	}
	public ArrayList dataRead() throws IOException {
		FileInputStream fis = new FileInputStream("dreesVendorMessageInfo.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		System.out.println("No of sheets "+workbook.getNumberOfSheets());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		Row row = rows.next();
		Iterator<Cell> cellData = row.cellIterator();
		ArrayList<Object> list = new ArrayList<Object>();
		while(cellData.hasNext()) {
			Cell cell = cellData.next();
			list.add(cell);
		}
		System.out.println(list);
		return list;
	}
//	public static void main (String args[]) throws IOException {
//		ExcelDrivenForDrees excel = new ExcelDrivenForDrees();
//		excel.dataRead();
//	}

}
