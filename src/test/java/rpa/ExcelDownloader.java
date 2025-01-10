package rpa;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aspose.cells.Cell;
import com.aspose.cells.CellValueType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
public class ExcelDownloader {
	String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiRW1wbG95ZWUsQWNjb3VudCBNYW5hZ2VyIiwiZnVsbG5hbWUiOiJEYW55YWwgQW1hbiwgTXVoYW1tYWQgIiwibmFtZSI6Im1kYW55YWxAbWVkY2FyZW1zby5jb20iLCJuYW1laWQiOiIxMDI4NDAiLCJQcmFjdGljZUNvZGUiOiIwIiwiUHJvdmlkZXJDb2RlIjoiMCIsIlVzZXJUeXBlIjoiIiwiUGF0aWVudEFjY291bnQiOiIwIiwibmJmIjoxNzM2NDg3NjMyLCJleHAiOjE3MzY1NzQwMzIsImlhdCI6MTczNjQ4NzYzMiwiaXNzIjoiaHR0cDovL3NlY3VyZWxvZ2luLm1lZGNhcmVtc28uY29tLyIsImF1ZCI6IkhuL01ITVRWSWJwMUFjcFlKWWRUbm91ZVJBTlFqQUkzb2NZSWNpYnY3NUU1czhUZmI1U2I2RlhucjZSK0liVUpYY3V3NHNvTytTT2J1RlpuRzFKK2hIbEFkMFpsOHNPNXkyTXB3U1VDaFRJPSJ9.1FhojQD2wLAOkwOT3I2W0WlGnCM8Wyzu2m4S1vnIHUM";
	
	 public List<String> getNPIandStateofPractice() {
		 
		  
			
			HttpRequest request1 = HttpRequest.newBuilder()
					.uri(URI.create("https://maxapi.medcaremso.com/api/Practice/GetPracticeProfile?practiceCode=21003"))
					.header("accept", "application/json")
					.header("accept-language", "en-US,en;q=0.9")
					.header("access-control-allow-credentials", "true")
					.header("access-control-allow-headers", "*")
					.header("access-control-allow-methods", "*")
					.header("access-control-allow-origin", "*")
					.header("authorization", "Bearer "+token+"")
					.header("content-type", "application/json")
					.header("practicecode", "21078")
					.header("sec-ch-ua-mobile", "?0")
					.header("sec-fetch-dest", "empty")
					.header("sec-fetch-mode", "cors")
					.header("sec-fetch-site", "same-site")
					.method("GET", HttpRequest.BodyPublishers.noBody())				
					.build();
			HttpResponse<String> response1 = null;
			List<String> AddressandNPIArray = new ArrayList<String>();
			try {
			
				response1  = HttpClient.newHttpClient().send(request1, HttpResponse.BodyHandlers.ofString());
				ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode rootNode = objectMapper.readTree(response1.body());	           
	            JsonNode address = rootNode.path("payload").path("Billing_Address");
	            AddressandNPIArray.add(address.asText());
	        //    System.out.println(address.asText());
	            JsonNode NPI = rootNode.path("payload").path("Group_NPI");
	            AddressandNPIArray.add(NPI.asText());
	        //    System.out.println(NPI.asText());
	 }catch(Exception e) {}
			return AddressandNPIArray;
	 }
	
	 public void downloadExcel() {
	        
        String jsonBody = "{"
                + "\"PartialFilterOnGroup\":false,"
                + "\"Status\":\"New\","
                + "\"ArFilterOnObjDetails\":{"
                + "\"ClaimTypeDetail\":\"all\","
                + "\"IsPartialClaimFilter\":false,"
                + "\"ClaimCategory\":\"all\","
                + "\"AssignedTo\":\"all\","
                + "\"PageNumber\":1,"
                + "\"RowOfPage\":1000"
                + "},"
                + "\"noResponseCheckbox\":false,"
                + "\"denialCodeCheckbox\":false,"
                + "\"moveByUserCheckbox\":false,"
                + "\"agingCheckbox\":true,"
                + "\"payerCheckbox\":true,"
                + "\"ClaimNoFltOnGroup\":null,"
                + "\"PRACTICE_CODE\":\"21003\","
                + "\"GroupType\":\"Payer-Aging\","
                + "\"RightWiseData\":\"New,In-Progress,Completed,\","
                + "\"PayerListOnGroup\":[],"
                + "\"DenialListOnGroup\":[],"
                + "\"AgingListOnGroup\":[],"
                + "\"ProvideListOnGroup\":[],"
                + "\"LocationListOnGroup\":[],"
                + "\"GroupTypeValue\":\"Total_\","
                + "\"AllClaimsFilter\":true,"
                + "\"TabIndex\":0"
                + "}";

        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Create HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://maxapi.medcaremso.com/api/Claim/GenerateCollectionExcel"))
                .header("accept", "application/json")
                .header("accept-language", "en-US,en;q=0.9")
                .header("access-control-allow-credentials", "true")
                .header("access-control-allow-headers", "*")
                .header("access-control-allow-methods", "*")
                .header("access-control-allow-origin", "*")
                .header("authorization", "Bearer "+token+"")
                .header("content-type", "application/json")
                .header("practicecode", "21003")
                .header("priority", "u=1, i")
                .header("sec-ch-ua", "\"Not)A;Brand\";v=\"99\", \"Google Chrome\";v=\"127\", \"Chromium\";v=\"127\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "same-site")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            // Send request and receive response
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Check response status code
            if (response.statusCode() == 200) {
                // Save the response input stream to a file
                try (InputStream in = response.body();
                     FileOutputStream fileOutputStream = new FileOutputStream("Availity Report.xlsx")) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }

                    System.out.println("File downloaded successfully!");
                }
            } else {
                // Print error message
                System.err.println("Failed to download file. HTTP Status Code: " + response.statusCode());
                try (InputStream errorStream = response.body()) {
                    String errorResponse = new String(errorStream.readAllBytes());
                    System.err.println("Error Response: " + errorResponse);
                } catch (IOException e) {
                    System.err.println("Error reading error response: " + e.getMessage());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error sending request: " + e.getMessage());
        }
    }
	 
	 
	 
	 public static String extractStateAcronym(String address) {
	        // Regular expression to match the state acronym (2 uppercase letters)
	        String regex = ",\\s*([A-Z]{2}),";
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(address);

	        // Find the last occurrence of the state acronym
	        String stateAcronym = null;
	        while (matcher.find()) {
	            stateAcronym = matcher.group(1); // Update to the latest match
	        }
	        
	        return stateAcronym; // Return the last found state acronym or null if none found
	    }
	  public static String vlookup(Worksheet worksheet, String lookupValue, int lookupColumn, int resultColumn) {
	        // Iterate through each row in the worksheet
	        for (int rowIndex = 1; rowIndex < worksheet.getCells().getMaxDataRow() + 1; rowIndex++) {
	            // Get the value in the lookup column for the current row
	            Cell cell = worksheet.getCells().get(rowIndex, lookupColumn);
	            if (cell.getType() == CellValueType.IS_STRING) {
	                String value = cell.getStringValue();
	                // Check if values match for the lookup column
	                if (lookupValue.equals(value)) {
	                    // Get the corresponding value from the result column
	                    return worksheet.getCells().get(rowIndex, resultColumn).getStringValue();
	                }
	            }
	        }

	        // If no match is found, return an empty string or whatever is appropriate for your use case
	        return "No match found";
	    }
	    
	    public static void performVLookup(String excel1, String excel2) throws Exception {
      Workbook workbook1 = new Workbook(System.getProperty("user.dir")+"\\"+excel1);
      Workbook workbook2 = new Workbook(System.getProperty("user.dir") + "\\"+excel2);
     
      Worksheet worksheet1 = workbook1.getWorksheets().get(0);
      Worksheet worksheet2 = workbook2.getWorksheets().get(0);

      // Get the data range from Excel1 (1 column to lookup)
      int lookupColumnLIS = 9; // 0-based index of the column to lookup in  Excel2 (LIS Master)

      // Get the column indices in Excel2 where to place the VLOOKUP results
      int targetColumn1 = 0; //Claim Number // 0-based index of the first column in Excel1 to place the VLOOKUP result
     
      
      // Iterate through each row in Excel2
      for (int rowIndex = 1; rowIndex < worksheet1.getCells().getMaxDataRow() + 1; rowIndex++) {
          // Get the value to lookup from Excel2
          Cell cell = worksheet1.getCells().get(rowIndex, lookupColumnLIS);
          String lookupValue = cell.getStringValue();

          // Perform VLOOKUP in Excel1 for the first target column
          String resultValue1 = vlookup(worksheet2, lookupValue, 0, 1);

     

          // Combine the results and place in Excel2
          
          worksheet1.getCells().get(rowIndex, 9).putValue(resultValue1);
        
      }
 

      // Save the modified Excel2
      workbook1.save(excel1);
	    }
	    
	    
	    public static void divideExcel(String inputFile) throws IOException {
	        FileInputStream fis = new FileInputStream(inputFile);
	        XSSFWorkbook workbook = new XSSFWorkbook(fis);
	        Sheet originalSheet = workbook.getSheetAt(0);

	        int totalRows = originalSheet.getPhysicalNumberOfRows();
	        int rowsPerPart = totalRows / 5;

	        // Copy header row
	        Row headerRow = originalSheet.getRow(0);

	        for (int i = 0; i < 5; i++) {
	            XSSFWorkbook newWorkbook = new XSSFWorkbook();
	            Sheet newSheet = newWorkbook.createSheet("Sheet1");

	            // Copy the header to the new sheet
	            Row newHeaderRow = newSheet.createRow(0);
	            copyRow(headerRow, newHeaderRow);

	            int startRow = i * rowsPerPart + 1; // Start from the row after the header
	            int endRow = (i == 4) ? totalRows : startRow + rowsPerPart;

	            for (int rowIndex = startRow; rowIndex < endRow; rowIndex++) {
	                Row originalRow = originalSheet.getRow(rowIndex);
	                Row newRow = newSheet.createRow(rowIndex - startRow + 1); // Adjust for header
	                copyRow(originalRow, newRow);
	            }

	            FileOutputStream fos = new FileOutputStream("Availity " + (i + 1) + ".xlsx");
	            newWorkbook.write(fos);
	            fos.close();
	            newWorkbook.close();
	        }

	        workbook.close();
	        fis.close();
	    }

	    private static void copyRow(Row originalRow, Row newRow) {
	        if (originalRow != null) {
	            for (int j = 0; j < originalRow.getPhysicalNumberOfCells(); j++) {
	               org.apache.poi.ss.usermodel.Cell originalCell = originalRow.getCell(j);
	                org.apache.poi.ss.usermodel.Cell newCell = newRow.createCell(j);
	                if (originalCell != null) {
	                    switch (originalCell.getCellType()) {
	                        case STRING:
	                            newCell.setCellValue(originalCell.getStringCellValue());
	                            break;
	                        case NUMERIC:
	                            newCell.setCellValue(originalCell.getNumericCellValue());
	                            break;
	                        case BOOLEAN:
	                            newCell.setCellValue(originalCell.getBooleanCellValue());
	                            break;
	                        case FORMULA:
	                            newCell.setCellFormula(originalCell.getCellFormula());
	                            break;
	                        default:
	                            newCell.setBlank();
	                            break;
	                    }
	                }
	            }
	        }
	    }

	    
	    
	    public static void addHeaders(String inputFile, int startColumn) throws IOException {
	    	
	    	 String[] headers = {
	                 "EOB Downloaded", "Availity DOS", "Claim Number", "Check Number", "Check Date",
	                 "Finalized Date", "Payment Date", "Paid Amount", "Allowed Amount",
	                 "Received Date", "Denial Reason", "Action", "Line CPT", "Line Paid",
	                 "Line Hippa", "Line Remarks", "Line Copay", "Line Deductible",
	                 "Line Ineligible", "Line Coinsurance", "Bot Status", "Maximus Status"
	             };
	    	
	        FileInputStream fis = new FileInputStream(inputFile);
	        XSSFWorkbook workbook = new XSSFWorkbook(fis);
	        Sheet sheet = workbook.getSheetAt(0); // Modify if you need a different sheet

	        // Create a new row for the headers, or get the first row if it already exists
	        Row headerRow = sheet.getRow(0);
	        if (headerRow == null) {
	            headerRow = sheet.createRow(0);
	        }

	        // Add headers starting from the specified column
	        for (int i = 0; i < headers.length; i++) {
	            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(startColumn + i);
	            cell.setCellValue(headers[i]);
	        }

	        // Write changes to a new file or overwrite the original
	        FileOutputStream fos = new FileOutputStream(inputFile);
	        workbook.write(fos);
	        fos.close();
	        workbook.close();
	        fis.close();
	    }
	    
	 
}


	 

