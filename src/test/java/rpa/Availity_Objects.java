package rpa;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Availity_Objects {

	WebDriver driver;
//	private WebDriverWait wait10, wait20;
	private WebDriverWait wait;
	public Availity_Objects(WebDriver driver) {
		super();
		this.driver = driver;
		wait = new WebDriverWait(driver,Duration.ofSeconds(10));
		PageFactory.initElements(driver, this);
	}
	
	
	
	@FindBy(xpath = "//input[@name='userId']")
	public WebElement usernameField;
	
	@FindBy(xpath = "//input[@name='password']")
	public WebElement passwordField;
	
	@FindBy(xpath = "//button[text()='Sign In']")
	public WebElement loginBtn;
	
	@FindBy(xpath = "//div[@class='av-large-logo']")
	public WebElement availityLogo;
	
	@FindBy(xpath = "//h3[text()='Claim Status']")
	public WebElement claimStatusTab;
	
	@FindBy(xpath = "//span[@id='select2-chosen-1']/following-sibling::span/b[@role='presentation']")
	public WebElement downArrowDrpDwn;
	
	@FindBy(id = "s2id_autogen1_search")
	public WebElement stateInput;
	
	@FindBy(xpath = "//div/span[text()='Michigan']")
	public WebElement michiganState;
	
	public WebElement getState(String state) {
		return driver.findElement(By.xpath("//div/span[text()='"+state+"']"));
	}
	
	@FindBy(id = "organization")
	public WebElement organizationInput;
	
	@FindBy(id = "payer")
	public WebElement payerInput;
	
	@FindBy(id = "HIPAA Standard")
	public WebElement hipaaTab;
	
	@FindBy(id = "patientMemberId")
	public WebElement memberIDInput;
	
	
	
	
	
	
	@FindBy(id = "patientLastName")
	public WebElement patientLastName;
	
	@FindBy(id = "patientFirstName")
	public WebElement patientFirstName;
	
	@FindBy(id = "patientBirthDate")
	public WebElement patientBirthDate;
	
	@FindBy(id = "serviceDates-start")
	public WebElement serviceDatestart;
	
	@FindBy(id = "serviceDates-end")
	public WebElement serviceDateend;
	
	@FindBy(id = "submit-by276")
	public WebElement submitBtn;
	
	@FindBy(id = "providerNpi")
	public WebElement providerNpi;
	
	
	@FindBy(xpath = "//table[@id='claimsTable']/descendant::td[contains(text(),'ADAMS, ELLERY')]/following-sibling::td[text()='MIJ892008138']/preceding-sibling::td[4]/span")
	public WebElement claimStatus;
	
	public WebElement claimStatus(String firstname, String lastname, String memberid, String balance) {
		return driver.findElement(By.xpath("//table[@id='claimsTable']/descendant::td[contains(text(),'"+lastname.toUpperCase()+", "+firstname.toUpperCase()+"')]/following-sibling::td[text()='"+memberid.toUpperCase()+"']/following-sibling::td[text()='"+balance+"']/preceding-sibling::td/span"));
	}
	//table[@id='claimsTable']/descendant::td[contains(text(),'BINGHAM, EMERSYN')]/following-sibling::td[text()='XYQ892408913']/following-sibling::td[text()='$1,248.97']/preceding-sibling::td[7]/span
	@FindBy(id = "claimAmount")
	public WebElement claimAmountInput;
	
	@FindBy(id = "clearForm")
	public WebElement clearForm;
	
	@FindBy(xpath = "//strong[contains(text(),'Transaction ID:')]")
	public WebElement transactionIDLogo;
	
	@FindBy(xpath = "//p[text()='Claim Number']/following-sibling::p")
	public WebElement claimNumber;
	
	@FindBy(id = "remittanceButton")
	public WebElement remittanceBtn;
	
	@FindBy(xpath = "//button[@aria-label='Close']")
	public WebElement closePopUp;
	
	@FindBy(xpath = "//span[text()='Check / EFT']")
	public WebElement checkEFTTab;
	
	
	@FindBy(id = "claimSearchInput")
	public WebElement claimSearchInput;
	
	@FindBy(id = "checkSearchButton")
	public WebElement checkSearchButton;
	
	@FindBy(xpath = "//a[text()='Results']")
	public WebElement resultsTab;
	
	
	
	public WebElement downloadEOB(String firstname, String lastname, String balance) {
		return driver.findElement(By.xpath("//div[contains(text(),'"+lastname.toUpperCase()+", "+firstname.toUpperCase()+"')]/ancestor::div[3]/following-sibling::div/span[contains(text(),'"+balance+"')]/parent::div/following-sibling::div/descendant::button[contains(@id,'claimeob')]"));
	}
	
	////////////////////////////////

	
	@FindBy(id = "billingClaimBtn35")
	public WebElement adjustmentBtn;
	
	@FindBy(id = "billingClaimBtn36")
	public WebElement cancelBtn;
	
	
	@FindBy(id = "claimAdjustmentsBtn9")
	public WebElement addBtn;
	
	@FindBy(id = "claimAdjustmentsBtn11")
	public WebElement threeDots;
	
	@FindBy(xpath = "//td[text()='No Authorization']")
	public WebElement noAuthorization;
	
	@FindBy(id = "FinancialAdjustmentCodeBtn5")
	public WebElement CodeOkBtn;
	
	@FindBy(id = "cptAmt")
	public WebElement amountField;
	

	@FindBy(id = "claimAdjustmentsBtn13")
	public WebElement AmountAddBtn;
	
	@FindBy(id = "claimAdjustmentsBtn14")
	public WebElement postCPTBtn;
	
	@FindBy(xpath = "//div/span[@ng-bind='ClaimData.InvId']")
	public WebElement ClaimVerif;
	
	@FindBy(id = "btnOKPostCPT")
	public WebElement OkBtn2;
	
	@FindBy(xpath = "//table[@id='billingClaimTbl15']/descendant::td[text()='Payments/Adj']/following-sibling::td/span")
	public WebElement adjustmentBalance;

	@FindBy(id = "claimAdjustmentsBtn16")
	public WebElement OkBtn3;
	
	@FindBy(xpath = "//button[contains(@id,'claimScreenOkBtn')]")
	public WebElement OkBtn4;
	
	@FindBy(xpath = "//button[@id='claimAdjustmentsBtn20' and text()='Yes']")
	public WebElement yesBtn;
	
	@FindBy(xpath = "//button[@id='claimAdjustmentsBtn23' and text()='Yes']")
	public WebElement yesBtn2;
	
	
	@FindBy(id = "doctorID")
	public WebElement usernameFieldECW;
	
	@FindBy(id = "passwordField")
	public WebElement passwordFieldECW;
	

	@FindBy(id = "Login")
	public WebElement loginBtnECW;
	
	@FindBy(id = "nextStep")
	public WebElement nextBtnECW;
	
	@FindBy(xpath = "//a[text()='Action' and @lookupshortcut]")
	public WebElement patientLookupBtnECW;
	
	@FindBy(xpath = "//div[@class='favlist']/a[@class='navgator mainMenu']")
	public WebElement expandMenubtnECW;
	
	@FindBy(xpath = "//li[@title='Billing']/a")
	public WebElement billingTabECW;
	
	@FindBy(xpath = "//span[text()='Claims']/parent::a")
	public WebElement claimsMenuECW;
	
	@FindBy(id = "claimLookupIpt10")
	public WebElement claimLookupInputECW;
	
	@FindBy(id = "btnclaimlookup")
	public WebElement claimLookupBtnECW;
	
	@FindBy(xpath = "//button[@ng-click='saveAllData()']")
	public WebElement saveClaimBtnECW;
	
	
	@FindBy(xpath = "//div[@class='billing-right-toggle claimRightPanel-tog']")
	public WebElement followUpArrowECW;
	
	@FindBy(id = "claimRightPanelNotes")
	public WebElement claimNotesECW;
	
	
	public WebElement getbalanceFromApp(String date, String cpt) {
		return driver.findElement(By.xpath("//tr/td[1][contains(text(),'"+date+"')]/following-sibling::td[2][contains(text(),'"+cpt+"')]/following-sibling::td[2]"));
	}
	
	public WebElement writeOffbalanceInApp(String date, String cpt) {
		return driver.findElement(By.xpath("//tr/td[1][contains(text(),'"+date+"')]/following-sibling::td[2][contains(text(),'"+cpt+"')]/following-sibling::td[3]/input"));
	}
	
	public void waitFunc(WebElement webEle) {
		wait.until(ExpectedConditions.elementToBeClickable(webEle));
	}
	
	public void waitFuncInvisibility(WebElement webEle) {
		wait.until(ExpectedConditions.invisibilityOf(webEle));
	}
	
}
