package test;

import org.testng.annotations.Test;
import executionengine.TestExecution;
import utilities.BaseClass;

public class TriggerKeywordDrivenFramework extends BaseClass
{	
	@Test
	private void triggerAutomation()
	{
		TestExecution.triggerTestExecution();
	}
}

