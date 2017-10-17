package executionengine;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;
import com.aventstack.extentreports.MediaEntityBuilder;
import utilities.BaseClass;
import utilities.KD_Utils;

public class ActionKeywords extends BaseClass
{
	public static enum alert {Accept, Dismiss, GetText};
	
	public static void launchChromeWithProfile(String profilePath)
	{
		try
		{
			logger.info("Trying to launch Chrome browser with saved profile");
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
			ChromeOptions options = new ChromeOptions();			
			options.addArguments("user-data-dir=" + profilePath);
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);			
			driver = new ChromeDriver(capabilities);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			logger.info("Browser launched Successfully");
			parentHandle = driver.getWindowHandle();
		}
		catch(Exception ex)
		{			
			System.out.println("Class ActionKeywords | Method launchChromeWithProfile | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void openBrowser(String browserName)
	{
		try
		{
			switch(browserName.toLowerCase())
			{
				case "ie":
				case "internetexplorer":
				case "internet explorer":
					logger.info("Trying to launch internet explorer");
					System.setProperty("webdriver.ie.driver", "IEDriverServer.exe");
					driver = new InternetExplorerDriver();
					break;
				case "chrome":
				case "google chrome":
				case "googlechrome":
					logger.info("Trying to launch Chrome browser");
					System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
					driver = new ChromeDriver();
					break;
				case "mozilla":
				case "firefox":
				case "fire fox":
				case "mozilla firefox":
				case "ff":
					logger.info("Trying to launch firefox browser");
					System.setProperty("webdriver.gecko.driver", "geckodriver.exe");					
					driver = new FirefoxDriver();					
					break;
			}
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			parentHandle = driver.getWindowHandle();
			logger.pass("Browser launched Successfully");
		}
		catch(Exception ex)
		{			
			System.out.println("Class ActionKeywords | Method openBrowser | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void navigate(String url)
	{
		try
		{
			logger.info("Trying to navigate to the page: " + url); 
			driver.get(url);
			logger.pass("Navigated to " + url + " Successfully");
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "navigate");
			System.out.println("Class ActionKeywords | Method navigate | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void waitForWindowsToLoad(String value)
	{
		boolean blnRetVal = false;
		try 
		{
			int windowSize = (int) Double.parseDouble(value);
			Set<String> Handles = driver.getWindowHandles();
			for (int i = 1; i <= 20; i++) 
			{
				if (Handles.size() == windowSize)
				{
					blnRetVal = true;
					break;
				}
				Thread.sleep(3000);
				Handles = driver.getWindowHandles();
				Reporter.log("Waited for " + (i * 3) + " Seconds", true);
			}
			if(blnRetVal)
				logger.info("Desired windows count reached: " + value);	
		}
		catch (Exception ex) 
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "waitForWindowsToLoad");
			System.out.println("Class ActionKeywords | Method waitForWindowsToLoad | Exception desc : " + ex.getMessage());
		}		
	}
	
	public static void switchFrameUsingTitle(String value)
	{
		try
		{
			List<WebElement>elements = driver.findElements(By.tagName("iframe"));
			for(WebElement ele: elements)
			{
				if(ele.getAttribute("title").equalsIgnoreCase(value))
				{
					driver.switchTo().frame(ele.getAttribute("name"));
					break;
				}
			}
		}
		catch (Exception ex) 
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "switchFrameUsingTitle");
			System.out.println("Class ActionKeywords | Method switchFrameUsingTitle | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void switchToWindowUsingTitle(String title)
	{
		boolean blnPageFound = false;
		Set<String> Handles = null;
		try
		{	
			Thread.sleep(2000);
			Handles = driver.getWindowHandles();
			System.out.println("Trying to find Page - " + title);			
			if(Handles != null)
			{	
				for (String handle : Handles) 
				{		
					if (driver.switchTo().window(handle).getTitle().contains(title)) 
					{						
						driver.switchTo().window(handle);
						Reporter.log("Switched to Page - " + title, true);
						blnPageFound = true;
						break;
					}
				}
			}			
			
			if(blnPageFound)
			{
				System.out.println("Able to find Page - " + title);
				logger.info("Switched to window using title");	
			}
			else
			{
				System.out.println("Unable to find Page - " + title);
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to Switch window using title", "switchToWindowUsingTitle");
			}
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "switchToWindowUsingTitle");
			System.out.println("Class ActionKeywords | Method switchToWindow | Exception desc : " + ex.getMessage());
		}		
	}
	
	public static boolean switchToReportsWindow(String value)
	{
		boolean blnPageFound = false;
		Set<String> Handles = null;
		try
		{	
			Handles = driver.getWindowHandles();					
			if(Handles != null)
			{	
				for (String handle : Handles) 
				{		
					if (!(handle.equalsIgnoreCase(driver.getWindowHandle()) || handle.equalsIgnoreCase(BaseClass.parentHandle))) 
					{						
						driver.switchTo().window(handle);						
						blnPageFound = true;
						break;
					}
				}
			}			
			
			if(blnPageFound)
				System.out.println("Able to reach Reports window");
			else
				System.out.println("Unable to reach Reports window");
		}
		catch(Exception ex)
		{
			System.out.println("Class ActionKeywords | Method switchToReportsWindow | Exception desc : " + ex.getMessage());
		}
		return blnPageFound;
	}
	
	public static void closeAllWindowsExceptParent(String value)
	{
		Set<String> Handles = null;
		try
		{			
			Handles = driver.getWindowHandles();
			if(Handles != null)
			{	
				for (String handle : Handles) 
				{		
					if (!handle.equalsIgnoreCase(parentHandle)) 
					{						
						driver.switchTo().window(handle);
						driver.close();
					}
				}
				driver.switchTo().window(parentHandle);
			}
		}
		catch(Exception ex)
		{
			System.out.println("Class ActionKeywords | Method closeAllWindowsExceptParent | Exception desc : " + ex.getMessage());
		}		
	}
	
	public static void verifyWindowTitle(String value)
	{
		try
		{
			logger.info("Validating window title");
			if(driver.getTitle().contains(value))
			{
				logger.pass("Window title matched");	
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Window title not macthed. Actual: " + driver.getTitle() + ", Expected: " + value, "WindowTitleIssue");
			}
		}
		catch(Exception ex)
		{
			System.out.println("Class ActionKeywords | Method verifyWindowTitle | Exception desc : " + ex.getMessage());
		}	
	}
	
	public static void switchToChildWindow(String value)
	{
		boolean blnPageFound = false;
		try
		{			
			Set<String> Handles = null;
			Handles = driver.getWindowHandles();
			if(Handles != null)
			{
				for (String handle : Handles) 
				{
					if (!handle.equalsIgnoreCase(parentHandle)) 
					{
						driver.switchTo().window(handle);					
						blnPageFound = true;
						break;
					}
				}
			}
			if(blnPageFound)
			{
				logger.info("Able to Reach Child window ");
				System.out.println("Able to Reach Child window ");
			}
			else
			{
				System.out.println("Unable to reach child window");
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to reach child window", "switchToChildWindow");
			}
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "switchToChildWindow");
			System.out.println("Class ActionKeywords | Method switchToChildWindow | Exception desc : " + ex.getMessage());
		}		
	}
	
	public static void mousehover(WebElement element, String elementName, String value)
	{
		try
		{
			logger.info("Trying to locate " + elementName);
			if(element != null)
			{
				logger.info("Successfully located " + elementName);
				Actions action = new Actions(driver);
				action.moveToElement(element).build().perform();
				logger.pass("Successfully hovered on element: \"" + elementName + "\"");
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}			
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "mousehover");
			System.out.println("Class ActionKeywords | Method mousehover | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void setText(WebElement element, String elementName, String value)
	{
		try
		{
			logger.info("Trying to locate " + elementName);
			if(element != null)
			{
				logger.info("Successfully located " + elementName);
				element.clear();
				element.click();
				element.sendKeys(value);
				logger.pass("Successfully entered " + value + " in " + elementName);
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "setText");
			System.out.println("Class ActionKeywords | Method setText | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void click(WebElement element, String elementName, String value)
	{
		try
		{
			logger.info("Trying to locate " + elementName);
			if(element != null)
			{
				logger.info("Successfully located " + elementName);
				element.click();
				logger.pass("Successfully clicked " + elementName);
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "click");
			System.out.println("Class ActionKeywords | Method click | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void clickBasedOnPosition(WebElement element, String elementName, String value)
	{
		logger.info("Trying to locate " + elementName);
		try
		{
			Thread.sleep(1000);
			logger.info("Successfully located " + elementName);
			JavascriptExecutor js =(JavascriptExecutor)driver;
	        js.executeScript("window.scrollTo(0," + element.getLocation().x +")");
			element.click();
			logger.pass("Successfully clicked " + elementName);
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "clickBasedOnPosition");
			System.out.println("Class ActionKeywords | Method clickBasedOnPosition | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void verifyPresenceOfEntity(WebElement element, String elementName, String value)
	{
		try
		{
			logger.info("Trying to verify presence of " + elementName);
			if(element != null)
			{
				logger.info("Successfully verified presence of " + elementName);				
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "verifyPresenceOfEntity");
			System.out.println("Class ActionKeywords | Method verifyPresenceOfEntity | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void dropdown_SelectByVisibleText(WebElement element, String elementName, String strVisibleText)
	{		
		try
		{
			logger.info("Trying to verify presence of " + elementName);
			if(element != null)
			{
				logger.info("Successfully verified presence of " + elementName);
				Select objSelect = new Select(element);
				objSelect.selectByVisibleText(strVisibleText);				
				logger.pass("Successfully selected value: " + strVisibleText);
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}
		} 
		catch (Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "dropdown_SelectByVisibleText");
			System.out.println("Class ActionKeywords | Method dropdown_SelectByVisibleText | Exception desc : " + ex.getMessage());
		}		
	}
	
	public static void dropdown_SelectByValue(WebElement element, String elementName, String strValue)
	{
		try
		{
			logger.info("Trying to verify presence of " + elementName);
			if(element != null)
			{
				logger.info("Successfully verified presence of " + elementName);
				Select objSelect = new Select(element);
				objSelect.selectByValue(strValue);	
				logger.pass("Successfully selected value: " + strValue);
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}
		} 
		catch (Exception ex) 
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "dropdown_SelectByValue");
			System.out.println("Class ActionKeywords | Method dropdown_SelectByValue | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void dropdown_SelectByIndex(WebElement element, String elementName, String value)
	{
		try
		{
			int intVal = (int)Double.parseDouble(value);
			logger.info("Trying to verify presence of " + elementName);
			if(element != null)
			{
				logger.info("Successfully verified presence of " + elementName);
				Select objSelect = new Select(element);
				objSelect.selectByIndex(intVal);
				logger.pass("Successfully selected value at index: " + value);
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}			
		} 
		catch (Exception ex) 
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "dropdown_SelectByIndex");
			System.out.println("Class ActionKeywords | Method dropdown_SelectByIndex | Exception desc : " + ex.getMessage());
		}
	}
		
	//value = locatorType::locatorValue::Time to poll (Ex: xpath:://div[@id='test']::2)	
	public static void pollForAnElement(WebElement element, String elementName, String value) 
	{
		int pollTime = 0;
		boolean blnElementFound = false;
		String[] arrayValue = new String[2];
		try
		{
			logger.info("Polling for an element: " + elementName);
			if(element != null)
			{
				logger.pass("Found element: " + elementName);
			}
			else
			{
				arrayValue = value.split("::");
				pollTime = Integer.parseInt(arrayValue[2]);
				for(int i = 0; i < pollTime; i++)
				{					
					if(KD_Utils.processWebElementBasedOnLocator(driver, arrayValue[0], arrayValue[1]) != null)
					{
						logger.pass("Found element: " + elementName);
						blnElementFound = true;
						break;
					}					
				}
				if(!blnElementFound)
				{
					KD_Utils.logFailureAndTakeScreenshot(driver, "Element not found: " + elementName, "pollForAnElement");
				}
			}
		}
		catch (Exception ex) 
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "pollForAnElement");
			System.out.println("Class ActionKeywords | Method pollForAnElement | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void sendKeys(String value)
	{
		boolean blnKeyPress = false;
		try
		{
			Keys[] keys = Keys.values();
			logger.info("Trying to pres key: " + value);
			for(Keys key: keys)
			{
				if(key.name().equalsIgnoreCase(value))
				{
					Actions action = new Actions(driver);
					action.sendKeys(key).perform();
					blnKeyPress = true;
					break;
				}
			}
			if(blnKeyPress)
				logger.pass(value + " key pressed successfully");
			else
				logger.fail("Failed to press key: " + value);			
		}
		catch (Exception ex) 
		{			
			System.out.println("Class ActionKeywords | Method sendKeys | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void keyUp(String value)
	{
		try
		{
			logger.info("Trying to release the key: " + value);
			if(value.equalsIgnoreCase(Keys.CONTROL.name()) ||  value.equalsIgnoreCase(Keys.ALT.name()) || value.equalsIgnoreCase(Keys.SHIFT.name()))
			{
				Actions action = new Actions(driver);
				action.keyUp(value.equalsIgnoreCase(Keys.CONTROL.name()) ? Keys.CONTROL : (value.equalsIgnoreCase(Keys.ALT.name())? Keys.ALT : Keys.SHIFT)).perform();
				logger.pass("Key up event performed successfully for key: " + value);
			}
			else
			{
				logger.fail("Failed to perform Key up event for key: " + value + " (Only CONTROL, ALT and SHIFT keys are allowed)");
			}	
		}
		catch (Exception ex) 
		{			
			System.out.println("Class ActionKeywords | Method keyUp | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void keyDown(String value)
	{
		try
		{
			logger.info("Trying to press and hold the key: " + value);
			if(value.equalsIgnoreCase(Keys.CONTROL.name()) ||  value.equalsIgnoreCase(Keys.ALT.name()) || value.equalsIgnoreCase(Keys.SHIFT.name()))
			{
				Actions action = new Actions(driver);
				action.keyDown(value.equalsIgnoreCase(Keys.CONTROL.name()) ? Keys.CONTROL : (value.equalsIgnoreCase(Keys.ALT.name())? Keys.ALT : Keys.SHIFT)).perform();
				logger.pass("Key down event performed successfully for key: " + value);
			}
			else
			{
				logger.fail("Failed to perform Key down event for key: " + value + " (Only CONTROL, ALT and SHIFT keys are allowed)");
			}					
		}
		catch (Exception ex) 
		{			
			System.out.println("Class ActionKeywords | Method keyDown | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void validateElementText(WebElement element, String elementName, String value)
	{
		try
		{
			logger.info("Trying to locate presence of " + elementName);
			if(element != null)
			{
				logger.info("Successfully located presence of " + elementName);
				if(element.getText().equalsIgnoreCase(value.trim()))
				{
					logger.pass("Element text matched for element: " + element.getText());
				}
				else
				{
					KD_Utils.logFailureAndTakeScreenshot(driver, "Element text not matched. Actual: " + element.getText() + ", Expected: " + value, elementName);
				}
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}			
		}
		catch(Exception ex)
		{
			System.out.println("Class ActionKeywords | Method validateElementText | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void validateAttributeText(WebElement element, String elementName, String value)
	{
		try
		{
			logger.info("Trying to verify presence of " + elementName);
			if(element != null)
			{
				logger.info("Successfully verified presence of " + elementName);
				String attributeName = "", attributeValue = "";
				attributeName = value.split("::", 2)[0];
				attributeValue = value.split("::", 2)[1];
				if(element.getAttribute(attributeName).contains(attributeValue))
				{
					logger.pass("Attribute text matched for element: " + elementName);
				}
				else
				{
					KD_Utils.logFailureAndTakeScreenshot(driver, "Element attribute text not matched. Actual: " + element.getAttribute(attributeName) + ", Expected: " + attributeValue, elementName);
				}
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}
		} 
		catch (Exception ex) 
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "validateAttributeText");
			System.out.println("Class ActionKeywords | Method validateAttributeText | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void validateAttributeTextForItemsInList(List<WebElement> elementList, String elementName, String value)
	{
		String attributeName = "", attributeValue = "";
		try
		{
			logger.info("Trying to locate " + elementName);
			if(elementList != null && elementList.size() > 0)
			{
				logger.info("Successfully located " + elementName);
				attributeName = value.split("::", 2)[0];
				attributeValue = value.split("::", 2)[1];
				String missingOptions = KD_Utils.compareOptions(attributeValue.split(";"), elementList, attributeName);
				if(missingOptions.equalsIgnoreCase(""))
				{
					logger.pass("All Options available");
				}
				else
				{
					KD_Utils.logFailureAndTakeScreenshot(driver, "Some options missing. Missing options are: " + missingOptions, elementName);
				}
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName, elementName);
			}
		}
		catch (Exception ex) 
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "validateAttributeTextForItemsInList");
			System.out.println("Class ActionKeywords | Method validateAttributeTextForItemsInList | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void displayListItems(List<WebElement> elementList, String elementName, String value)
	{
		String displayItems = "";
		try
		{
			logger.info("Trying to locate " + elementName + " Options");
			if(elementList != null && elementList.size() > 0)
			{
				logger.info("Successfully located " + elementName + " Options");				
				for(WebElement ele: elementList)
				{
					if(displayItems == "")
						displayItems = ele.getText();
					else
						displayItems = displayItems + ";" + ele.getText();
				}
				logger.info("List items: " + displayItems.replace("<", "").replace(">", ""));
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName + " Options", elementName);
			}
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "displayListItems");
			System.out.println("Class ActionKeywords | Method displayListItems | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void verifyDropdownOptions(List<WebElement> elementList, String elementName, String value)	
	{
		try
		{
			logger.info("Trying to locate " + elementName + " Options");
			if(elementList != null && elementList.size() > 0)
			{
				logger.info("Successfully located " + elementName + " Options");
				logger.info("Verifying " + elementName + " Options");
				String missingOptions = KD_Utils.compareOptions(value.split(";"), elementList);
				if(missingOptions.equals(""))
				{
					logger.info("All options available");											
				}
				else
				{
					KD_Utils.logFailureAndTakeScreenshot(driver, "Missing options: " + missingOptions, "ManageExternalUserOptions");
				}
			}
			else
			{
				KD_Utils.logFailureAndTakeScreenshot(driver, "Unable to locate " + elementName + " Options", elementName);
			}
		}
		catch(Exception ex)
		{
			KD_Utils.logFailureAndTakeScreenshot(driver, "Some unhandled exception occured: " + ex.getMessage(), "verifyDropdownOptions");
			System.out.println("Class ActionKeywords | Method verifyDropdownOptions | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void takeScreenshot(String value)	
	{
		try 
		{
			logger.info("Taking Screenshot", MediaEntityBuilder.createScreenCaptureFromPath(KD_Utils.takeScreenShot(driver, value)).build());			
		} 
		catch (Exception ex) 
		{			
			System.out.println("Class ActionKeywords | Method takeScreenshot | Exception desc : " + ex.getMessage());
		} 
	}
	
	public static void maximizeWindow(String value)
	{
		try 
		{
			if(driver != null)
			{
				driver.manage().window().maximize();
				logger.pass("Successfully maximized current window");
			}
			else
				logger.fail("Driver object is null");
		} 
		catch (Exception ex) 
		{			
			System.out.println("Class ActionKeywords | Method maximizeWindow | Exception desc : " + ex.getMessage());
		}
	}
	
	public static void closeActiveWindow(String value)
	{
		try 
		{
			driver.close();
		} 
		catch (Exception ex) 
		{			
			System.out.println("Class ActionKeywords | Method closeActiveWindow | Exception desc : " + ex.getMessage());
		} 
	}
	
	public static void wait(String value) 
	{
		try 
		{
			Thread.sleep((int) Double.parseDouble(value));
		} 
		catch (Exception ex) 
		{			
			System.out.println("Class ActionKeywords | Method wait | Exception desc : " + ex.getMessage());
		} 
	}
	
	public static void alert_Accept(String val)
	{
		alertActions(alert.Accept);
	}
	
	public static void alert_Dismiss(String val)
	{
		alertActions(alert.Dismiss);
	}
	
	private static String alertActions(alert actionToPerform)
	{
		String strRetVal = "";
		try
		{
			Alert objAlert = driver.switchTo().alert();			
			switch (actionToPerform) 
			{
				case Accept:
					objAlert.accept();					
					break;
				case Dismiss:
					objAlert.dismiss();					
					break;
				case GetText:
					strRetVal = objAlert.getText();
					break;
			}
		}
		catch(Exception ex)
		{
			System.out.println("No Alert Present");
		}
		return strRetVal;
	}
}
