package ee.veebimaailm.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VeebimaailmTests {
	private WebDriver driver;
	private Random random;
	private String pageSource;
	WebDriverWait wait;
	
	@Before
	public void setUp() throws Exception {
		//File profileDir = new File("./seleniumFirefoxProfile");
		File profileDir = new File("C:\\Users\\Mkk\\Desktop\\seleniumFirefoxProfile");
		FirefoxProfile firefoxProfile = new FirefoxProfile(profileDir);
		firefoxProfile.setAcceptUntrustedCertificates(true);
		firefoxProfile.setPreference("security.default_personal_cert", "Select Automatically");
		
		driver = new FirefoxDriver(firefoxProfile);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.get("http://veebimaailm.dyndns.info/");
		random = new Random();
		wait = new WebDriverWait(driver,15);
	}
	
	@Test
	public void testLogin() throws Exception {
		logIn();
		pageSource = driver.getPageSource();
		assertTrue(pageSource.contains("MARI-LIIS MÄNNIK"));
		assertTrue(pageSource.contains("Logi Välja"));
		logOut();
		pageSource = driver.getPageSource();
		assertFalse(pageSource.contains("MARI-LIIS MÄNNIK"));
		assertTrue(pageSource.contains("Logi sisse ID-kaardiga"));
	}
	
	@Test
	public void testNominate() throws Exception {
		logIn();
		navigate("Kandideeri");
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.id("is_voted_text"),"Te ei ole veel kandideerinud."));
		
		int region_id = random.nextInt(17)+1;
		int party_id = random.nextInt(4)+1;
		selectRegion(region_id);
		selectParty(party_id);
		
		
		nominate();
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.id("is_voted_text"),"Te olete kandideerinud."));
		
		navigate("Statistika");
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.className("name"), "Erakond"));
		selectRegion(region_id);
		selectParty(party_id);
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.className("name"), "Nimi"));
		assertTrue(candidateExists("MARI-LIIS MÃ?NNIK"));
		
		navigate("Kandideeri");
		cleanupNominate();
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.id("is_voted_text"),"Te ei ole veel kandideerinud."));
		
		logOut();
		
	}
	@Test
	public void testVote() throws Exception {
		logIn();
		String candidateName = "Eduard Ekskavaator";
		String candidateNameLetters = "eksk";
		navigate("Statistika");
		
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.className("name"), "Erakond"));
		searchCandidateByLetters(candidateNameLetters);
		
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.className("name"), "Nimi"));
		int priorVoteCount = votesByCandidate(candidateName);
		if (priorVoteCount==-1) {
			fail("Could not found Candidate");
		}
		
		navigate("Hääleta");
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.id("is_voted_text"), "Te ei ole veel hääletanud."));
		
		searchCandidateByLetters(candidateNameLetters);
		//Date date = new Date();
		voteForCandidate(candidateName);
		//DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.id("is_voted_text"), "Te olete juba hääletanud."));
		
		navigate("Statistika");
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.className("name"), "Erakond"));
		
		searchCandidateByLetters(candidateNameLetters);
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.className("name"), "Nimi"));
		int currentVoteCount = votesByCandidate(candidateName);
		if (priorVoteCount>=currentVoteCount) {
			fail("Vote count didn't increased.");
		}
		
		navigate("Hääleta");
		cleanupVote();
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.textToBePresentInElement(By.id("is_voted_text"), "Te ei ole veel hääletanud."));
		logOut();	
	}
	@Test
	public void testSearchCandidate() throws Exception {
		logIn();
		String candidateName = "Neeme Näljahäda";
		int region_id = 8;
		int party_id = 4;
		navigate("Hääleta");
		
		selectRegion(region_id);
		assertTrue(candidateExists(candidateName));
		
		selectParty(party_id);
		assertTrue(candidateExists(candidateName));
		
		selectRegion(0);
		assertTrue(candidateExists(candidateName));
		
		logOut();
		
	}
	
	private void logIn() throws Exception {
		WebElement loginbutton = driver.findElement(By.id("loginlink"));
		loginbutton.click();
		waitForPageLoaded(driver);
		Thread.sleep(1000);
		WebElement loginbutton2 = driver.findElement(By.id("loginlink"));	
		loginbutton2.click();
		WebElement loginname = driver.findElement(By.id("namefield"));
	}
	private void logOut() throws Exception {
		WebElement logoutButton = driver.findElement(By.name("Submit"));
		logoutButton.click();
		WebElement loginForm = driver.findElement(By.id("loginlink"));
	}
	private void navigate(String linkText) throws Exception {
		wait.until(ExpectedConditions.elementToBeClickable(By.linkText(linkText)));
		WebElement link = driver.findElement(By.linkText(linkText));
		link.click();
	}
	private void nominate() throws Exception {
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("submitbutton")));
		WebElement submitButton = driver.findElement(By.id("submitbutton"));
		submitButton.click();
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("button")));
		List<WebElement> choiceButtons = driver.findElements(By.tagName("button"));
		for (WebElement button: choiceButtons) {
			if (button.getText().equals("Jah")) {
				button.click();
				return;
			}
		}
	}
	private void selectRegion(int region_id) {
		Select select_region = new Select(driver.findElement(By.id("districts")));
		select_region.selectByIndex(region_id);
	}
	private void selectParty(int party_id) {
		Select select_party = new Select(driver.findElement(By.id("politics-party")));
		select_party.selectByIndex(party_id);
		
	}
	private boolean candidateExists(String name) {
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("name")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("voting-table-body")));
		WebElement statisticTable = driver.findElement(By.id("voting-table-body"));
		List<WebElement> nameColumns = statisticTable.findElements(By.className("name"));
		for (WebElement column: nameColumns) {
			if (column.getText().equals(name)) {
				return true;
			}
			continue;
		}
		return false;
	}
	private void cleanupNominate() throws Exception {
		WebElement cancelButton = driver.findElement(By.id("cancel_nominate"));
		cancelButton.click();
	}
	private void searchCandidateByLetters(String letters) throws Exception {
		WebElement searchField = driver.findElement(By.id("search-candidate"));
		searchField.sendKeys(letters);
	}
	private int votesByCandidate(String name) {
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("name")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("voting-table-body")));
		WebElement statisticTable = driver.findElement(By.id("voting-table-body"));
		List<WebElement> nameColumns = statisticTable.findElements(By.className("name"));
		for (WebElement column: nameColumns) {
			if (column.getText().equals(name)) {
				WebElement votesColumn = column.findElement(By.xpath("./following::td"));
				return Integer.parseInt(votesColumn.getText());
			}
			continue;
		}
		return -1;
	}
	private void voteForCandidate(String name) throws Exception  {
		waitforAJAXRequestsToComplete(driver);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("name")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("voting-table-body")));
		WebElement votingTable = driver.findElement(By.id("voting-table-body"));
		List<WebElement> nameColumns = votingTable.findElements(By.className("name"));
		for (WebElement column: nameColumns) {
			if (column.getText().equals(name)) {
				WebElement votingColumn = column.findElement(By.xpath("./following::td[@class='vote'][1]/p"));
				votingColumn.click();
				return;
			}
			continue;
		}
	}
	private void cleanupVote() throws Exception {
		WebElement cancelButton = driver.findElement(By.id("cancel_vote"));
		cancelButton.click();
	}
	public void waitForPageLoaded(WebDriver driver) {
		//http://stackoverflow.com/questions/13244225/selenium-how-to-make-the-web-driver-to-wait-for-page-to-refresh-before-executin
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
	        }
	    };

	    Wait<WebDriver> wait = new WebDriverWait(driver,30);
	    try {
	    	wait.until(expectation);
	    } catch(Throwable error) {
	    	assertFalse("Timeout waiting for Page Load Request to complete.",true);
	    }
	 }
	//http://stackoverflow.com/questions/8048463/how-would-you-use-events-with-selenium-2
	public void waitforAJAXRequestsToComplete(WebDriver driver){
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor)driver).executeScript("return jQuery.active").toString().equals("0");
	        }
	    };
	    Wait<WebDriver> wait = new WebDriverWait(driver,30);
	    try {
	    	wait.until(expectation);
	    } catch(Throwable error) {
	    	assertFalse("Timeout waiting for Page Load Request to complete.",true);
	    }
  
    }
	@After
	public void tearDown() throws Exception {
		driver.close();
	}
}
