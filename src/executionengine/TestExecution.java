package executionengine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;

import utilities.BaseClass;
import utilities.BaseClass.sheetType;
import utilities.KDConstants;
import utilities.KD_Utils;

public class TestExecution
{
	static WebElement element = null;
	static List<WebElement> elementList = null;
	static LinkedHashMap<String, Object[]> scenarios = new LinkedHashMap<String, Object[]>();
	public static ActionKeywords actionKeywords;
	public static Method method[];
	static List<Map<String, String>> dataSetRcursive = new ArrayList<Map<String, String>>();
	static Map<String, String> dataMap = new HashMap<String, String>();
	public static void triggerTestExecution()
	{
		int dataSetSize = 1;
		try
		{
			actionKeywords = new ActionKeywords();
			method = actionKeywords.getClass().getMethods();
			
			scenarios = KD_Utils.getScenarios();
			
			for (Map.Entry<String, Object[]> entry : scenarios.entrySet()) 
			{
				//Get Scenario info
				KDConstants.scenarioID = entry.getKey();
			    Object[] value = entry.getValue();
			    KDConstants.scenarioName = (String)value[1];	
			    KDConstants.blnRunMode = ((String) value[2]).equalsIgnoreCase("yes") ? true : false;
			    KDConstants.blnRecursive = ((String) value[3]).equalsIgnoreCase("yes") ? true : false;
			    KDConstants.sheetName_Recursive = (String) value[4];
			    
			    if(KDConstants.blnRunMode)
			    {
			    	dataSetRcursive.clear();
			    	BaseClass.logger = KD_Utils.report.createTest(KDConstants.scenarioName);
			    	
			    	if(KDConstants.blnRecursive)
			    		dataSetRcursive = ExcelOperations.getDataSet(sheetType.DataSet);
			    	
			    	if(dataSetRcursive.size() > 0)
			    		dataSetSize = dataSetRcursive.size();
			    	else
			    		dataSetSize = 1;
			    	
			    	for(int i = 0; i < dataSetSize; i++)
			    	{			    		
			    		Object[][] testCases = KD_Utils.getTestCasesForEachScenario(KDConstants.scenarioID);
			    		if(KDConstants.blnRecursive)
			    			dataMap = dataSetRcursive.get(i);
				    	for(int j = 0; j < testCases.length; j++)
				    		processTestSteps(testCases[j]);
			    	}
			    }			    			    
			}
		}
		catch(Exception ex)
		{
			System.out.println("Class TestExecution | Method triggerTestExecution | Exception desc : " + ex.getMessage());
		}		
	}
	
	private static void processTestSteps(Object[] testSteps)
	{
		element = null;
		elementList = null;
		boolean blnElementNeeded = false;
		String strValue = "";
		try
		{
			if(testSteps[0] != null)
			{
				if(!((String)testSteps[3]).contentEquals("-"))
				{
					if(((String)testSteps[4]).contains("{List}::"))
						elementList = KD_Utils.processElementListBasedOnLocator(BaseClass.driver, String.valueOf(testSteps[3]), ((String)testSteps[4]).replace("{List}::", "").trim());
					else
						element = KD_Utils.processWebElementBasedOnLocator(BaseClass.driver, String.valueOf(testSteps[3]), String.valueOf(testSteps[4]));
					blnElementNeeded = true;
				}
				
				//If recursive, get data from data set or get data from cell
				strValue = KDConstants.blnRecursive? dataMap.get(String.valueOf(testSteps[2])): String.valueOf(testSteps[6]);
				
				for(int i = 0; i < method.length; i++)
				{								
					if(method[i].getName().equals((String)testSteps[5]))
					{
						if(!blnElementNeeded)
							method[i].invoke(actionKeywords, strValue); 
						else
						{
							if(elementList != null)
								method[i].invoke(actionKeywords, elementList, String.valueOf(testSteps[2]), strValue);
							else
								method[i].invoke(actionKeywords, element, String.valueOf(testSteps[2]), strValue);
						}
						break;
					}
				}
			}			
		}
		catch (Exception ex) 
		{
			System.out.println("Class TestExecution | Method processTestSteps | Exception desc : " + ex.getMessage());
		}
	}
}

