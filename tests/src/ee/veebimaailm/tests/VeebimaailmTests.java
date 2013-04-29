package ee.veebimaailm.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.Select;

public class VeebimaailmTests {
	private WebDriver driver;
	private Random random;
	private String pageSource;
	private int sleepTime = 5000;
	
	@Before
	public void setUp() throws Exception {
		File profileDir = new File("./seleniumFirefoxProfile");
		FirefoxProfile firefoxProfile = new FirefoxProfile(profileDir);
		firefoxProfile.setAcceptUntrustedCertificates(true);
		firefoxProfile.setPreference("security.default_personal_cert", "Select Automatically");
		driver = new FirefoxDriver(firefoxProfile);
		driver.get("http://veebimaailm.dyndns.info/");
		random = new Random();
		Thread.sleep(sleepTime);	
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
		pageSource = driver.getPageSource();
		assertTrue(pageSource.contains("Te ei ole veel kandideerinud."));
		
		int region_id = random.nextInt(17)+1;
		int party_id = random.nextInt(4)+1;
		selectParty(party_id);
		selectRegion(region_id);
		nominate();
		pageSource = driver.getPageSource();
		assertTrue(pageSource.contains("Te olete kandideerinud."));
		
		navigate("Statistika");
		selectParty(party_id);
		selectRegion(region_id);
		Thread.sleep(sleepTime);
		assertTrue(candidateExists("MARI-LIIS MÃ?NNIK"));
		
		navigate("Kandideeri");
		cleanupNominate();
		pageSource = driver.getPageSource();
		assertTrue(pageSource.contains("Te ei ole veel kandideerinud."));
		logOut();
		
	}
	@Test
	public void testVote() throws Exception {
		logIn();
		String candidateName = "Eduard Ekskavaator";
		String candidateNameLetters = "eksk";
		navigate("Statistika");
		searchCandidateByLetters(candidateNameLetters);
		int priorVoteCount = votesByCandidate(candidateName);
		if (priorVoteCount==-1) {
			fail("Could not found Candidate");
		}
		
		navigate("Hääleta");
		pageSource = driver.getPageSource();
		assertTrue(pageSource.contains("Te ei ole veel hääletanud."));
		
		searchCandidateByLetters(candidateNameLetters);
		Date date = new Date();
		voteForCandidate(candidateName);
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		pageSource = driver.getPageSource();
		assertTrue(pageSource.contains("Te olete juba hääletanud.["+dateFormat.format(date)+"]"));
		
		navigate("Statistika");
		searchCandidateByLetters(candidateNameLetters);
		int currentVoteCount = votesByCandidate(candidateName);
		if (priorVoteCount>=currentVoteCount) {
			fail("Vote count didn't increased.");
		}
		navigate("Hääleta");
		cleanupVote();
		pageSource = driver.getPageSource();
		assertTrue(pageSource.contains("Te ei ole veel hääletanud."));
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
		Thread.sleep(sleepTime);
		assertTrue(candidateExists(candidateName));
		
		selectParty(party_id);
		Thread.sleep(sleepTime);
		assertTrue(candidateExists(candidateName));
		
		selectRegion(0);
		Thread.sleep(sleepTime);
		assertTrue(candidateExists(candidateName));
		
		logOut();
		
	}
	
	private void logIn() throws Exception {
		WebElement loginbutton = driver.findElement(By.id("loginlink"));
		loginbutton.click();
		Thread.sleep(sleepTime);
		WebElement loginbutton2 = driver.findElement(By.id("loginlink"));	
		loginbutton2.click();
		Thread.sleep(sleepTime);	
	}
	private void logOut() throws Exception {
		WebElement logoutButton = driver.findElement(By.name("Submit"));
		logoutButton.click();
		Thread.sleep(sleepTime);
	}
	private void navigate(String linkText) throws Exception {
		WebElement link = driver.findElement(By.linkText(linkText));
		link.click();
		Thread.sleep(sleepTime);
	}
	private void nominate() throws Exception {
		WebElement submitButton = driver.findElement(By.id("submitbutton"));
		submitButton.click();
		Thread.sleep(sleepTime);
		List<WebElement> choiceButtons = driver.findElements(By.tagName("button"));
		for (WebElement button: choiceButtons) {
			if (button.getText().equals("Jah")) {
				button.click();
			}
		}
		Thread.sleep(sleepTime*2);
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
		Thread.sleep(sleepTime*2);
	}
	private void searchCandidateByLetters(String letters) throws Exception {
		WebElement searchField = driver.findElement(By.id("search-candidate"));
		searchField.sendKeys(letters);
		Thread.sleep(sleepTime);
	}
	private int votesByCandidate(String name) {
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
		WebElement votingTable = driver.findElement(By.id("voting-table-body"));
		List<WebElement> nameColumns = votingTable.findElements(By.className("name"));
		for (WebElement column: nameColumns) {
			if (column.getText().equals(name)) {
				WebElement votingColumn = column.findElement(By.xpath("./following::td[@class='vote'][1]/p"));
				votingColumn.click();
				Thread.sleep(sleepTime*2);
				return;
			}
			continue;
		}
	}
	private void cleanupVote() throws Exception {
		WebElement cancelButton = driver.findElement(By.id("cancel_vote"));
		cancelButton.click();
		Thread.sleep(sleepTime*2);
	}
	@After
	public void tearDown() throws Exception {
		driver.close();
	}
}
