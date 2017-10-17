package executionengine;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utilities.BaseClass.sheetType;
import utilities.KDConstants;

public class ExcelOperations 
{
	private static XSSFSheet ExcelWSheet;
	private static XSSFWorkbook ExcelWBook;	

	private static void setExcelFile(sheetType sheetInfo)
	{
		try 
		{					
			FileInputStream ExcelFile = new FileInputStream(KDConstants.strScenariosFilePath);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
			switch (sheetInfo) 
			{
				case Scenarios:
					ExcelWSheet = ExcelWBook.getSheet(KDConstants.strScenariosSheetName);
					break;
				case TestCases:
					ExcelWSheet = ExcelWBook.getSheet(KDConstants.strTestCasesSheetName);
					break;
				case DataSet:
					ExcelWSheet = ExcelWBook.getSheet(KDConstants.sheetName_Recursive);					
					break;
			}			
		}
		catch (Exception ex) 
		{
			System.out.println("Class ExcelOperations | Method setExcelFile | Exception desc : " + ex.getMessage());
		}
	}
	
	public static List<Map<String, String>> getDataSet(sheetType sheetInfo)
	{
		int row = 0, col = 0;
		boolean blnKeys = false;
		List<Map<String, String>> dataSet = new ArrayList<Map<String,String>>();		
		Map<String, String> data = null;
		try
		{
			setExcelFile(sheetInfo);
			Iterator<Row> iterator = ExcelWSheet.iterator();
			String[] arrayKeys = new String[ExcelWSheet.getRow(0).getPhysicalNumberOfCells()]; //To store Map keys
			while (iterator.hasNext()) 
			{	
				data = new HashMap<String, String>();
				if(row == 0)
					blnKeys = true;
				else
					blnKeys = false;
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				col = 0;
				while (cellIterator.hasNext())
				{
					Cell cell = cellIterator.next();
					if(cell.getCellTypeEnum() != CellType.BLANK)
					{
						cell.setCellType(CellType.STRING);
						if(blnKeys)
							arrayKeys[col] = cell.getStringCellValue();
						else
							data.put(arrayKeys[col], cell.getStringCellValue());
						col++;
					}					
				}
				if(row != 0 && data.size() > 0)
					dataSet.add(data);
				row++;
			}
		}
		catch(Exception ex)
		{
			System.out.println("Class ExcelOperations | Method getDataSet | Exception desc : " + ex.getMessage());
		}
		return dataSet;
	}
	
	private static int getNumberOfTestCasesForScenario(String strScenarioID)
	{
		int intRetVal = 0;
		boolean blnScenarioFound = false;
		try
		{
			Iterator<Row> iterator = ExcelWSheet.iterator();
			while (iterator.hasNext()) 
			{
				Row nextRow = iterator.next();				
				Cell cell = nextRow.getCell(0);	
				if(cell != null)
					cell.setCellType(CellType.STRING);
				if(cell != null && cell.getStringCellValue().equalsIgnoreCase(strScenarioID))
				{
					blnScenarioFound = true;
					intRetVal++;
				}
				else if(blnScenarioFound && (cell == null || cell.getCellTypeEnum() == CellType.BLANK || cell.getStringCellValue().equals("")))
				{
					intRetVal++;
				}
				else if(blnScenarioFound)
				{
					break;
				}				
			}
		}
		catch(Exception ex)
		{
			System.out.println("Class ExcelOperations | Method getNumberOfTestCasesForScenario | Exception desc : " + ex.getMessage());
		}
		return intRetVal;
	}
	
	public static Object[][] readDataFromExcel(sheetType sheetInfo, String strScenarioID)
	{
		boolean blnProcessRow = false;
		int rowsCount = 0, colsCount = 0, row = 0, col = 0;
		Object[][] excelData = null;
		try
		{			
			setExcelFile(sheetInfo);
			Iterator<Row> iterator = ExcelWSheet.iterator();
			
			if(sheetInfo == sheetType.TestCases)
				rowsCount = getNumberOfTestCasesForScenario(strScenarioID);
			else
				rowsCount = ExcelWSheet.getPhysicalNumberOfRows() - 1; //Exclude first row as it contains heading		
			
			colsCount = ExcelWSheet.getRow(0).getPhysicalNumberOfCells();
			excelData = new Object[rowsCount][colsCount];
			while (iterator.hasNext()) 
			{
				if(row == 0)
				{
					row++;
					iterator.next();
					continue;
				}
				
				Row nextRow = iterator.next();
				
				if(sheetInfo == sheetType.TestCases)
				{
					if(blnProcessRow && nextRow.getCell(0) == null)
					{
						col = 0;
						blnProcessRow = true;
					}
					else if (blnProcessRow && (nextRow.getCell(0).getCellTypeEnum() == CellType.BLANK || nextRow.getCell(0).getStringCellValue().equals(""))) 
					{
						col = -1;
						blnProcessRow = true;
					}
					else if(nextRow.getCell(0) != null && nextRow.getCell(0).toString().equalsIgnoreCase(strScenarioID))
					{
						col = -1;
						blnProcessRow = true;
					}
					else
					{
						blnProcessRow = false;
					}
				}
				else if(sheetInfo == sheetType.Scenarios)
				{
					col = 0;
					blnProcessRow = true;
				}
				
				if(blnProcessRow)
				{
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					while (cellIterator.hasNext())
					{	
						if(sheetInfo == sheetType.TestCases && col == -1 && nextRow.getCell(0) != null) // To skip scenario ID from testcases sheet
						{
							col = 0;
							cellIterator.next();
							continue;							
						}
						/*else if(sheetInfo == sheetType.TestCases && col == -1 && nextRow.getCell(0).getCellTypeEnum() == CellType.BLANK)
						{
							col = 0;
							cellIterator.next();
							continue;
						}*/
						
						Cell cell = cellIterator.next();
						switch (cell.getCellTypeEnum()) 
						{
							case STRING:
								excelData[row - 1][col] = cell.getStringCellValue();
								break;
							case BOOLEAN:
								excelData[row - 1][col] = cell.getBooleanCellValue();
								break;
							case NUMERIC:
								excelData[row - 1][col] = cell.getNumericCellValue();
								break;
							default:
								break;
						}					
						col++;
					}
					row++;
				}
			}	
		}
		catch(Exception ex)
		{
			System.out.println("Class ExcelOperations | Method readDataFromExcel | Exception desc : " + ex.getMessage());
		}		
		return excelData;
	}
}
