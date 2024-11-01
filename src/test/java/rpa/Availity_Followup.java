package rpa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

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
import org.openqa.selenium.support.ui.Select;
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




public class Availity_Followup {
	Logger logger = LogManager.getLogger(Availity_Followup.class);

	String projDirPath, NPI,status, claimNo ,claimNumAvaility, AvailityDOS, denialReason,DOB ,serviceDate ,firstName, lastName,memberID,ecwStatus,DOS, claimStatus,dateofbirth, npivalue, charges,currency, error, originalTab, checkNum,checkDate,paidAmount,paymentDate, receivedDate, allowedAmount, processedDate,finalizedDate;
	
	SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yy");

	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	public static ExcelReader excel,excel1; 
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
		excel1 = new ExcelReader(System.getProperty("user.dir")+"\\Availity_Backup-Codes.xlsx");
		sel = new SeleniumUtils(projDirPath);

		driver = sel.getDriver();

		//js = (JavascriptExecutor) driver;
		bcbs= new Availity_Objects(driver);
		utility = new Utility();
		
		String[] params = new String[]{"url", "username", "password", "state","npi","excelName"};
		HashMap<String, String> configs = utility.getConfig("config.xml", params);

		String url = configs.get("url"), 
				username = configs.get("username"), 
				state = configs.get("state"),
				password = configs.get("password");
		NPI = configs.get("npi");
		excelFileName = configs.get("excelName");
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
		
		Thread.sleep(5000);

		try {
		sel.pauseClick(driver.findElement(By.xpath("//input[@id='elect-Enter a one-time use backup code-1-radio']")), 10);
		driver.findElement(By.xpath("//input[@id='elect-Enter a one-time use backup code-1-radio']")).click();
		logger.info("Clicked on enter backup codes");
		}catch(Exception e) {}
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
		bcbs.waitFunc(driver.findElement(By.xpath("//a[@title=\"Jim's Account\"]")));
		driver.findElement(By.xpath("//a[@title=\"Jim's Account\"]")).click();
		logger.info("Clicked on Jim account");
		}catch(Exception e) {
			
				for(int i=0; i<5; i++) {
					Thread.sleep(4000);
				try{ 
					if(driver.findElement(By.xpath("//a[@title=\"Jim's Account\"]")).isDisplayed()) 
					{
					bcbs.waitFunc(driver.findElement(By.xpath("//a[@title=\"Jim's Account\"]")));
					driver.findElement(By.xpath("//a[@title=\"Jim's Account\"]")).click();
					logger.info("Clicked on Jim account");
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
		driver.switchTo().frame("newBodyFrame");
		
		
		
		
		
		
		try {
			bcbs.waitFunc(driver.findElement(By.xpath("//button[@id='security']")));
			driver.findElement(By.xpath("//button[@id='security']")).click();
			logger.info("Clicked on security tab");
			}catch(Exception e) {
				
					for(int i=0; i<5; i++) {
						Thread.sleep(4000);
					try{ 
						if(driver.findElement(By.xpath("//button[@id='security']")).isDisplayed()) 
						{
						bcbs.waitFunc(driver.findElement(By.xpath("//button[@id='security']")));
						driver.findElement(By.xpath("//button[@id='security']")).click();
						logger.info("Clicked on security tab");
						break;
						}
						}catch(Exception e1) {}
				}
			}
			
		
		
		
		
		
		
		
		
		
		
		Thread.sleep(3000);
		
		driver.switchTo().defaultContent();
		driver.findElement(By.id("onetrust-accept-btn-handler")).click();
		logger.info("Clicked on Accept Cookies");
		driver.switchTo().frame("newBodyFrame");
		
		bcbs.waitFunc(driver.findElement(By.xpath("//button[text()='Update 2-Step Authentication']")));
		driver.findElement(By.xpath("//button[text()='Update 2-Step Authentication']")).click();
		logger.info("Clicked on 2-Step Authentication");
		
		Thread.sleep(3000);
		bcbs.waitFunc(driver.findElement(By.xpath("//button[text()='Show backup codes']")));
		driver.findElement(By.xpath("//button[text()='Show backup codes']")).click();
		logger.info("Clicked on show backup codes");
		
		Thread.sleep(4000);
		bcbs.waitFunc(driver.findElements(By.xpath("//div[@class='text-center list card-body']")).get(0));
		String ExtractedbackupCode = driver.findElements(By.xpath("//div[@class='text-center list card-body']")).get(0).getText();
		logger.info("Extracted backup code saved  is "+ExtractedbackupCode);
		
		excel1.setCellData(sheetName, "Code", 2, ExtractedbackupCode);
		
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
	
		bcbs.organizationInput.sendKeys("ARK Laboratory LLC"+Keys.ENTER);
		logger.info("Entered Organization: ARK Laboratory LLC");
		
		
		
		 

		
		
		
	}

	@Test(dataProvider= "getData") 
	public void AvailityPortal(Hashtable<String,String> data) throws InterruptedException, ParseException {
		rowNum++;
		skipFlag=false;
		boolean newInterface = false;
		status = data.get("Bot Status");
	
		if(status.isBlank() || status.isBlank()) {
			
			
			try {
				driver.findElement(By.xpath("//a[text()='Search']")).click();
				logger.info("Clicked on Search Tab");
			}catch(Exception e) {
				
			}
			payer= data.get("Transaction Payer");
			bcbs.payerInput.clear();
			bcbs.payerInput.sendKeys(payer);
			Thread.sleep(1000);
			bcbs.payerInput.sendKeys(Keys.ENTER);
		//	bcbs.payerInput.sendKeys(Keys.SPACE);
			logger.info("Entered Payer: "+payer);
			Thread.sleep(5000);
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
			  
			SimpleDateFormat parser = new SimpleDateFormat("M/dd/yy");
			// output format: yyyy-MM-dd
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			DOB = data.get("Date of Birth");
			DOS = data.get("SERVICE DATE");
			//System.out.println(formatter.format(parser.parse(DOB)) +" is date");
			DOS= formatter.format(parser.parse(DOS));
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
			bcbs.serviceDatestart.sendKeys(DOS);
		//	bcbs.serviceDatestart.sendKeys(Keys.ENTER);
			logger.info("DOS entered as: "+ DOS);
	}catch(Exception e) {
		driver.findElement(By.id("fromDate")).clear();
		driver.findElement(By.id("fromDate")).sendKeys(DOS);
		logger.info("DOS entered as: "+ DOS);
	}
			
	try {		bcbs.serviceDateend.clear();
			bcbs.serviceDateend.sendKeys(DOS);
		//	bcbs.serviceDateend.sendKeys(Keys.ENTER);
			logger.info("DOS entered as: "+ DOS);
	}catch(Exception e) {
		driver.findElement(By.id("toDate")).clear();
		driver.findElement(By.id("toDate")).sendKeys(DOS);
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
				bcbs.claimStatus(firstName, lastName,currency).click();
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
			
			
			
		
				//Paid = Finalized
				
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
			
			if((claimStatus.equals("FINALIZED")|| claimStatus.equals("PAID")) &&(!checkNum.equals("N/A")|| checkNum.isBlank()|| checkNum.isEmpty()) ) {
				driver.switchTo().defaultContent();
				driver.switchTo().frame("newBodyFrame");
				bcbs.remittanceBtn.click();
				logger.info("Clicked on remittance button");
		/*		
				ArrayList<String> windowTabs = new ArrayList<String>(driver.getWindowHandles());
				for(String str: windowTabs) {
					if(!str.equals(originalTab)) {
						driver.switchTo().window(tabs.get(0))
					}
				}
		*/	
				
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
	/*
	
				driver.findElement(By.xpath("//a[text()='Claims & Payments']")).click();
				logger.info("Clicked on 'Claims & Payments'");
				sel.pauseClick(driver.findElement(By.xpath("//div[text()='Remittance Viewer']")), 10);
				driver.findElement(By.xpath("//div[text()='Remittance Viewer']")).click();
				Thread.sleep(5000);
				sel.pauseClick(driver.findElement(By.xpath("//a[text()='Remittance Viewer']")), 10);
				driver.findElement(By.xpath("//a[text()='Remittance Viewer']")).click();
				bcbs.claimSearchInput.sendKeys(checkNum);
				 */
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
			/*	try {
					bcbs.waitFunc(driver.findElement(By.xpath("//h3[contains(text(),'EOP/EOB Downloads')]")));
				}catch(Exception e) {
					
					for(int i=0; i<5; i++) {
						
						bcbs.downloadEOB(firstName, lastName, currency).click();
						Thread.sleep(5000);
					try {
					if(driver.findElement(By.xpath("//h3[contains(text(),'EOP/EOB Downloads')]")).isDisplayed()){
						logger.info("Dialogue box for downloads opened");
						break;}
					}catch(Exception e1) {}
						
				}
				}*/
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
	
	
	@Test(priority=3, dependsOnMethods="AvailityPortal") 
	public void ecwLogin(){
		
		rowNum = 1;
		driver.get("https://azuarq3ezwcrczrn8xapp.ecwcloud.com/mobiledoc/jsp/webemr/login/newLogin.jsp#/mobiledoc/jsp/webemr/webpm/claimLookup.jsp");
		logger.info("Open url: https://azuarq3ezwcrczrn8xapp.ecwcloud.com/mobiledoc/jsp/webemr/login/newLogin.jsp#/mobiledoc/jsp/webemr/webpm/claimLookup.jsp");
		bcbs.waitFunc(bcbs.usernameFieldECW);
		
		bcbs.usernameFieldECW.clear();
		bcbs.usernameFieldECW.sendKeys("jimmartin");
		logger.info("Enter username: jimmartin");

		bcbs.nextBtnECW.click();
		logger.info("Click next button");

		sel.pauseClick(bcbs.loginBtnECW,10);
		
		bcbs.passwordFieldECW.clear();
		bcbs.passwordFieldECW.sendKeys("!Ndian@195");
		logger.info("Enter password");

		bcbs.loginBtnECW.click();
		logger.info("Clicked on login");
		
		sel.pauseClick(bcbs.patientLookupBtnECW,150);
		


		bcbs.expandMenubtnECW.click();
		logger.info("Click expand menu button");

		bcbs.billingTabECW.click();
		logger.info("Click Billing Tab");

		bcbs.claimsMenuECW.click();
		logger.info("Click Claims");
		
	}
	@Test(dataProvider= "getData",priority=4, dependsOnMethods={"ecwLogin","AvailityPortal"}) 
	public void ASRtoECWCase(Hashtable<String,String> data) throws InterruptedException, ParseException, IOException {
		rowNum++;
		status = data.get("Bot Status");
		ecwStatus = data.get("ECW Status");
		claimStatus = data.get("Claim Status");
checkNum = data.get("Check Number");
claimNumAvaility = data.get("Claim Number");
String DOSAvaility = data.get("DOS");
receivedDate = data.get("Received Date");
paidAmount = data.get("Paid Amount");
paymentDate = data.get("Payment Date");
checkDate = data.get("Check Date");
allowedAmount = data.get("Allowed Amount");
denialReason = data.get("Denial Reason");
finalizedDate = data.get("Finalized Date");
		DOS=data.get("SERVICE DATE");
		if(status.equals("Pass") && ((ecwStatus.isBlank() || ecwStatus.isEmpty())) ) {
			
			sel.pauseClick(bcbs.claimLookupInputECW, 30);
			claimNo = data.get("CLAIMS#").replace(".0", "");
			bcbs.claimLookupInputECW.clear();
			bcbs.claimLookupInputECW.sendKeys(claimNo);
			logger.info("Claim no entered as :"+ claimNo);
			bcbs.claimLookupBtnECW.click();
			logger.info("Clicked on look up button");
			Thread.sleep(8000);
			try {
				
				sel.pauseClick(driver.findElement(By.xpath("//div[contains(text(),'More than one fee schedule is selected')]/parent::div/following-sibling::div/button[text()='OK']")), 20);
			}
			catch(Exception e) {}
			try {
			bcbs.waitFunc(driver.findElement(By.xpath("//div[contains(text(),'More than one fee schedule is selected')]/parent::div/following-sibling::div/button[text()='OK']")));
			sel.pauseClick(driver.findElement(By.xpath("//div[contains(text(),'More than one fee schedule is selected')]/parent::div/following-sibling::div/button[text()='OK']")), 20);
			driver.findElement(By.xpath("//div[contains(text(),'More than one fee schedule is selected')]/parent::div/following-sibling::div/button[text()='OK']")).click();
			logger.info("Clicked on OK button");
			}catch(Exception e) {}
			
			try {
				sel.pauseClick(bcbs.saveClaimBtnECW, 15);
			driver.findElement(By.xpath("//div[@class='billing-right-toggle claimRightPanel-tog active']")).isDisplayed();
			}catch(Exception e) {
				sel.pauseClick(bcbs.followUpArrowECW, 20);
			}
			
			
		
			
			try {
				if(!bcbs.claimNotesECW.isDisplayed()) {
					bcbs.followUpArrowECW.click();
					logger.info("Clicked on follow up arrows");
				}
				
			}catch(Exception e) {
				
			}
			sel.pauseClick(bcbs.claimNotesECW, 15);
			Select select = new Select(driver.findElement(By.xpath("//select[contains(@id,'claimStatusSel')]")));
			if(claimStatus.equals("FINALIZED")|| claimStatus.equals("PAID")) {
				bcbs.claimNotesECW.sendKeys("Source: Availity Portal \n Link: https://apps.availity.com/ \n Claim Number: "+claimNumAvaility+"\n Processed Date:"+processedDate+" \n Allowed Amount:"+allowedAmount+" \n Paid Amount: "+paidAmount+" \n PR\n Check Number: "+checkNum +"\n Check Date:"+checkDate+"\nPayment Date:"+paymentDate+" ");
				logger.info("Claim is processed. EOB is downloaded. Entered");
				select.selectByVisibleText("Insurance Accepted");
			}
			else if(claimStatus.equals("PENDING")) {
				bcbs.claimNotesECW.sendKeys("Source: Availity Portal \n Source: Portal\nLink: https://apps.availity.com/ \n Claim Number: "+claimNumAvaility+"\n  Allowed Amount: "+ allowedAmount+"I checked claim status from above mentioned portal and found that claim is in process on payer end. I marked this claim on follow up for 2 weeks.");
				logger.info("Claim is still in process.");
				select.selectByVisibleText("Medcare - In Process Claims");
			}
			else if(claimStatus.equals("DENIED")) {
				bcbs.claimNotesECW.sendKeys("Source: Availity Portal \n Link: https://apps.availity.com/ \n DOS: "+DOSAvaility +" \n Claim Number: "+claimNumAvaility+"\n Processed Date: "+processedDate+" Check Number: "+checkNum +"\n Check Date: "+checkDate+" \nProcessedDate: "+processedDate+"\n Denail Reason: "+denialReason+"Action: I checked the claim status from above mentioned portal and got to know that this claim is denied due to above mentioned reason, I downloaded the eob and placed it on shared path for posting team.");
				logger.info("Claim is still in process.");
				select.selectByVisibleText("ERA PAYER DENIED");
			}
			else {
				bcbs.claimNotesECW.sendKeys(claimStatus);
				logger.info("Claim is"+claimStatus +" Entered");
			}
			
			
			
			
			
			bcbs.saveClaimBtnECW.click();
			logger.info("Save button clicked");
			excel.setCellData(sheetName, "ECW Status", rowNum, "Pass");
			
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