package utilities;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentTest;

public class BaseClass 
{	
	public static WebDriver driver;	
	public static ExtentTest tempLogger;
	public static ExtentTest logger;
	public static String parentHandle = null; 
	public static enum sheetType {Scenarios, TestCases, DataSet}; 
	
	@BeforeSuite
	public void setup()
	{
		KD_Utils.setSuiteLevelConsts();
		KD_Utils.setExtentReport();
	}
	
	@AfterSuite
	public void tearDown()
	{
		KD_Utils.report.flush();
		driver.quit();
	}
	
}
