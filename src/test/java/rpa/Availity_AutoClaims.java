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
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import objects.ExcelOperations;
import objects.SeleniumUtils;
import objects.Utility;
import utilities.ExcelReader;




public class Availity_AutoClaims {
	Logger logger = LogManager.getLogger(Availity_AutoClaims.class);

	String projDirPath,NPI, status, claimNo ,claimNumAvaility, AvailityDOS, denialReason,DOB ,serviceDate ,firstName, lastName,memberID, maximusStatus,DOS, claimStatus,dateofbirth, npivalue, charges,currency, error, originalTab, checkNum,checkDate,paidAmount,paymentDate, receivedDate, allowedAmount, processedDate,finalizedDate;
	
	SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yy");

	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	public static ExcelReader excel, excel1,excel2; 
	public static String sheetName = "Sheet1";
	int rowNum = 1;
	boolean skipFlag =false;
	WebDriver driver;

	//JavascriptExecutor js;
	SeleniumUtils sel;
	Utility utility;

	List<String> npiAndAddress = new ArrayList<String>();
	ExcelOperations excelFile;
	Availity_Objects bcbs;
	static String excelFileName, payer;

	@BeforeTest
	public void preRec() throws Exception {
			 ExcelDownloader downloader = new ExcelDownloader();
			downloader.downloadExcel();
			
			ExcelReader excel = new ExcelReader("Availity Report.xlsx");
			if(excel.getCellData(sheetName, 1, 1).isEmpty()){
				excel.shiftRowsUp();
			}
			
			excel.renameSheet(0,"Sheet1");
			ExcelDownloader.addHeaders("Availity Report.xlsx", 21);
			npiAndAddress = downloader.getNPIandStateofPractice();
			String state = downloader.extractStateAcronym(npiAndAddress.get(0));
			System.out.println(state);
		excel1 = new ExcelReader(System.getProperty("user.dir")+"\\Availity_Backup-Codes.xlsx");
		excel2 = new ExcelReader(System.getProperty("user.dir")+"\\AV VS MAXIMUS.xlsx");
		sel = new SeleniumUtils(projDirPath);
		int rowOfState = excel2.getCellRowNum(sheetName, "State Acronym", state);
		state = excel2.getCellData(sheetName, "State Name", rowOfState);
		
		downloader.performVLookup("Availity Report.xlsx","AV VS MAXIMUS.xlsx");
		downloader.divideExcel("Availity Report.xlsx");
		excel =  new ExcelReader(System.getProperty("user.dir")+"\\Availity 1.xlsx");
		
		driver = sel.getDriver();

		//js = (JavascriptExecutor) driver;
		bcbs= new Availity_Objects(driver);
		utility = new Utility();
		
		String[] params = new String[]{"url", "username", "password", "state","npi","excelName"};
		HashMap<String, String> configs = utility.getConfig("config.xml", params);

				String url = configs.get("url"), 
				username = configs.get("username"), 	
				password = configs.get("password");
				excelFileName = "Availity 1.xlsx";
				
		System.out.println(excelFileName);

		NPI =npiAndAddress.get(1).trim();
		
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
		bcbs.waitFunc(driver.findElement(By.xpath("//div[@class='container-fluid']//a[contains(@title,'Account')]")));
		driver.findElement(By.xpath("//div[@class='container-fluid']//a[contains(@title,'Account')]")).click();
		logger.info("Clicked on user's account");
		}catch(Exception e) {
			
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
		try {
			driver.findElement(By.xpath("//button[@id='onetrust-accept-btn-handler']")).click();
			logger.info("Clicked on accept cookies");
		}catch(Exception e) {}
		Thread.sleep(3000);
		
		try {
			Thread.sleep(3000);
			driver.findElement(By.xpath("//div[@aria-label='close']")).click();
			logger.info("Pop up closed");
		}catch(Exception e) {}
		
		driver.switchTo().frame("newBodyFrame");
		sel.pauseClick(bcbs.claimStatusTab,60);
		bcbs.waitFunc(bcbs.claimStatusTab);
		
		bcbs.claimStatusTab.click();
		logger.info("Clicked on Claim Status tab");
		
		Thread.sleep(2000);
		try {
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
		
		
		
		 

		
		
		
	}

	@Test(dataProvider= "getData") 
	public void AvailityPortal(Hashtable<String,String> data) throws InterruptedException, ParseException {
		rowNum++;
		skipFlag=false;
		boolean newInterface = false;
		String agingDay = excel.getCellData(sheetName, 0, rowNum);
		payer= data.get("Payer");
		status = data.get("Bot Status");
		Boolean agingDayPresent = false;
		if(!data.get("Aging Days").isBlank()|| !data.get("Aging Days").isEmpty()) {
		
			System.out.println(Integer.valueOf(agingDay));
			System.out.println(Integer.valueOf(agingDay)>30);
			agingDayPresent= Integer.valueOf(agingDay)>30;
		}
		
		if((status.isBlank() || status.isBlank()) && agingDayPresent && !payer.equals("No match found")) {
			
			
			try {
				driver.findElement(By.xpath("//a[text()='Search']")).click();
				logger.info("Clicked on Search Tab");
			}catch(Exception e) {
				
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
			originalTab  = driver.getWindowHandle();
			System.out.println(driver.getWindowHandle());
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
			  
			SimpleDateFormat parser = new SimpleDateFormat("M/dd/yyyy");
			// output format: yyyy-MM-dd
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			DOB = data.get("DOB");
			DOS = data.get("DOS");
			//System.out.println(formatter.format(parser.parse(DOB)) +" is date");
			DOS= formatter.format(parser.parse(DOS));
			dateofbirth=	formatter.format(parser.parse(DOB));
			firstName = data.get("Patient Name").split(",")[0].replace(" ", "").trim();
			lastName = data.get("Patient Name").split(",")[1].replace(" ", "").trim();
			memberID = data.get("Member ID");
			charges = data.get("Charges ($)").replace("$", "");
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
			
			
			bcbs.providerNpi.clear();
			bcbs.providerNpi.sendKeys(NPI);
			logger.info("Entered NPI: "+ NPI);
			
			/*
			for(int i=0; i<5; i++) {
				npivalue= bcbs.providerNpi.getAttribute("value");
				if(npivalue.isBlank() || npivalue.isEmpty()) {
				bcbs.providerNpi.sendKeys(NPI);
				logger.info("Entered NPI: "+ NPI);
				}
				else {
					bcbs.providerNpi.clear();
				}
			}
			*/
			
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
			
			
			try {
				driver.findElement(By.id("subscriberLastName")).sendKeys(lastName);
			}catch(Exception e) {
				
			}
			
			try {
				driver.findElement(By.id("subscriberFirstName")).sendKeys(firstName);
			}catch(Exception e) {
				
			}
	try {	
			bcbs.serviceDatestart.clear();
			bcbs.serviceDatestart.sendKeys(DOS.split("/")[0]+DOS.split("/")[1]+DOS.split("/")[2]);
		//	bcbs.serviceDatestart.sendKeys(Keys.ENTER);
			logger.info("DOS entered as: "+ DOS);
	}catch(Exception e) {
		driver.findElement(By.id("fromDate")).clear();
		driver.findElement(By.id("fromDate")).sendKeys(DOS.split("/")[0]+DOS.split("/")[1]+DOS.split("/")[2]);
		logger.info("DOS entered as: "+ DOS);
	}
			
	try {		bcbs.serviceDateend.clear();
			bcbs.serviceDateend.sendKeys(DOS.split("/")[0]+DOS.split("/")[1]+DOS.split("/")[2]);
		//	bcbs.serviceDateend.sendKeys(Keys.ENTER);
			logger.info("DOS entered as: "+ DOS);
	}catch(Exception e) {
		driver.findElement(By.id("toDate")).clear();
		driver.findElement(By.id("toDate")).sendKeys(DOS.split("/")[0]+DOS.split("/")[1]+DOS.split("/")[2]);
		logger.info("DOS entered as: "+ DOS);
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
			
		
			if(!payer.equals("HUMANA")) {
			
			try {
				bcbs.waitFunc(bcbs.claimStatus(firstName, lastName, memberID,currency));
				}catch(Exception e) {
					for(int i=0; i<5; i++) {
						Thread.sleep(4000);
					try {
							bcbs.claimStatus(firstName, lastName, memberID,currency).isDisplayed();
						break;
					}catch(Exception e1) {}	
				}
					
				}
			
		try {	
			claimStatus= bcbs.claimStatus(firstName, lastName, memberID,currency).getText();
			excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
		}catch(Exception e) {
			try {
				
				driver.findElements(By.xpath("//span[text()='Billed']/parent::span/parent::div/following-sibling::div/span[text()='"+currency+"']")).get(0).click();
				logger.info("Clicked on first claim with currency match");
			Thread.sleep(1000);
				claimStatus = driver.findElements(By.xpath("//span[text()='Billed']/parent::span/parent::div/following-sibling::div/span[text()='"+currency+"']/parent::div/parent::div/preceding-sibling::div/div/span/span[text()='Status']/parent::span/parent::div/following-sibling::div/span")).get(0).getText();
			
				newInterface = driver.findElement(By.xpath("//span[text()='Member ID']/parent::span/parent::div/following-sibling::div/span[text()='"+memberID+"']")).isDisplayed();
			
				checkNum= driver.findElement(By.xpath("//span[text()='Check Number']/parent::span/parent::div/following-sibling::div/span")).getText();
				
			}catch(Exception e1) {
			
				try {
					
					driver.findElements(By.xpath("//table[@id='claimsTable']/descendant::td[contains(text(),'LOUDY, JESSICA')]/following-sibling::td/following-sibling::td[text()='$264.86']/preceding-sibling::td/span")).get(0).click();
					claimStatus=	driver.findElements(By.xpath("//table[@id='claimsTable']/descendant::td[contains(text(),'LOUDY, JESSICA')]/following-sibling::td/following-sibling::td[text()='$264.86']/preceding-sibling::td/span")).get(0).getText();
					
					
				}catch(Exception e2) {
					excel.setCellData(sheetName, "Bot Status", rowNum, "member Id, name or charge mismatch");
					throw new SkipException("Skipping this exception, member Id, name or charge mismatch");
				}
				
				
			}
			
		}
		
			}else if (payer.equals("HUMANA")){
				
				
				claimNumAvaility = driver.findElement(By.xpath("//div[@data-testid='results-header']//span[contains(text(),'Claim')]")).getText().split("Claim ")[1];
				logger.info("Claim number in availity app is "+ claimNumAvaility);
				
				AvailityDOS = driver.findElement(By.xpath("//div[@data-testid='results-header']//span[contains(text(),'Dates of Service')]/parent::span/parent::div/following-sibling::div/span")).getText();
				logger.info("Availity DOS  in availity app is "+ AvailityDOS);

			
				paidAmount = driver.findElement(By.xpath("//div[@data-testid='results-header']//span[contains(text(),'Paid')]/parent::span/parent::div/following-sibling::div/span")).getText();
		
				logger.info("Paid amount is "+ paidAmount);
				
				claimStatus = driver.findElement(By.xpath("//div[@data-testid='results-header']//span[contains(text(),'Status')]/parent::span/parent::div/following-sibling::div/span")).getText();
				
				
				logger.info(claimStatus);
				
				
				processedDate = driver.findElement(By.xpath("//div[@data-testid='results-header']//span[contains(text(),'Processed Date')]/parent::span/parent::div/following-sibling::div/span")).getText();
				
				
				logger.info("Processed Date is: "+ processedDate);
				
				
				checkDate = driver.findElement(By.xpath("//div[@data-testid='resultsSummary']//span[contains(text(),'Check Date')]/parent::span/parent::div/following-sibling::div/span")).getText();
				logger.info("Check date is: "+checkDate);
				checkNum = driver.findElement(By.xpath("//div[@data-testid='resultsSummary']//span[contains(text(),'Check Number')]/parent::span/parent::div/following-sibling::div/span")).getText();
				logger.info("Check number is: "+checkNum);
				
				if(checkNum.isBlank()||checkNum.isEmpty() || checkNum.equals("N/A")) {
					excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
					excel.setCellData(sheetName, "Bot Status", rowNum, "Pass");
					excel.setCellData(sheetName, "Check Number", rowNum, "0000000");
					checkNum="0000000";
					driver.navigate().back();

					throw new SkipException("Skipping this exception, Check number is null");
				
				}else {
				
				}
			}
			
			
			
			logger.info(claimStatus);
			if((claimStatus.equals("FINALIZED")|| claimStatus.equals("PAID")) && !payer.equals("HUMANA") && (newInterface==false)) {
				
				try {
				bcbs.claimStatus(firstName, lastName, memberID,currency).click();
				logger.info("Clicked on the claim status");
				}catch(Exception e) {}
				
				try {
					bcbs.waitFunc(bcbs.claimNumber);
					}catch(Exception e) {
						for(int i=0; i<5; i++) {
							Thread.sleep(4000);
						try { if(bcbs.claimNumber.isDisplayed()) {
							break;
						}
						}catch(Exception e1) {}
					}
					}
				//Paid = Finalized
				claimNumAvaility= 	bcbs.claimNumber.getText();
				logger.info("Claim number in availity app is "+ claimNumAvaility);
				try {
				checkNum = driver.findElement(By.xpath("//div[@id='Check Number']/p[2]")).getText(); //new change
				}catch(Exception e) {}
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
				
				excel.setCellData(sheetName, "Check Number", rowNum, checkNum);
				excel.setCellData(sheetName, "Claim Number", rowNum, claimNumAvaility);
				excel.setCellData(sheetName, "Check Date", rowNum, checkDate);
				excel.setCellData(sheetName, "Payment Date", rowNum, paymentDate);
				excel.setCellData(sheetName, "Received Date", rowNum, receivedDate);
				excel.setCellData(sheetName, "Paid Amount", rowNum, paidAmount);
				excel.setCellData(sheetName, "Claim Number", rowNum, claimNumAvaility);
				excel.setCellData(sheetName, "Availity DOS", rowNum, AvailityDOS);
				excel.setCellData(sheetName, "Denial Reason", rowNum, denialReason);
			excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
			if(checkNum.isBlank()||checkNum.isEmpty()) {
				excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
				excel.setCellData(sheetName, "Bot Status", rowNum, "Pass");
				excel.setCellData(sheetName, "Check Number", rowNum, "0000000");
				excel.setCellData(sheetName, "EOB Downloaded", rowNum, "No");
				checkNum="0000000";
				bcbs.waitFunc(bcbs.resultsTab);
				bcbs.resultsTab.click();
				logger.info("Clicked on results");

				throw new SkipException("Skipping this exception, Check number is null");
			
			}
			}
			
			
	/*		if((claimStatus.equals("FINALIZED")|| claimStatus.equals("PAID")) &&(!checkNum.equals("N/A")|| checkNum.isBlank()|| checkNum.isEmpty()) ) {
				
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
			
		}else {
			excel.setCellData(sheetName, "Bot Status", rowNum, "Can not be processed");
		}
}
	
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
String DOSAvaility = data.get("Availity DOS").replace("/", "-");
receivedDate = data.get("Received Date").replace("/", "-");
paidAmount = data.get("Paid Amount");
paymentDate = data.get("Payment Date").replace("/", "-");
checkDate = data.get("Check Date").replace("/", "-");
allowedAmount = data.get("Allowed Amount");
denialReason = data.get("Denial Reason");
finalizedDate = data.get("Finalized Date").replace("/", "-");
		DOS=data.get("DOS").replace("/", "-");
		
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
	
	@AfterMethod()
	public void afterMethod(ITestResult result) throws IOException {

		if(!result.isSuccess()) {
			// Test Failed
			String error = result.getThrowable().getLocalizedMessage();
			logger.info(error);
			//result.getThrowable().printStackTrace();
			try {
				TakesScreenshot ts = (TakesScreenshot) driver;
				File ss = ts.getScreenshotAs(OutputType.FILE);
				String ssPath = "./Screenshots/" + result.getName() + " - " + rowNum + ".png";
				FileUtils.copyFile(ss, new File(ssPath));
			} catch (Exception e) {
				System.out.println("Error taking screenshot");
			}

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