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





public class Availity_Followup {
	Logger logger = LogManager.getLogger(Availity_Followup.class);

	String projDirPath, status, claimNo ,claimNumAvaility, DOB ,serviceDate ,firstName, lastName,memberID,ecwStatus,DOS, claimStatus,dateofbirth, npivalue, charges,currency, error, originalTab;
	
	SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yy");

	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	public static ExcelReader excel; 
	public static String sheetName = "Sheet1";
	int rowNum = 1;
	boolean skipFlag =false;
	WebDriver driver;

	//JavascriptExecutor js;
	SeleniumUtils sel;
	Utility utility;

	ExcelOperations excelFile;
	Availity_Objects bcbs;
	static String excelFileName;

	@BeforeTest
	public void preRec() throws InterruptedException, SAXException, IOException, ParserConfigurationException {

		sel = new SeleniumUtils(projDirPath);

		driver = sel.getDriver();

		//js = (JavascriptExecutor) driver;
		bcbs= new Availity_Objects(driver);
		utility = new Utility();


		String[] params = new String[]{"url", "username", "password", "state","payer","excelName"};
		HashMap<String, String> configs = utility.getConfig("config.xml", params);

		String url = configs.get("url"), 
				username = configs.get("username"), 
				state = configs.get("state"),
				payer = configs.get("payer"),
				password = configs.get("password");

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
	
		bcbs.organizationInput.sendKeys(Keys.ENTER);
		logger.info("Entered Organization: ARK Laboratory LLC");
		 

		bcbs.payerInput.clear();
		bcbs.payerInput.sendKeys(payer);
		Thread.sleep(1000);
		bcbs.payerInput.sendKeys(Keys.ENTER);
		bcbs.payerInput.sendKeys(Keys.SPACE);
		logger.info("Entered Payer: "+payer);
		
		Thread.sleep(3000);
		bcbs.waitFunc(bcbs.hipaaTab);
		bcbs.hipaaTab.click();
		logger.info("Clicked on HIPAA tab");
		originalTab  = driver.getWindowHandle();
		System.out.println(driver.getWindowHandle());
	}

	@Test(dataProvider= "getData") 
	public void AvailityPortal(Hashtable<String,String> data) throws InterruptedException, ParseException {
		rowNum++;
		skipFlag=false;
		status = data.get("Final Status");
		
		if(status.isBlank() || status.isBlank()) {
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
			bcbs.clearForm.click();
			  
			SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yy");
			// output format: yyyy-MM-dd
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			DOB = data.get("Date of Birth");
			DOS = data.get("Service Date");
			//System.out.println(formatter.format(parser.parse(DOB)) +" is date");
			DOS= formatter.format(parser.parse(DOS));
			dateofbirth=	formatter.format(parser.parse(DOB));
			firstName = data.get("First Name").toUpperCase().trim();
			lastName = data.get("Last Name").toUpperCase().trim();
			memberID = data.get("Member ID");
			charges = data.get("Billed Charge").replace("$", "");
			double balanceDouble = Double.parseDouble(charges); 
			 NumberFormat currencyformatter=NumberFormat.getCurrencyInstance(Locale.US);  
			  currency=currencyformatter.format(balanceDouble);
			System.out.println(currency);
			logger.info("Last name is "+lastName);
			logger.info("First name is "+firstName);
			logger.info("DOB is "+dateofbirth);
			
			
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
				bcbs.providerNpi.sendKeys("1700120938");
				logger.info("Entered NPI: 1700120938");
				}
				else {
					bcbs.providerNpi.clear();
				}
			}
			
			
			bcbs.waitFunc(bcbs.memberIDInput); 
			bcbs.memberIDInput.clear();
			bcbs.memberIDInput.sendKeys(memberID);
			logger.info("Entered member ID: "+ memberID);
			
			bcbs.patientLastName.clear();
			bcbs.patientLastName.sendKeys(lastName);
			logger.info("Entered last Name: "+ lastName);
			
			bcbs.patientFirstName.clear();
			bcbs.patientFirstName.sendKeys(firstName);
			logger.info("Entered first Name: "+ firstName);
			
			bcbs.patientBirthDate.clear();
			bcbs.patientBirthDate.sendKeys(dateofbirth);
			logger.info("DOB entered as: "+ dateofbirth);
			
			bcbs.serviceDatestart.clear();
			bcbs.serviceDatestart.sendKeys(DOS);
		//	bcbs.serviceDatestart.sendKeys(Keys.ENTER);
			logger.info("DOS entered as: "+ DOS);
			
			bcbs.serviceDateend.clear();
			bcbs.serviceDateend.sendKeys(DOS);
		//	bcbs.serviceDateend.sendKeys(Keys.ENTER);
			logger.info("DOS entered as: "+ DOS);
			bcbs.claimAmountInput.click();
			
			bcbs.submitBtn.click();
			logger.info("Clicked on Submit button");
			
			sel.pauseClick(bcbs.transactionIDLogo, 15);
			Thread.sleep(5000);
			
			try {
				bcbs.transactionIDLogo.isDisplayed();
			}catch(Exception e) {
				excel.setCellData(sheetName, "Final Status", rowNum, "Data error");
				throw new SkipException("Skipping this exception, Data error");
			}
			
			
			try {
				bcbs.waitFunc(driver.findElement(By.xpath("//div[1][@role='alert']/ul/li")));
				driver.findElement(By.xpath("//div[1][@role='alert']/ul/li")).isDisplayed();
				error=driver.findElement(By.xpath("//div[1][@role='alert']/ul/li")).getText();
				bcbs.clearForm.click();
				logger.info("Form cleared");
		
				
				
			}catch(Exception e) {
				
			}
			if(	skipFlag==true) {
				excel.setCellData(sheetName, "Final Status", rowNum, error);
				throw new SkipException("Skipping this exception, "+error);
			}
			driver.switchTo().defaultContent();
			driver.switchTo().frame("newBody");
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
		}catch(Exception e) {
			excel.setCellData(sheetName, "Final Status", rowNum, "member Id, name or charge mismatch");
			throw new SkipException("Skipping this exception, member Id, name or charge mismatch");
		}
			
			logger.info(claimStatus);
			
			if(claimStatus.equals("FINALIZED")) {
				
				bcbs.claimStatus(firstName, lastName, memberID,currency).click();
				logger.info("Clicked on the claim status");
				
				
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
				
				claimNumAvaility= 	bcbs.claimNumber.getText();
				logger.info("Claim number in availity app is "+ claimNumAvaility);
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
				
				//change tab ghere
				bcbs.waitFunc(bcbs.claimTab);
				bcbs.claimTab.click();
				logger.info("Clicked on claim tab");
				
				bcbs.claimSearchInput.sendKeys(claimNumAvaility);
				bcbs.claimSearchButton.click();
				logger.info("Clicked on Search button");
				try {
					
					bcbs.waitFunc(bcbs.downloadEOB(firstName, lastName, currency));
					logger.info("Download EOB button found in try");
				}catch(Exception e) {
					for(int i=0; i<5; i++) {
						Thread.sleep(6000);
					try {	
					if(bcbs.downloadEOB(firstName, lastName, currency).isDisplayed()) {
						logger.info("Download EOB button found in catch");
						break;
					}
					}catch(Exception e1) {}
				}
				}
				skipFlag=false;
				try {
					driver.findElement(By.xpath("//strong[contains(text(),\"We didn't find any remits to show\")]")).isDisplayed();
					excel.setCellData(sheetName, "Claim Status", rowNum, "FINALIZED + We didn't find any remits to show");
					excel.setCellData(sheetName, "Final Status", rowNum, "Check");
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
				bcbs.downloadEOB(firstName, lastName, currency).click();
				logger.info("Download EOB clicked first time");
		}catch(Exception e) {
			excel.setCellData(sheetName, "Claim Status", rowNum, "FINALIZED + record not found");
			excel.setCellData(sheetName, "Final Status", rowNum, "Check");
			driver.close();
			driver.switchTo().window(newTb.get(0));
			driver.switchTo().frame("newBodyFrame");
			throw new SkipException("Skipping this exception,record not found");
		}
				try {
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
				}
				try {
					driver.findElement(By.xpath("//label[contains(text(),'Auto Downloading')]")).isDisplayed();
					logger.info("Auto Downloading Dialogue box displayed");
					
					}catch(Exception e2) {
						
						
						for(int i=0; i<5; i++) {

							Thread.sleep(5000);
						try {
						if(driver.findElement(By.xpath("//label[contains(text(),'Auto Downloading')]")).isDisplayed()){
							logger.info("Auto Downloading Dialogue box displayed");
							break;
							}
						}catch(Exception e1) {}
					}
			
				}
				
				Thread.sleep(4000);
				logger.info("Download button clicked for patient with charges as "+currency);
				driver.close();
				driver.switchTo().window(newTb.get(0));
				driver.switchTo().frame("newBodyFrame");
				
				try {
					bcbs.waitFunc(bcbs.resultsTab);
					bcbs.resultsTab.click();
					logger.info("Clicked on results");
				}catch(Exception e) {}
				
			}else {
				
			}
			
			excel.setCellData(sheetName, "Claim Status", rowNum, claimStatus);
			excel.setCellData(sheetName, "Final Status", rowNum, "Pass");
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
		bcbs.passwordFieldECW.sendKeys("!Ndian@193");
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
		status = data.get("Final Status");
		ecwStatus = data.get("ECW Status");
		claimStatus = data.get("Claim Status");

		if(status.equals("Pass")&& (ecwStatus.isBlank() || ecwStatus.isEmpty()) ) {
			
			sel.pauseClick(bcbs.claimLookupInputECW, 30);
			claimNo = data.get("Claim No").replace(".0", "");
			bcbs.claimLookupInputECW.clear();
			bcbs.claimLookupInputECW.sendKeys(claimNo);
			logger.info("Claim no entered as :"+ claimNo);
			bcbs.claimLookupBtnECW.click();
			logger.info("Clicked on look up button");
			
			sel.pauseClick(bcbs.saveClaimBtnECW, 50);
			sel.pauseClick(bcbs.followUpArrowECW, 50);
			
			try {
				if(!bcbs.claimNotesECW.isDisplayed()) {
					bcbs.followUpArrowECW.click();
					logger.info("Clicked on follow up arrows");
				}
				
			}catch(Exception e) {
				
			}
			sel.pauseClick(bcbs.claimNotesECW, 15);
			Select select = new Select(driver.findElement(By.xpath("//select[contains(@id,'claimStatusSel')]")));
			if(claimStatus.equals("FINALIZED")) {
				bcbs.claimNotesECW.sendKeys("Claim is processed. EOB is downloaded.");
				logger.info("Claim is processed. EOB is downloaded. Entered");
				select.selectByVisibleText("Insurance Accepted");
			}
			else if(claimStatus.equals("PENDING")) {
				bcbs.claimNotesECW.sendKeys("Claim is still in process.");
				logger.info("Claim is still in process.");
				select.selectByVisibleText("Medcare - In Process Claims");
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
