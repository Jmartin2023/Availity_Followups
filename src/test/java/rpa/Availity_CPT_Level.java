package rpa;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;



import objects.ExcelOperations;
import objects.SeleniumUtils;
import objects.Utility;
import objects.ExcelReader;




public class Availity_CPT_Level {
	Logger logger = LogManager.getLogger(Availity_CPT_Level.class);

	String projDirPath,NPI, status, claimNo ,claimNumAvaility, AvailityDOS, denialReason,DOB ,serviceDate ,firstName, lastName,memberID, maximusStatus,DOSFrom, DOSTo, claimStatus,dateofbirth, npivalue, charges,currency, error, originalTab, checkNum,checkDate,paidAmount,paymentDate, receivedDate, allowedAmount, processedDate,finalizedDate;
	
	SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yy");

	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	public static ExcelReader excel, excel1; 
	public static String sheetName = "Sheet1";
	int rowNum = 1;
	boolean skipFlag =false;
	WebDriver driver;

	//JavascriptExecutor js;
	SeleniumUtils sel;
	Utility utility;

	ExcelOperations excelFile;
	Availity_Objects bcbs;
	static String excelFileName, payer;

	@BeforeTest
	public void preRec() throws InterruptedException, SAXException, IOException, ParserConfigurationException {
		 System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+ "\\chromedriver.exe");

	        // Configure Chrome options for PDF printing
	        ChromeOptions options = new ChromeOptions();
	        options.addArguments("--headless");  // Run in headless mode for background processing
	     
	        // Set up print preferences for saving as PDF
	        Map<String, Object> prefs = new HashMap<>();
	        prefs.put("download.default_directory", System.getProperty("user.dir") + "\\DownloadedFiles");
	        
	        prefs.put("printing.print_preview_sticky_settings.appState",
	                  "{\"recentDestinations\":[{\"id\":\"Save as PDF\",\"origin\":\"local\"}],\"selectedDestinationId\":\"Save as PDF\",\"version\":2}");
	        options.setExperimentalOption("prefs", prefs);
	        options.addArguments("--kiosk-printing");  // Auto-selects the "Save as PDF" option

	        // Initialize WebDriver with ChromeOptions
	         driver = new ChromeDriver(options);

	
	sel = new SeleniumUtils(projDirPath);

	//	driver = sel.getDriver();

		//js = (JavascriptExecutor) driver;
		bcbs= new Availity_Objects(driver);
		utility = new Utility();
		
		String[] params = new String[]{"url", "username", "password", "state","excelName","excelNameBackup"};
		HashMap<String, String> configs = utility.getConfig("config.xml", params);

		String url = configs.get("url"), 
				username = configs.get("username"), 
				state = configs.get("state"),
				password = configs.get("password");
		
		excelFileName = configs.get("excelName");
		String excelBackup = configs.get("excelNameBackup");
		excel1 = new ExcelReader(System.getProperty("user.dir")+"\\"+excelBackup);
		System.out.println(excelFileName);

		driver.get(url);
		logger.info("Open url: " + url);

		sel.pauseClick(bcbs.loginBtn, 10);

		bcbs.usernameField.sendKeys(username);
		logger.info("Enter username: " + username);


		bcbs.passwordField.sendKeys(password);
		logger.info("Enter password");

		

		bcbs.loginBtn.click();
		logger.info("Click login button");
		
		Thread.sleep(10000);
		
		
		sel.pauseClick(driver.findElement(By.xpath("//input[contains(@id,'elect-Enter a one-time use backup code')]")), 10);
		driver.findElement(By.xpath("//input[contains(@id,'elect-Enter a one-time use backup code')]")).click();
		logger.info("Clicked on enter backup codes");
		
		driver.findElement(By.xpath("//button[text()='Continue']")).click();
		logger.info("Clicked on Continue button");
		
		Thread.sleep(2000);
		sel.pauseClick(driver.findElement(By.id("code")), 10);
		
		String backupCode = excel1.getCellData(sheetName, "Code", 2);
String usedCode = excel1.getCellData(sheetName, "Used Code", 2);
		
		if(backupCode.equals(usedCode)) {
			driver.quit();
			Assert.fail("Back up code not updated");
			
		}
		
		driver.findElement(By.id("code")).sendKeys(backupCode);
		System.out.println("backup code entered is " + backupCode);
		excel1.setCellData(sheetName, "Used Code", 2, backupCode);
		
		driver.findElement(By.xpath("//button[text()='Continue']")).click();
		logger.info("Clicked on Continue button");
		
		Thread.sleep(4000);
		
		driver.findElement(By.xpath("//button[text()='Continue']")).click();
		logger.info("Clicked on Continue button");
		
		Thread.sleep(5000);
		try {
			Thread.sleep(2000);
			logger.info("waiting for My account in try");
		bcbs.waitFunc(driver.findElement(By.xpath("//div[@class='container-fluid']//a[contains(@title,'Account')]")));
		driver.findElement(By.xpath("//div[@class='container-fluid']//a[contains(@title,'Account')]")).click();
		logger.info("Clicked on user's account");
		}catch(Exception e) {
			Thread.sleep(2000);
			logger.info("waiting for My account in catch");
				for(int i=0; i<5; i++) {
					Thread.sleep(4000);
				try{ 
					if(driver.findElement(By.xpath("//div[@class='container-fluid']//a[contains(@title,'Account')]")).isDisplayed()) 
					{
					bcbs.waitFunc(driver.findElement(By.xpath("//div[@class='container-fluid']//a[contains(@title,'Account')]")));
					driver.findElement(By.xpath("//div[@class='container-fluid']//a[contains(@title,'Account')]")).click();
					logger.info("Clicked on user's account");
					break;
					}
					}catch(Exception e1) {}
			}
		}
		
		
		
	
		
		Thread.sleep(1500);
		bcbs.waitFunc(driver.findElement(By.xpath("//a[text()=\"My Account\"]")));
		driver.findElement(By.xpath("//a[text()=\"My Account\"]")).click();
		logger.info("Clicked on My account");
		
		

		Thread.sleep(2000);
		driver.switchTo().defaultContent();
		driver.findElement(By.id("onetrust-accept-btn-handler")).click();
		logger.info("Clicked on Accept Cookies");
		driver.switchTo().frame("newBodyFrame");
	
	
		
		
		
		
		
		try {
			bcbs.waitFunc(driver.findElement(By.xpath("//p[text()='Security']")));
			driver.findElement(By.xpath("//p[text()='Security']")).click();
			logger.info("Clicked on security tab");
			}catch(Exception e) {
				
					for(int i=0; i<5; i++) {
						Thread.sleep(4000);
					try{ 
						if(driver.findElement(By.xpath("//p[text()='Security']")).isDisplayed()) 
						{
						bcbs.waitFunc(driver.findElement(By.xpath("//p[text()='Security']")));
						driver.findElement(By.xpath("//p[text()='Security']")).click();
						logger.info("Clicked on security tab");
						break;
						}
						}catch(Exception e1) {}
				}
			}
			
		
		
		
		
		
		
		
		
		
		
		Thread.sleep(3000);
		
	
		bcbs.waitFunc(driver.findElement(By.xpath("//button[text()='Update 2-Step Authentication']")));
		driver.findElement(By.xpath("//button[text()='Update 2-Step Authentication']")).click();
		logger.info("Clicked on 2-Step Authentication");
		
		Thread.sleep(3000);
		bcbs.waitFunc(driver.findElement(By.xpath("//button[text()='Show backup codes']")));
		driver.findElement(By.xpath("//button[text()='Show backup codes']")).click();
		logger.info("Clicked on show backup codes");
		
		Thread.sleep(4000);
		bcbs.waitFunc(driver.findElements(By.xpath("//div[@class='text-center list card-body']")).get(0));
		backupCode = driver.findElements(By.xpath("//div[@class='text-center list card-body']")).get(0).getText();
		logger.info("Extracted backup code saved  is "+backupCode);
		
		excel1.setCellData(sheetName, "Code", 2, backupCode);
		
		driver.switchTo().defaultContent();
		driver.navigate().to("https://apps.availity.com/availity/web/public.elegant.login?p:lm=1695867051");
		
		Thread.sleep(5000);
		try {
		bcbs.waitFunc(bcbs.downArrowDrpDwn);
		bcbs.downArrowDrpDwn.click();
		logger.info("Clicked on down arrow dropdown");
		}catch(Exception e) {
			
				for(int i=0; i<5; i++) {
					Thread.sleep(4000);
				try{ 
					if(bcbs.downArrowDrpDwn.isDisplayed()) 
					{
					bcbs.waitFunc(bcbs.downArrowDrpDwn);
					bcbs.downArrowDrpDwn.click();
					logger.info("Clicked on down arrow dropdown");
					break;
					}
					}catch(Exception e1) {}
			}
		}

		bcbs.waitFunc(bcbs.stateInput);
		bcbs.stateInput.sendKeys(state);
		Thread.sleep(2000);
		//bcbs.michiganState.click();;
		bcbs.getState(state).click();
		logger.info("State enetered as"+ state);
		Thread.sleep(4000);
		try {
			driver.findElement(By.xpath("//div[@class='vex-close aptr-engagement-close-btn px-close-button']")).click();
		}catch(Exception e) {}
		
		
		try {
			driver.findElement(By.xpath("//button[@id='onetrust-accept-btn-handler']")).click();
			logger.info("Clicked on accept cookies");
		}catch(Exception e) {}
		Thread.sleep(3000);
		driver.switchTo().frame("newBodyFrame");
		sel.pauseClick(bcbs.claimStatusTab,60);
		bcbs.waitFunc(bcbs.claimStatusTab);
		
		bcbs.claimStatusTab.click();
		logger.info("Clicked on Claim Status tab");
		
		Thread.sleep(2000);
	/*	try {
		bcbs.waitFunc(bcbs.organizationInput);
		bcbs.organizationInput.clear();
		}catch(Exception e) {
			for(int i=0; i<5; i++) {
				Thread.sleep(4000);
			if(bcbs.organizationInput.isDisplayed()) {
				break;
			}
		}
		}
	
		bcbs.organizationInput.sendKeys("Ark"+Keys.ENTER);
		logger.info("Entered Organization: Ark Laboratory LLC");
	*/	
		
		
		 

		
		
		
	}

	@Test(dataProvider= "getData") 
	public void AvailityPortal(Hashtable<String,String> data) throws InterruptedException, ParseException {
		rowNum++;
		skipFlag=false;
		boolean newInterface = false;
		status = data.get("Bot Status");
		 WebDriverWait waitExplicit = new WebDriverWait(driver, Duration.ofSeconds(50));
			
	
		if(status.isBlank() || status.isBlank()) {
			
			
			try {
				driver.findElement(By.xpath("//a[text()='Search']")).click();
				logger.info("Clicked on Search Tab");
			}catch(Exception e) {
				
			}
			
			payer= data.get("Transaction Payer");
			
			try {
				bcbs.waitFunc(bcbs.payerInput);
				}catch(Exception e) {
					for(int i=0; i<5; i++) {
						Thread.sleep(4000);
					try {
							bcbs.payerInput.isDisplayed();
						break;
					}catch(Exception e1) {}	
				}
					
				}
			
			bcbs.payerInput.clear();
			bcbs.payerInput.sendKeys(payer);
			Thread.sleep(1000);
			bcbs.payerInput.sendKeys(Keys.ENTER);
		//	bcbs.payerInput.sendKeys(Keys.SPACE);
			logger.info("Entered Payer: "+payer);
			Thread.sleep(2000);
		//	driver.switchTo().frame("newBodyFrame");
			
		try {	
			bcbs.waitFunc(bcbs.hipaaTab);
			bcbs.hipaaTab.click();
			logger.info("Clicked on HIPAA tab");
			}catch(Exception e) {
				excel.setCellData(sheetName, "Bot Status", rowNum, "Payer not found");
				throw new SkipException("Skipping this exception, Payer not found");
			
			}
	//		originalTab  = driver.getWindowHandle();
		//	System.out.println(driver.getWindowHandle());
			//driver.findElement(By.id("payer")).sendKeys(payer+Keys.ENTER);
			//logger.info("Payer selected as "+payer);
			try {
				bcbs.waitFunc(bcbs.resultsTab);
				bcbs.resultsTab.click();
				logger.info("Clicked on results");
			}catch(Exception e) {}
			
			try {
				bcbs.waitFunc(bcbs.clearForm);
				}catch(Exception e) {
					for(int i=0; i<5; i++) {
						Thread.sleep(4000);
					try {
							bcbs.clearForm.isDisplayed();
						break;
					}catch(Exception e1) {}	
				}
					
				}
			
	//		driver.switchTo().frame("newBodyFrame");
			
			bcbs.clearForm.click();
			  String providerForNPI = data.get("Provider");
			SimpleDateFormat parser = new SimpleDateFormat("M/dd/yy");
			// output format: yyyy-MM-dd
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			DOB = data.get("Date of Birth");
			DOSFrom = data.get("SERVICE DATE From");
			DOSTo = data.get("SERVICE DATE To");
			//System.out.println(formatter.format(parser.parse(DOB)) +" is date");
			DOSFrom= formatter.format(parser.parse(DOSFrom));
			DOSTo= formatter.format(parser.parse(DOSTo));
			dateofbirth=	formatter.format(parser.parse(DOB));
			firstName = data.get("First Name").toUpperCase().trim();
			lastName = data.get("Last Name").toUpperCase().trim();
			memberID = data.get("Member ID");
			charges = data.get("CHARGES").replace("$", "");
			double balanceDouble = Double.parseDouble(charges); 
			 NumberFormat currencyformatter=NumberFormat.getCurrencyInstance(Locale.US);  
			  currency=currencyformatter.format(balanceDouble);
			System.out.println(currency);
			logger.info("Last name is "+lastName);
			logger.info("First name is "+firstName);
			logger.info("DOB is "+dateofbirth
					);
		try{	
	        // Convert the scientific notation string to a double
	        double number = Double.parseDouble(memberID);
	        
	        // Create a DecimalFormat instance to format the number
	        DecimalFormat decimalFormat = new DecimalFormat("0"); // Customize as needed
	        
	        // Format the number
	        String formattedNumber = decimalFormat.format(number);
	        
	        // Print the result
	        System.out.println("Formatted Number: " + formattedNumber);
	        memberID = formattedNumber;
		}catch(Exception e1) {}
			
			try {
				bcbs.waitFunc(bcbs.providerNpi);
				}catch(Exception e) {
					for(int i=0; i<5; i++) {
						Thread.sleep(4000);
					if(bcbs.providerNpi.isDisplayed()) {
						break;
					}
				}
				}
			 ///////////////////////////////////////
			
			if(providerForNPI.equals("IMS Experts, LLC")) {
				NPI="1972697068";
			}
			else if(providerForNPI.equals("Health ROM")) {
				NPI="1194307090";
			}
			else if(providerForNPI.equals("ROM Therapy")) {
				NPI="1285380360";
			}
	
		
				npivalue= bcbs.providerNpi.getAttribute("value");
				if(npivalue.isBlank() || npivalue.isEmpty()) {
				bcbs.providerNpi.sendKeys(NPI);
				logger.info("Entered NPI: "+ NPI);
				}
				else {
					bcbs.providerNpi.sendKeys("");
					bcbs.providerNpi.sendKeys(NPI);
					logger.info("Entered NPI: "+ NPI +" for provider "+providerForNPI);
				}
			
			
		
			
	try {		bcbs.waitFunc(bcbs.memberIDInput); 
			bcbs.memberIDInput.clear();
			bcbs.memberIDInput.sendKeys(memberID);
			logger.info("Entered member ID: "+ memberID);
	}catch(Exception e) {
		driver.findElement(By.id("subscriberMemberId")).clear();
		driver.findElement(By.id("subscriberMemberId")).sendKeys(memberID);
		logger.info("Entered member ID: "+ memberID);
	}
			bcbs.patientLastName.sendKeys(lastName);
			logger.info("Entered last Name: "+ lastName);
			
			bcbs.patientFirstName.clear();
			bcbs.patientFirstName.sendKeys(firstName);
			logger.info("Entered first Name: "+ firstName);
			
			bcbs.patientBirthDate.clear();
			bcbs.patientBirthDate.sendKeys(dateofbirth);
			logger.info("DOB entered as: "+ dateofbirth);
			
			
			/*		try {
				driver.findElement(By.id("subscriberLastName")).sendKeys(lastName);
			}catch(Exception e) {
				
			}
			
			try {
				driver.findElement(By.id("subscriberFirstName")).sendKeys(firstName);
			}catch(Exception e) {
				
			}
			*/
			
			try {
				driver.findElement(By.xpath("//input[contains(@id,'patientIsSubscriber')]")).click();
				logger.info("Clicked on check box Subscriber is same as patient");
			}catch(Exception e) {}
	try {	
			bcbs.serviceDatestart.clear();
			bcbs.serviceDatestart.sendKeys(DOSFrom.split("/")[0]+DOSFrom.split("/")[1]+DOSFrom.split("/")[2]);
		//	bcbs.serviceDatestart.sendKeys(Keys.ENTER);
			logger.info("DOS From entered as: "+ DOSFrom);
	}catch(Exception e) {
		driver.findElement(By.id("fromDate")).clear();
		driver.findElement(By.id("fromDate")).sendKeys(DOSFrom.split("/")[0]+DOSFrom.split("/")[1]+DOSFrom.split("/")[2]);
		logger.info("DOS From entered as: "+ DOSFrom);
	}
			
	try {		bcbs.serviceDateend.clear();
			bcbs.serviceDateend.sendKeys(DOSTo.split("/")[0]+DOSTo.split("/")[1]+DOSTo.split("/")[2]);
		//	bcbs.serviceDateend.sendKeys(Keys.ENTER);
			logger.info("DOS To entered as: "+ DOSTo);
	}catch(Exception e) {
		driver.findElement(By.id("toDate")).clear();
		driver.findElement(By.id("toDate")).sendKeys(DOSTo.split("/")[0]+DOSTo.split("/")[1]+DOSTo.split("/")[2]);
		logger.info("DOS To entered as: "+ DOSTo);
	}	
			
		try {	bcbs.claimAmountInput.click();
		}catch(Exception e) {
			
		}
			bcbs.submitBtn.click(); 
			logger.info("Clicked on Submit button");
			
			sel.pauseClick(bcbs.transactionIDLogo, 15);
			Thread.sleep(5000);
			
			try {
				bcbs.transactionIDLogo.isDisplayed();
			}catch(Exception e) {
				excel.setCellData(sheetName, "Bot Status", rowNum, "Data error");
				throw new SkipException("Skipping this exception, Data error");
			}
			
			
			try {
				bcbs.waitFunc(driver.findElement(By.xpath("//div[1][@role='alert']/ul/li")));
				driver.findElement(By.xpath("//div[1][@role='alert']/ul/li")).isDisplayed();
				error=driver.findElement(By.xpath("//div[1][@role='alert']/ul/li")).getText();
				bcbs.clearForm.click();
				logger.info("Form cleared");
				skipFlag=true;
		
				
				
			}catch(Exception e) {
				
			}
			if(	skipFlag==true) {
				excel.setCellData(sheetName, "Bot Status", rowNum, error);
				throw new SkipException("Skipping this exception, "+error);
			}
			driver.switchTo().defaultContent();
			driver.switchTo().frame("newBody");
			
			
try {
				
				bcbs.waitFunc(bcbs.claimStatus(firstName, lastName,currency));
				}catch(Exception e) {
					for(int i=0; i<5; i++) {
						Thread.sleep(4000);
					try {
							bcbs.claimStatus(firstName, lastName,currency).isDisplayed();
						break;
					}catch(Exception e1) {}	
				}
					
				}
			
		try {	
			claimStatus= bcbs.claimStatus(firstName, lastName,currency).getText();
			excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
		}catch(Exception e) {
			
			excel.setCellData(sheetName, "Bot Status", rowNum, "name or charge mismatch");
			throw new SkipException("Skipping this exception,  name or charge mismatch");
		}
		
		try {
			bcbs.claimStatus(firstName, lastName,currency).click();
			logger.info("Clicked on the claim status");
		}catch(Exception e2) {
			excel.setCellData(sheetName, "Bot Status", rowNum, "name or charge mismatch");
			throw new SkipException("Skipping this exception,  name or charge mismatch");
		}
		
			
			if (payer.equals("HUMANA")){
				
				
					
		
			try {
				claimNumAvaility = driver.findElement(By.xpath("//div[@id='Claim Number']/p[2]")).getText();
				logger.info("Claim number in availity app is "+ claimNumAvaility);
				}catch(Exception e) {
					for(int i=0; i<5; i++) {
						Thread.sleep(4000);
					try {
						claimNumAvaility = driver.findElement(By.xpath("//div[@id='Claim Number']/p[2]")).getText();
						logger.info("Claim number in availity app is "+ claimNumAvaility);
						break;
					}catch(Exception e1) {}	
				}
					
				}
			
			
				
				
				 try {
			           

			            // Print to PDF (requires DevTools Protocol Command)
			            Map<String, Object> printOptions = new HashMap<>();
			            printOptions.put("paperWidth", 8.5);  // Set width in inches
			            printOptions.put("paperHeight", 11);  // Set height in inches
			            printOptions.put("printBackground", true);  // Include background graphics

			            // Execute the command and get the result
			            Map<String, Object> result = ((ChromeDriver) driver)
			                    .executeCdpCommand("Page.printToPDF", printOptions);

			            // Extract the base64 PDF string from the result
			            String base64PDF = (String) result.get("data");

			            // Save the PDF from base64 to a file
			            byte[] decoded = java.util.Base64.getDecoder().decode(base64PDF);
			            java.nio.file.Files.write(java.nio.file.Paths.get(System.getProperty("user.dir") + "\\DownloadedFiles\\"+firstName+"-"+lastName+".pdf"), decoded);

			            System.out.println("PDF saved as firstName.pdf");
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
				
				try {
				AvailityDOS = driver.findElement(By.xpath("//div[@id='Service Dates']/p[2]")).getText();
				logger.info("Availity DOS  in availity app is "+ AvailityDOS);
				}catch(Exception e) {}
			
				try {
				paidAmount = driver.findElement(By.xpath("//div[@id='Paid Amount']/p[2]")).getText();
		
				logger.info("Paid amount is "+ paidAmount);
				}catch(Exception e) {}
				
				try {
				claimStatus = driver.findElement(By.xpath("//div[@id='Claim Status']/p[2]")).getText();
				
				
				logger.info(claimStatus);
				}catch(Exception e) {}
				
				try {
				processedDate = driver.findElement(By.xpath("//div[@data-testid='results-header']//span[contains(text(),'Processed Date')]/parent::span/parent::div/following-sibling::div/span")).getText();
				logger.info("Processed Date is: "+ processedDate);
				}catch(Exception e) {}
				
				try {
				checkDate = driver.findElement(By.xpath("//div[@id='Check Date']/p[2]")).getText();
				logger.info("Check date is: "+checkDate);
				}catch(Exception e) {}
				try {
				checkNum = driver.findElement(By.xpath("//div[@id='Check Number']/p[2]")).getText();
				logger.info("Check number is: "+checkNum);
				}catch(Exception e) {}
				
				excel.setCellData(sheetName, "Check Number", rowNum, checkNum);
				excel.setCellData(sheetName, "Claim Number", rowNum, claimNumAvaility);
				excel.setCellData(sheetName, "Check Date", rowNum, checkDate);
				excel.setCellData(sheetName, "Payment Date", rowNum, paymentDate);
				excel.setCellData(sheetName, "Received Date", rowNum, receivedDate);
				excel.setCellData(sheetName, "Paid Amount", rowNum, paidAmount);
				excel.setCellData(sheetName, "Claim Number", rowNum, claimNumAvaility);
				excel.setCellData(sheetName, "DOS", rowNum, AvailityDOS);
				excel.setCellData(sheetName, "Denial Reason", rowNum, denialReason);
			excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
			}
			
			
			
			logger.info(claimStatus);
			if( !payer.equals("HUMANA") && (newInterface==false)) {
				
			
				//Paid = Finalized
				 waitExplicit.until(ExpectedConditions.visibilityOf(bcbs.claimNumber));
				 
				 try {
					 waitExplicit.until(ExpectedConditions.visibilityOf(bcbs.claimNumber));
					bcbs.waitFunc(bcbs.claimNumber);
					}catch(Exception e) {
						for(int i=0; i<5; i++) {
							Thread.sleep(4000);
						try {
							bcbs.claimNumber.isDisplayed();
							break;
						}catch(Exception e1) {}	
					}
						
					}
				
				 
					
				claimNumAvaility= 	bcbs.claimNumber.getText();
				logger.info("Claim number in availity app is "+ claimNumAvaility);
				try {
				checkNum = driver.findElement(By.xpath("//div[@id='Check Number']/p[2]")).getText(); //new change
				}catch(Exception e) {}
				
				
				 try {
			          

			            // Print to PDF (requires DevTools Protocol Command)
			            Map<String, Object> printOptions = new HashMap<>();
			            printOptions.put("paperWidth", 8.5);  // Set width in inches
			            printOptions.put("paperHeight", 11);  // Set height in inches
			            printOptions.put("printBackground", true);  // Include background graphics

			            // Execute the command and get the result
			            Map<String, Object> result = ((ChromeDriver) driver)
			                    .executeCdpCommand("Page.printToPDF", printOptions);

			            // Extract the base64 PDF string from the result
			            String base64PDF = (String) result.get("data");

			            // Save the PDF from base64 to a file
			            byte[] decoded = java.util.Base64.getDecoder().decode(base64PDF);
			            java.nio.file.Files.write(java.nio.file.Paths.get(System.getProperty("user.dir") + "\\DownloadedFiles\\"+firstName+"-"+lastName+".pdf"), decoded);

			            System.out.println("PDF saved as firstName.pdf");
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
				
				
				try {
				checkDate= driver.findElement(By.xpath("//div[@id='Check Date']/p[2]")).getText(); //new change
				}catch(Exception e) {}
				try {
				paymentDate =driver.findElement(By.xpath("//div[@id='Payment Date']/p[2]")).getText(); 
				}catch(Exception e) {}
			try {
				finalizedDate = driver.findElement(By.xpath("//div[@id='Finalized Date']/p[2]")).getText(); 
			}catch(Exception e) {}
			try {
				paidAmount = driver.findElement(By.xpath("//div[@id='Paid Amount']/p[2]")).getText(); 
			}catch(Exception e) {}
			try {
				receivedDate = driver.findElement(By.xpath("//div[@id='Received Date']/p[2]")).getText(); 
			}catch(Exception e) {}
			try {
				AvailityDOS=  driver.findElement(By.xpath("//div[@id='Service Dates']/p[2]")).getText();
			}catch(Exception e) {}
				
			try {
			denialReason=  driver.findElement(By.xpath("//table[@id='codesTable']/descendant::tr[4]/td[3]")).getText();
			}catch(Exception e) {}

			/*	
			Thread.sleep(2000);
			List<WebElement> lineLevelCPTS = driver.findElements(By.xpath("//table[@id='lineLevelTable']//tr/td[3]/p"));
		List<String> lineCPTs = new ArrayList<String>();
			for(int i=0; i<lineLevelCPTS.size(); i++) {
				lineCPTs.add( lineLevelCPTS.get(i).getText());
			}
			excel.setCellData(sheetName, "Line CPT", rowNum, lineCPTs.toString());
			
			
		List<WebElement> lineLevelPaid = driver.findElements(By.xpath("//table[@id='lineLevelTable']//tr/td[13]"));
	
		List<String> linePaid = new ArrayList<String>();
			for(int i=0; i<lineLevelCPTS.size(); i++) {
				linePaid.add( lineLevelPaid.get(i).getText());
					
			}
			excel.setCellData(sheetName, "Line Paid", rowNum, linePaid.toString());
			
	try {		List<WebElement> lineLevelHIPA = driver.findElements(By.xpath("//table[@id='lineLevelTable']//tr/td[8]/p"));
			List<String> lineLevelHIPACode = new ArrayList<String>();
				for(int i=0; i<lineLevelHIPA.size(); i++) {
					String code=null;
					String codeDescription=null;
					code =  lineLevelHIPA.get(i).getText().split(":")[1];
					codeDescription = driver.findElement(By.xpath("//table[@id='codesTable']//td[contains(text(),'"+code+"')]/following-sibling::td[1]")).getText(); 
					lineLevelHIPACode.add(code+": "+codeDescription);
				}
				excel.setCellData(sheetName, "Line Hippa", rowNum, lineLevelHIPACode.toString());
			}catch(Exception e) {
				
			}	
	
	try {
				List<WebElement> lineLevelRemarks = driver.findElements(By.xpath("//table[@id='lineLevelTable']//tr/td[7]/p"));
				List<String> lineLevelRemarksCode = new ArrayList<String>();
					for(int i=0; i<lineLevelRemarks.size(); i++) {
						String code=null;
						String codeDescription=null;
						code =  lineLevelRemarks.get(i).getText();
						if(code.contains(", ")) {
							codeDescription = driver.findElement(By.xpath("//table[@id='codesTable']//td[contains(text(),'"+code.split(", ")[1]+"')]/following-sibling::td[1]")).getText(); 
						}
						else {
							codeDescription = driver.findElement(By.xpath("//table[@id='codesTable']//td[contains(text(),'"+code+"')]/following-sibling::td[1]")).getText(); 
						}
						
						lineLevelRemarksCode.add(code+": "+codeDescription);
						
						
					
					}
			
					excel.setCellData(sheetName, "Line Remarks", rowNum, lineLevelRemarksCode.toString());
	
	}catch(Exception e) {}
	
	
				List<String> lineLevelCopay = new ArrayList<String>();
				List<String> lineLevelDedcutible = new ArrayList<String>();
				List<String> lineLevelIneligible = new ArrayList<String>();
				List<String> lineLevelCoinsurancePaid = new ArrayList<String>();
				
				List<WebElement> lineLevelPlusIcon = driver.findElements(By.xpath("//button[@title='Toggle Row Expanded']"));
		
				try {
				for(int i=0; i<lineLevelPlusIcon.size(); i++) {
					lineLevelPlusIcon.get(i).click();
					Thread.sleep(2000);
					
					lineLevelCopay.add( driver.findElement(By.xpath("//p[text()='Copay']/following-sibling::p")).getText());
					lineLevelDedcutible.add(  driver.findElement(By.xpath("//p[text()='Deductible']/following-sibling::p")).getText());
					lineLevelIneligible.add(  driver.findElement(By.xpath("//p[text()='Ineligible']/following-sibling::p")).getText());
					lineLevelCoinsurancePaid.add(  driver.findElement(By.xpath("//p[text()='Coinsurance']/following-sibling::p")).getText());
					
					lineLevelPlusIcon.get(i).click();
						
				}		
				excel.setCellData(sheetName, "Line Copay", rowNum, lineLevelCopay.toString());
				excel.setCellData(sheetName, "Line Deductible", rowNum, lineLevelDedcutible.toString());
				excel.setCellData(sheetName, "Line Ineligible", rowNum, lineLevelIneligible.toString());
				excel.setCellData(sheetName, "Line Coinsurance", rowNum, lineLevelCoinsurancePaid.toString());
				}catch(Exception e) {
					
				}		
				
				
				*/
			
			
			 
				excel.setCellData(sheetName, "Check Number", rowNum, checkNum);
				excel.setCellData(sheetName, "Claim Number", rowNum, claimNumAvaility);
				excel.setCellData(sheetName, "Check Date", rowNum, checkDate);
				excel.setCellData(sheetName, "Payment Date", rowNum, paymentDate);
				excel.setCellData(sheetName, "Received Date", rowNum, receivedDate);
				excel.setCellData(sheetName, "Paid Amount", rowNum, paidAmount);
				excel.setCellData(sheetName, "Claim Number", rowNum, claimNumAvaility);
				excel.setCellData(sheetName, "DOS", rowNum, AvailityDOS);
				excel.setCellData(sheetName, "Denial Reason", rowNum, denialReason);
			excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
			
			}
			
			/* uncommenting this part	
			if( (!checkNum.equals("N/A")|| checkNum.isBlank()|| checkNum.isEmpty()) ) {
				
				bcbs.remittanceBtn.click();
				logger.info("Clicked on remittance button");
			
				Thread.sleep(10000);
				ArrayList<String> newTb = new ArrayList<String>(driver.getWindowHandles());
			      //switch to new tab
			      driver.switchTo().window(newTb.get(1));
			      driver.switchTo().frame("newBodyFrame");
			     // System.out.println("Page title of new tab: " + driver.getTitle());
			      //switch to parent window
			    //  driver.switchTo().window(newTb.get(0));
				
				
				try {
					bcbs.waitFunc(bcbs.closePopUp);
					bcbs.closePopUp.click();
					logger.info("Remittannce pop up closed");
				}catch(Exception e) {}
		//continue on Tuesday		
				//change tab ghere
				bcbs.waitFunc(bcbs.checkEFTTab);
				bcbs.checkEFTTab.click();
				logger.info("Clicked on claim tab");
			
				Thread.sleep(5000);
	
				sel.pauseClick(driver.findElement(By.id("checkSearchInput")), 10);
				driver.findElement(By.id("checkSearchInput")).sendKeys(checkNum+Keys.ENTER);

				
 				//driver.findElement(By.xpath("//input[contains(@id,'react-select-')]")).sendKeys("ARK"+Keys.ENTER);
				driver.findElement(By.xpath("//input[contains(@id,'organizationId')]")).sendKeys("ARK"+Keys.ENTER);
				
				driver.findElement(By.id("checkcheckDates-start")).clear();
				driver.findElement(By.id("checkcheckDates-start")).clear();
				driver.findElement(By.id("checkcheckDates-start")).sendKeys("01/01/2023" + Keys.ENTER);
				bcbs.checkSearchButton.click();
				logger.info("Clicked on Search button");
				try {
				driver.findElement(By.xpath("//div[text()='Payments issued from ']")).click();
				}catch(Exception e) {}
			try {
					
					bcbs.waitFunc(driver.findElement(By.xpath("//span[@class='icon icon-menu']")));
					logger.info("Download EOB button found in try");
				}catch(Exception e) {
					for(int i=0; i<5; i++) {
						Thread.sleep(6000);
					try {	
					if(driver.findElement(By.xpath("//span[@class='icon icon-menu']")).isDisplayed()) {
						logger.info("Download EOB button found in catch");
						break;
					}
					}catch(Exception e1) {}
				}
				}
				skipFlag=false;
				try {
					driver.findElement(By.xpath("//strong[contains(text(),\"We did not find remittance\")]")).isDisplayed();
					excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
					excel.setCellData(sheetName, "EOB Downloaded", rowNum, "No");
					excel.setCellData(sheetName, "Bot Status", rowNum, "Pass");
					driver.close();
					driver.switchTo().window(newTb.get(0));
					driver.switchTo().frame("newBodyFrame");
					skipFlag=true;
				    
					
				}catch(Exception e) {
					
				}
				if(skipFlag==true) {
					throw new SkipException("Skipping this exception, We didn't find any remits to show");
				}
				
		try {		
				driver.findElement(By.xpath("//span[@class='icon icon-menu']")).click();
				Thread.sleep(1500);
				driver.findElement(By.xpath("//button[@class='download-check-summary-single dropdown-item']")).click();
				logger.info("Download Check Summary clicked first time");
				excel.setCellData(sheetName, "EOB Downloaded", rowNum, "Yes");
				try {
					Thread.sleep(5000);
					sel.pauseClick(driver.findElement(By.xpath("//button[text()='Continue']")), 10);
					driver.findElement(By.xpath("//button[text()='Continue']")).click();
					logger.info("Confirm Large Download Request: Clicked on continue button");
					
				}catch(Exception e) {}
		}catch(Exception e) {
			excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
			excel.setCellData(sheetName, "EOB Downloaded", rowNum, "No");
			excel.setCellData(sheetName, "Bot Status", rowNum, "Pass");
			driver.close();
			driver.switchTo().window(newTb.get(0));
			driver.switchTo().frame("newBodyFrame");
			try {
			bcbs.waitFunc(bcbs.resultsTab);
			bcbs.resultsTab.click();
			logger.info("Clicked on results");
			}catch(Exception e1) {
				driver.navigate().back();
			}
			throw new SkipException("Skipping this exception,record not found");
		}
			
				try {
					driver.findElement(By.xpath("//div[contains(text(),'We are preparing')]")).isDisplayed();
					logger.info("Downloading Dialogue box displayed");
					
					}catch(Exception e2) {
						
						
						for(int i=0; i<5; i++) {

							Thread.sleep(5000);
						try {
						if(driver.findElement(By.xpath("//div[contains(text(),'We are preparing')]")).isDisplayed()){
							logger.info("Downloading Dialogue box displayed");
							break;
							}
						}catch(Exception e1) {}
					}
			
				}
				
				Thread.sleep(4000);
		//		logger.info("Download button clicked for patient with charges as "+currency);
				driver.close();
				driver.switchTo().window(newTb.get(0));
				driver.switchTo().frame("newBodyFrame");
				
				try {
					bcbs.waitFunc(bcbs.resultsTab);
					bcbs.resultsTab.click();
					logger.info("Clicked on results");
				}catch(Exception e) {}
				
			}else {
				// else of if finalized and paid
				excel.setCellData(sheetName, "EOB Downloaded", rowNum, "No");
			}
		*/	
			try {
				driver.switchTo().frame("newBodyFrame");
			}catch(Exception e) {}
			driver.findElement(By.xpath("//a[text()='Search']")).click();
			logger.info("Clicked on Search");
			excel.setCellData(sheetName, "Bot Status", rowNum, "Pass");
			
			bcbs.clearForm.click();
			logger.info("Form cleared");
			
		}
}
	
	
	
	/*
	@Test(priority=3, dependsOnMethods="AvailityPortal") 
	public void rowReset(){
		rowNum=1;
	}
	
	
	@Test(dataProvider= "getData",priority=4, dependsOnMethods={"rowReset","AvailityPortal"}) 
	public void availityToMaximus(Hashtable<String,String> data) throws InterruptedException, ParseException, IOException {
		rowNum++;
		claimNo = data.get("CLAIMS#").replace(".0", "");
		status = data.get("Bot Status");
		maximusStatus = data.get("Maximus Status");
		claimStatus = data.get("Claim Status");
checkNum = data.get("Check Number");
claimNumAvaility = data.get("Claim Number").replace(".0", "");
String DOSAvaility = data.get("DOS").replace("/", "-");
receivedDate = data.get("Received Date").replace("/", "-");
paidAmount = data.get("Paid Amount");
paymentDate = data.get("Payment Date").replace("/", "-");
checkDate = data.get("Check Date").replace("/", "-");
allowedAmount = data.get("Allowed Amount");
denialReason = data.get("Denial Reason");
finalizedDate = data.get("Finalized Date").replace("/", "-");
		DOS=data.get("SERVICE DATE").replace("/", "-");
		
		String noteBody=null;
		
		if(status.equals("Pass") && ((maximusStatus.isBlank() || maximusStatus.isEmpty())) ) {
			
		
			if(claimStatus.equals("FINALIZED")|| claimStatus.equals("PAID")) {
				noteBody = "Source: Availity Portal"
					
						+ "Claim Number: "+claimNumAvaility+""
								+ " Processed Date:"+processedDate+" "
										+ " Allowed Amount:"+allowedAmount+" "
												+ " Paid Amount: "+paidAmount+" "
														+ " PR"
														+ " Check Number: "+checkNum +""
																+ " Check Date:"+checkDate+""
																		+ "Payment Date:"+paymentDate+" ";
				logger.info("Claim is processed. EOB is downloaded. Entered");
				
			}
			else if(claimStatus.equals("PENDING")) {
				noteBody = "Source: Availity Portal "
					
						+ "Claim Number: "+claimNumAvaility+""
								+ " Allowed Amount: "+ allowedAmount+"I checked claim status from above mentioned portal and found that claim is in process on payer end. I marked this claim on follow up for 2 weeks.";
				logger.info("Claim is still in process.");
				
			}
			else if(claimStatus.equals("DENIED")) {
				noteBody= "Source: Availity Portal"
					
						+ " DOS: "+DOSAvaility +" "
								+ " Claim Number: "+claimNumAvaility+""
										+ " Processed Date: "+processedDate+" Check Number: "+checkNum +""
												+ "Check Date: "+checkDate+" ProcessedDate: "+processedDate+""
														+ " Denail Reason: "+denialReason+"Action: I checked the claim status from above mentioned portal and got to know that this claim is denied due to above mentioned reason, I downloaded the eob and placed it on shared path for posting team.";
				logger.info("Claim is still in process.");
			
			}
			else {
				noteBody = claimStatus;
				logger.info("Claim is"+claimStatus +" Entered");
			}
			
			
			String jsonBody = "{\"notes_Category_Id\":22051,\"description\":\""+noteBody+"\",\"claim_No\":"+claimNo+"}";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://maxapi.medcaremso.com/api/ClaimNotes/SaveClaimNotes"))
				.header("accept", "application/json")
				.header("accept-language", "en-US,en;q=0.9")
				.header("access-control-allow-credentials", "true")
				.header("access-control-allow-headers", "*")
				.header("access-control-allow-methods", "*")
				.header("access-control-allow-origin", "*")
				.header("authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiRW1wbG95ZWUsQWNjb3VudCBNYW5hZ2VyIiwiZnVsbG5hbWUiOiJEYW55YWwgQW1hbiwgTXVoYW1tYWQgIiwibmFtZSI6Im1kYW55YWxAbWVkY2FyZW1zby5jb20iLCJuYW1laWQiOiIxMDI4NDAiLCJQcmFjdGljZUNvZGUiOiIwIiwiUHJvdmlkZXJDb2RlIjoiMCIsIlVzZXJUeXBlIjoiIiwibmJmIjoxNzIyMzIwNzA3LCJleHAiOjE3MjI0MDcxMDcsImlhdCI6MTcyMjMyMDcwNywiaXNzIjoiaHR0cDovL3NlY3VyZWxvZ2luLm1lZGNhcmVtc28uY29tLyIsImF1ZCI6IkhuL01ITVRWSWJwMUFjcFlKWWRUbm91ZVJBTlFqQUkzb2NZSWNpYnY3NUU1czhUZmI1U2I2RlhucjZSK0liVUpYY3V3NHNvTytTT2J1RlpuRzFKK2hIbEFkMFpsOHNPNXkyTXB3U1VDaFRJPSJ9.6lHAGh2ALQqF-5F73xUdxglnbK6dAD7H5lxyr8GzCtk")
				.header("content-type", "application/json")
				.header("practicecode", "21017")
				.header("sec-ch-ua-mobile", "?0")
				.header("sec-fetch-dest", "empty")
				.header("sec-fetch-mode", "cors")
				.header("sec-fetch-site", "same-site")
				.method("POST", HttpRequest.BodyPublishers.ofString(jsonBody))				
				.build();
		HttpResponse<String> response = null;
		
			response  = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			
		System.out.println(	response.statusCode());
		
			
			
			if(	response.statusCode()==200) {
			logger.info("Data added to Maximus");
			excel.setCellData(sheetName, "Maximus Status", rowNum, "Pass");
			}
			else {
				logger.info("Data could not be added to Maximus");
				excel.setCellData(sheetName, "Maximus Status", rowNum, "Fail");
			}
	}
	
	}
	*/
	@AfterMethod()
	public void afterMethod(ITestResult result) throws IOException {

		if(!result.isSuccess()) {
			// Test Failed
			String error = result.getThrowable().getLocalizedMessage();
			logger.info(error);
			//result.getThrowable().printStackTrace();
		/*	try {
				TakesScreenshot ts = (TakesScreenshot) driver;
				File ss = ts.getScreenshotAs(OutputType.FILE);
				String ssPath = "./Screenshots/" + result.getName() + " - " + rowNum + ".png";
				FileUtils.copyFile(ss, new File(ssPath));
			} catch (Exception e) {
				System.out.println("Error taking screenshot");
			}
*/
		}
		else {
			logger.info("Test completed successfully");
		}}
	@DataProvider
	public static Object[][] getData(){


		if(excel == null){


			excel = new ExcelReader(System.getProperty("user.dir")+"\\"+excelFileName);


		}


		int rows = excel.getRowCount(sheetName);
		int cols = excel.getColumnCount(sheetName);

		Object[][] data = new Object[rows-1][1];

		Hashtable<String,String> table = null;

		for(int rowNum=2; rowNum<=rows; rowNum++){

			table = new Hashtable<String,String>();

			for(int colNum=0; colNum<cols; colNum++){

				//	data[rowNum-2][colNum]=	excel.getCellData(sheetName, colNum, rowNum);

				table.put(excel.getCellData(sheetName, colNum, 1), excel.getCellData(sheetName, colNum, rowNum));	
				data[rowNum-2][0]=table;	

			}
		}

		return data;

	}}
