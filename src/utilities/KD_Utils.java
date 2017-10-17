package utilities;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import executionengine.ExcelOperations;
import utilities.BaseClass.sheetType;

public class KD_Utils 
{
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports report = new ExtentReports();
	
	public static void setSuiteLevelConsts() 
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc;		
		try 
		{
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse("KeywordDrivenConfig.xml");
			doc.getDocumentElement().normalize();
			String strRootPath = System.getProperty("user.dir");			
			
			KDConstants.strReportsPath = strRootPath + getNodeValue(doc, "Reports");
			KDConstants.strScreenshotsPath = strRootPath + getNodeValue(doc, "ScreenShots");
			KDConstants.strPathForPageObjectConfigs = strRootPath + getNodeValue(doc, "PageObjectConfigs");			
			KDConstants.strScenariosFilePath = getNodeValue(doc, "FilePath");
			KDConstants.strScenariosSheetName = getNodeValue(doc, "SheetName-Scenarios");
			KDConstants.strTestCasesSheetName = getNodeValue(doc, "SheetName-TestCases");
		} 
		catch (Exception ex) 
		{
			System.out.println("Class KD_Utils | Method setSuiteLevelConsts | Exception desc : " + ex.getMessage());
		}
	}
	
	private static String getNodeValue(Document doc, String strNodeName)
	{		
		Node nNode;
		NodeList nodeList;
		String strRetVal = "";
		try
		{
			nodeList = doc.getElementsByTagName(strNodeName);
			nNode = nodeList.item(0);
			strRetVal = nNode.getTextContent();
		} 
		catch (Exception ex) 
		{
			System.out.println("Class KD_Utils | Method getNodeValue | Exception desc : " + ex.getMessage());
		}
		return strRetVal;
	}
	
	public static void setExtentReport()
	{
		try
		{	
			Calendar cal = Calendar.getInstance();
			KDConstants.strDirectory = Integer.toString(cal.get(Calendar.YEAR)) + Integer.toString(cal.get(Calendar.MONTH) + 1) + Integer.toString(cal.get(Calendar.DATE));
			createDirectory(KDConstants.strReportsPath + KDConstants.strDirectory);
			
			htmlReporter = new ExtentHtmlReporter(KDConstants.strReportsPath + KDConstants.strDirectory + "\\KDReport_" + getRandomVal() + ".html");
			htmlReporter.loadXMLConfig(KDConstants.strReportsPath + "extent-config.xml");
	        htmlReporter.config().setChartVisibilityOnOpen(false);	        
			report.attachReporter(htmlReporter);	
		}
		catch(Exception ex)
		{
			System.out.println("Class KD_Utils | Method setExtentReport | Exception desc : " + ex.getMessage());
		}
	}
	
	private static void createDirectory(String strDirName)
	{
		try
		{
			File strDir = new File(strDirName);

			if (!strDir.exists()) 
			{
				System.out.println("creating directory: " + strDir);				
				strDir.mkdir();
				System.out.println("Directory Successfully created");
			}
		}
		catch(Exception ex)
		{
			System.out.println("Class KD_Utils | Method createDirectory | Exception desc : " + ex.getMessage());
		}
	}
	
	public static String takeScreenShot(WebDriver scDriver, String strFileName)
    {		
		String strRetVal = "";
		try
		{	
			File scrFile = ((TakesScreenshot)scDriver).getScreenshotAs(OutputType.FILE);     
			strRetVal = KDConstants.strScreenshotsPath + KDConstants.strDirectory + "//" + strFileName + "_" + getRandomVal() + ".jpeg";
            FileUtils.copyFile(scrFile, new File(strRetVal));            
		}
		catch(Exception ex)
		{
			System.out.println("Class KD_Utils | Method takeScreenShot | Exception desc : " + ex.getMessage());
		}
		return strRetVal;
    }
	
	public static void logFailureAndTakeScreenshot(WebDriver driver, String failureDescription, String screenshotName)
	{
		try
		{
			BaseClass.logger.fail(failureDescription);
			BaseClass.logger.fail(screenshotName, MediaEntityBuilder.createScreenCaptureFromPath(takeScreenShot(driver, screenshotName)).build());
		}
		catch(Exception ex)
		{
			System.out.println("Class KD_Utils | Method logReportAndTakeScreenshot | Exception desc : " + ex.getMessage());
		}
	}
	
	private static String getRandomVal() 
	{
		String strRamdom = "";
		try 
		{			
			Calendar cal = Calendar.getInstance();

			strRamdom = Integer.toString(cal.get(Calendar.YEAR)) + Integer.toString(cal.get(Calendar.MONTH) + 1)
					+ Integer.toString(cal.get(Calendar.DATE)) + "_"
					+ ((cal.get(Calendar.AM_PM) == Calendar.AM) ? Integer.toString(cal.get(Calendar.HOUR))
							: (Integer.toString(cal.get(Calendar.HOUR) + 12)))
					+ Integer.toString(cal.get(Calendar.MINUTE)) + Integer.toString(cal.get(Calendar.SECOND));
		} catch (Exception ex) {
			System.out.println("Class KD_Utils | Method getRandomVal | Exception desc : " + ex.getMessage());
		}
		return strRamdom;
	}
	
	public static LinkedHashMap<String, Object[]> getScenarios()
	{
		LinkedHashMap<String, Object[]> scenarios = new LinkedHashMap<String, Object[]>();
		try
		{
			Object[][] excelData = ExcelOperations.readDataFromExcel(sheetType.Scenarios, "");
			for(int i = 0; i < excelData.length; i++)
				scenarios.put((String) excelData[i][0], excelData[i]);
		}
		catch(Exception ex)
		{
			System.out.println("Class KD_Utils | Method getScenarios | Exception desc : " + ex.getMessage());
		}
		return scenarios;
	}
	
	public static Object[][] getTestCasesForEachScenario(String scenarioID)
	{		
		Object[][] excelData = null;
		try
		{
			excelData = ExcelOperations.readDataFromExcel(sheetType.TestCases, scenarioID);			
		}
		catch(Exception ex)
		{
			System.out.println("Class KD_Utils | Method getTestCasesForEachScenario | Exception desc : " + ex.getMessage());
		}
		return excelData;
	}
	
	public static List<WebElement> processElementListBasedOnLocator(WebDriver driver, String strLocator, String strLocatorValue)
	{
		List<WebElement> elementList = null;		
		try
		{
			switch (strLocator) {
			case "xpath":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.xpath(strLocatorValue)));
				elementList = driver.findElements(By.xpath(strLocatorValue));
				break;
			case "id":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.id(strLocatorValue)));
				elementList = driver.findElements(By.id(strLocatorValue));
				break;
			case "name":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.name(strLocatorValue)));
				elementList = driver.findElements(By.name(strLocatorValue));
				break;
			case "linkText":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.linkText(strLocatorValue)));
				elementList = driver.findElements(By.linkText(strLocatorValue));
				break;
			case "partialLinkText":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(strLocatorValue)));
				elementList = driver.findElements(By.partialLinkText(strLocatorValue));
				break;
			case "className":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.className(strLocatorValue)));
				elementList = driver.findElements(By.className(strLocatorValue));
				break;
			case "cssSelector":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(strLocatorValue)));
				elementList = driver.findElements(By.cssSelector(strLocatorValue));
				break;
			case "tagName":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.tagName(strLocatorValue)));
				elementList = driver.findElements(By.tagName(strLocatorValue));
				break;
			default:
				break;
			}
		}
		catch (Exception ex) {
			System.out.println("Class KD_Utils | Method processElementListBasedOnLocator | Exception desc : " + ex.getMessage());
		}
		return elementList;
	}
	
	public static WebElement processWebElementBasedOnLocator(WebDriver driver, String strLocator, String strLocatorValue)
	{
		WebElement element = null;		
		try
		{
			switch (strLocator) {
			case "xpath":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.xpath(strLocatorValue)));
				element = driver.findElement(By.xpath(strLocatorValue));
				break;
			case "id":				
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.id(strLocatorValue)));
				element = driver.findElement(By.id(strLocatorValue));
				break;
			case "name":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.name(strLocatorValue)));
				element = driver.findElement(By.name(strLocatorValue));
				break;
			case "linkText":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.linkText(strLocatorValue)));
				element = driver.findElement(By.linkText(strLocatorValue));
				break;
			case "partialLinkText":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(strLocatorValue)));
				element = driver.findElement(By.partialLinkText(strLocatorValue));
				break;
			case "className":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.className(strLocatorValue)));
				element = driver.findElement(By.className(strLocatorValue));
				break;
			case "cssSelector":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(strLocatorValue)));
				element = driver.findElement(By.cssSelector(strLocatorValue));
				break;
			case "tagName":
				new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfElementLocated(By.tagName(strLocatorValue)));
				element = driver.findElement(By.tagName(strLocatorValue));
				break;
			default:
				break;
			}
		}
		catch (Exception ex) {
			System.out.println("Class KD_Utils | Method processWebElementBasedOnLocator | Exception desc : " + ex.getMessage());
		}
		return element;
	}
	
	public static String compareOptions(String[] options, List<WebElement> elementList)
	{
		String missingOptions = "";
		boolean optionexists = false;
		try
		{			
			for(String option: options)
			{
				optionexists = false;
				for(WebElement ele: elementList)
				{
					if(option.equalsIgnoreCase(ele.getText()))
					{
						optionexists = true;
						break;
					}
				}
				if(!optionexists)
				{
					missingOptions +=  " " + "\"" + option + "\"";
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("Class KD_Utils | Method compareOptions | Exception desc : " + ex.getMessage());
		}
		return missingOptions;
	}
	
	public static String compareOptions(String[] options, List<WebElement> elementList, String attName)
	{
		String missingOptions = "";
		boolean optionexists = false;
		try
		{			
			for(String option: options)
			{
				optionexists = false;
				for(WebElement ele: elementList)
				{
					if(option.contains(ele.getAttribute(attName)))
					{
						optionexists = true;
						break;
					}
				}
				if(!optionexists)
				{
					missingOptions +=  " " + "\"" + option + "\"";
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("Class KD_Utils | Method compareOptions | Exception desc : " + ex.getMessage());
		}
		return missingOptions;
	}
	
}
