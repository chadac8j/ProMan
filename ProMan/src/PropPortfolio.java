/*
 *  Copyright 2014 LandLord Innovations
 *  author: Chad A. Cole
 *
 *  The Joda software library was utilized in this work, and it is
 *  Licensed under the Apache License, Version 2.0 (the "License").
 *  
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.TreeSet;

import org.joda.time.LocalDate;

import java.io.BufferedWriter;
//import java.time.LocalDate;
//import java.time.Month;

//import Unit;

public class PropPortfolio implements Serializable {
    final String DELIMITER = "\\|";
	private int id;
	private int numunits = 0;
	private int reportNum = 0;
	private String name;
	private String phoneNum;
	private String email;
	private double manageFee;
	private String clientInfo = "None entered yet";
	private List<String> buildingNames = new ArrayList<String>();
	private TreeSet<String> capExYears = new TreeSet<String>();
	private TreeSet<String> revYears = new TreeSet<String>();
	private TreeSet<String> expYears = new TreeSet<String>();
	private List<Expense> expenses = new ArrayList<Expense>();
	private List<Revenue> revenues = new ArrayList<Revenue>();
	private Map<String, Building> buildings = new HashMap<String, Building>();
	//size of double array is rent/managementfee/maint/repair/utility/legal/SUPPLIES/CLEANING/?
	private Map<String, double[]> reportStruct = new HashMap<String, double[]>();
//	private List<String> indexList = new ArrayList<String>();
//	private TreeSet<String> indexList = new TreeSet<String>(Collator.getInstance());
	private TreeSet<String> indexList = new TreeSet<String>();
	private List<String> indexListSmall = new ArrayList<String>();
	private List<String> indexBuildingUnitSameOrder = new ArrayList<String>();
	private Map<String, Integer> yearIndices = new HashMap<String, Integer>();
	// keep track of which rents/expenses are associated with which report, binned by year
/*	private Map<Integer, Integer> yearReportCounts = new HashMap<Integer, Integer>() {{
		put(LocalDate.now().getYear(), 0); put(LocalDate.now().getYear()+1, 0); put(LocalDate.now().getYear()+2, 0);
		put(LocalDate.now().getYear()+3, 0); put(LocalDate.now().getYear()+4, 0);	}}; */
//	private double [][] values = new double [][];
	public static final Map<String, Integer> mapIndices = new HashMap<String, Integer>() {{
		put("RENT", 0); put("MANAGEFEE", 1); put("MAINTENANCE", 2);
		put("REPAIR", 3); put("UTILITY", 4); 
		put("SUPPLIES", 5); put("CLEANING", 6); 
		put("LEASING", 7); put("LEGAL", 8); put("TRAVEL", 9);}}; 
	public static final Map<String, Integer> clientIndices = new HashMap<String, Integer>() {{
			put("NUMUNITS", 0); put("MANAGEFEE", 1); put("EMAIL", 2);
			put("PHONENUM", 3);  }};
	public static final List<String> indexHeaderList = Arrays.asList(
			"RENT", "MANAGEFEE", "MAINTENANCE",
			"REPAIR", "UTILITY", 
			"SUPPLIES", "CLEANING",
			"LEASING", "TRAVEL", "LEGAL");
//			"LEASING"); //put("TRAVEL", 9); "LEGAL",
	public static final List<String> indexHeaderListTotal = Arrays.asList(
			"RENT", "MANAGEFEE", "MAINTENANCE",
			"REPAIR", "UTILITY",  "LEGAL",
			"SUPPLIES", "CLEANING",
			"TRAVEL", "LEASING");
	private final int numColumns = indexHeaderListTotal.size();
	
	public String getData(String dataType) {
		if (dataType.equals("NUMUNITS")) {
			return Integer.toString(numunits);
		}
		else if (dataType.equals("MANAGEFEE")) {
			return Double.toString(manageFee);
		}
		else if (dataType.equals("EMAIL")) {
			return email;
		}
		else if (dataType.equals("PHONENUM")) {
			return phoneNum;
		}
		return "Invalid header";
	}
	public String setData(String dataType, String dataIn) {
		if (dataType.equals("NUMUNITS")) {
		
			// Will I ever use this since it's calculated automatically upon load?
		}
		else if (dataType.equals("MANAGEFEE")) {
			manageFee = Double.parseDouble(dataIn);
			manageFee = manageFee/100.0;
		}
		else if (dataType.equals("EMAIL")) {
			email = dataIn;
		}
		else if (dataType.equals("PHONENUM")) {
			phoneNum = dataIn;
		}
		return "Invalid header";
	}
	public double getManageFee() {
		return manageFee;
	}
	
	public List<OpenItem> getUnitItems(String buildingUnit){
		String [] splitArray = buildingUnit.split("\\s+");
		if (splitArray[0].equalsIgnoreCase("PORTFOLIO")) {
			List<OpenItem> openItemList = new ArrayList<OpenItem>();
			return openItemList; //dummy value (no items)
		}
		Building tempBuilding = buildings.get(splitArray[0]);
		Map<String, Unit> tempMap = tempBuilding.getUnits();
		if (splitArray.length == 1 && tempMap.size() > 1) { //building level info on multi-family 
			List<OpenItem> openItemList = new ArrayList<OpenItem>();
			return openItemList; //dummy value (no items)
		}
		Unit tempUnit;
		if (splitArray.length == 1) { // not multi-family
			tempUnit = tempMap.get("");
		}
		else {
			tempUnit = tempMap.get(splitArray[1]); }
		
		return tempUnit.getItemList();
	}

	public void setUnitItems(String buildingUnit, List<OpenItem> inList){
		String [] splitArray = buildingUnit.split("\\s+");
		if (splitArray[0].equalsIgnoreCase("PORTFOLIO")) {
			return;  //currently don't allow portfolio open items
		}
		Building tempBuilding = buildings.get(splitArray[0]);
		Map<String, Unit> tempMap = tempBuilding.getUnits();
		if (splitArray.length == 1 && tempMap.size() > 1) { //building level info on multi-family 
			return;  // may do something else later
		}
		Unit tempUnit;
		if (splitArray.length == 1) { // not multi-family
			tempUnit = tempMap.get("");
		}
		else {
			tempUnit = tempMap.get(splitArray[1]); }
		
		tempUnit.setItemList(inList);
	}

	public void setUnitLevelInfo(String strField, String buildingUnit, Object data) {
		// split building/unit
		
		String [] splitArray = buildingUnit.split("\\s+");
		if (splitArray[0].equalsIgnoreCase("PORTFOLIO")) {
			return;
		}
		Building tempBuilding = buildings.get(splitArray[0]);
		Map<String, Unit> tempMap = tempBuilding.getUnits();
		Unit tempUnit;
		if (splitArray.length == 1 && tempMap.size() > 1) { //building level info on multi-family 
			return;  // may do something else later
		}
		else if (splitArray.length == 1) {// not multi-family
			tempUnit = tempMap.get("");
		}
		else {
			tempUnit = tempMap.get(splitArray[1]); }
		//Tenant tempTenant = tempUnit.getTenant();
		
		if (strField.equals("TENANT")) {
			tempUnit.setTenant((Tenant)data);
		}
		else if (strField.equals("RENT")) {
			tempUnit.setRent((Double)data);
		}
		else if (strField.equals("DESCR")) {
			tempUnit.setDescr((String)data);
		}
/*		else if (strField.equals("DEPOSIT")) {
			tempUnit.setDeposit((Double)data);
		}
		else if (strField.equals("PHONENUM")) {
			tempUnit.setPhoneNum((String)data);
		}
		else if (strField.equals("EMAIL")) {
			tempUnit.setEmail((String)data);
		}
		else if (strField.equals("MOVEIN")) {
			tempUnit.setMoveInDate((LocalDate)data);
		}
		else if (strField.equals("LEASEEND")) {
			tempUnit.setLeaseEnd((String)data);
		}
		else if (strField.equals("BALANCE")) {
			tempUnit.setBalanceForward((double)data);
		} */
		else if (strField.equals("ITEM")) {
			OpenItem newItem = new OpenItem();
			String [] splitArray2 = ((String)data).split(DELIMITER);
			if (splitArray2[0].length() > 2)
				newItem.setDueDate(splitArray2[0]);
			newItem.setItemDescr(splitArray2[1]);
			newItem.setExpenseCategory(splitArray2[2]);
			newItem.setServicer(splitArray2[3]);
			
			tempUnit.addItem(newItem);
		}
		// add back unit to Map?  i.e. does get() pass by value or ref? seems to pass by ref... don't need to add back to map
		//tempMap.put(splitArray[1], tempUnit);
		//tempBuilding.setUnits(tempMap);
		//buildings.put(splitArray[0], tempBuilding);
	}

	public Object getUnitLevelInfo(String strField, String buildingUnit) {
		// split building/unit 
		String [] splitArray = buildingUnit.split("\\s+");
		if (splitArray[0].equalsIgnoreCase("PORTFOLIO")) {
			if (strField.equals("TENANT")) {
				Tenant dummyTenant = TenantManager.createDummyTenant();
				return dummyTenant; // Tenant object type
			}
			else if (strField.equals("RENT")) {
				return 0.0; //double
			}
			else if (strField.equals("DESCR")) {
				return "";
			}
		}
		Building tempBuilding = buildings.get(splitArray[0]);
		Map<String, Unit> tempMap = tempBuilding.getUnits();
		Unit tempUnit;
		if (splitArray.length == 1 && tempMap.size() > 1) { //building level info on multi-family
			tempUnit = null;
			// may do something else later
			if (strField.equals("TENANT")) {
				Tenant dummyTenant = TenantManager.createDummyTenant();
				return dummyTenant; // Tenant object type
			}
			else if (strField.equals("RENT")) {
				return 0.0; //double
			}
			else if (strField.equals("DESCR")) {
				return "";
			}
		}
		else if (splitArray.length == 1) {// not multi-family
			tempUnit = tempMap.get("");
		}
		else {
			tempUnit = tempMap.get(splitArray[1]); }
		
		if (strField.equals("TENANT")) {
			return tempUnit.getTenant(); // Tenant object type
		}
		else if (strField.equals("RENT")) {
			return tempUnit.getRent(); //double
		}
		else if (strField.equals("DESCR")) {
			return tempUnit.getDescr();
		}
/*		else if (strField.equals("PHONENUM")) {
			return tempUnit.getPhoneNum();
		}
		else if (strField.equals("EMAIL")) {
			return tempUnit.getEmail();
		}
		else if (strField.equals("MOVEIN")) {
			return tempUnit.getMoveInDate().toString();
		}
		else if (strField.equals("LEASEEND")) {
			return tempUnit.getLeaseEnd();
		}
		else if (strField.equals("DEPOSIT")) {
			return Double.toString(tempUnit.getDeposit());
		}
		else if (strField.equals("BALANCE")) {
			return Double.toString(tempUnit.getBalanceForward());
		} */
		else if (strField.equals("ITEM")) {
		}
		return "Nothing returned";
	}
	public Object [][] genRentReport() {
			int curMonth = LocalDate.now().getMonthOfYear();
			int offset = curMonth - 5;
			String [] strArray = new String[6];
			int ind = 0;
			Map<Integer, Integer> mapArrayInd = new HashMap<Integer, Integer>();
			for (int i = curMonth; i > curMonth-6; i--) {
				strArray[5-ind] = Unit.mapMonths.get((i-1)%12);
				mapArrayInd.put(i%12, 5-ind);
				ind++;
			}
			LocalDate cutoffDate = new LocalDate().now();
			LocalDate finalDate = new LocalDate().now();
			finalDate = finalDate.plusMonths(1);
			finalDate = new LocalDate(finalDate.getYear()+"-"+finalDate.getMonthOfYear()+"-01");
			cutoffDate = cutoffDate.minusMonths(5);
			int dayOfMonth = cutoffDate.getDayOfMonth();
			cutoffDate = cutoffDate.minusDays(dayOfMonth);
			System.out.println("new date: " + cutoffDate.toString());
			Map<String, double[]> rentStruct = new HashMap<String, double[]>();
			for (String buildkey : buildingNames) {
				List<String> tempListStr = new ArrayList<String>(buildings.get(buildkey).getUnits().keySet());
				for (String unitkey : tempListStr) {
					if (isUnit(buildkey + "  " + unitkey)) {
     					double [] doubleArray = new double[6];
	    				Arrays.fill(doubleArray, 0.0);
		    			rentStruct.put(buildkey + unitkey, doubleArray);
					}
				}
			}
			Iterator<Revenue> revenueIt = this.revenues.iterator();
			double totalRev = 0.0;
			String curBuildingUnit = "";
			double [] tempArray = new double[6];
			Arrays.fill(tempArray, 0.0);
			while (revenueIt.hasNext()) {
				Revenue revenue = revenueIt.next();
				curBuildingUnit = revenue.getBuilding()+revenue.getUnit();
				if (isUnit(revenue.getBuilding()+" "+revenue.getUnit()) &&
						revenue.getDate().isAfter(cutoffDate) && 
						revenue.getDate().isBefore(finalDate)) {
					// add rent
					tempArray = rentStruct.get(curBuildingUnit);
					System.out.println("\n index: " + revenue.getDate().getMonthOfYear()%12 + "  " + mapArrayInd.get(revenue.getDate().getMonthOfYear()%12));
					tempArray[mapArrayInd.get(revenue.getDate().getMonthOfYear()%12)] += revenue.getAmount();
					rentStruct.put(curBuildingUnit, tempArray);
					totalRev += revenue.getAmount();
				}
			}

		// The size of this object isn't quite right - overprovision for worst possible case of all single family
		Object[][] returnVal = new Object[2*reportStruct.size()+1][7];
		for (int i = 0; i < returnVal.length; i++) { //must initialize to something to avoid later null pointer exceptions
			for (int j = 0; j < returnVal[i].length; j++){
				returnVal[i][j] = ""; //init to blank
			}
		}
		// show month headers
		returnVal[0][0] = "BUILDING/UNIT";
		for (int j = 1; j <= strArray.length; j++){
			returnVal[0][j] = strArray[j-1];
		}
		int indrow = 1;
		for (String buildkey : buildingNames) {
			List<String> tempListStr = new ArrayList<String>(buildings.get(buildkey).getUnits().keySet());
			if (buildings.get(buildkey).getUnitsList().size() > 1) {
//				tempListStr.add(buildkey);
				returnVal[indrow][Unit.tenantBuildingHeaders.get("BUILDING/UNIT")] = buildkey;
			}
			indrow += 1;
			Collections.sort(tempListStr);
			for (String unitkey : tempListStr) {
				if (isUnit(buildkey + "  " + unitkey)) {
					tempArray = rentStruct.get(buildkey + unitkey);
					returnVal[indrow][0] = buildkey + "  " + unitkey;
					for (int j = 1; j < 7; j++){
						returnVal[indrow][j] = String.format("%1$,.2f", tempArray[j-1]);
					}
				}
				indrow += 1;
			}
			indrow += 1;  //put space between buildings
		}
		returnVal[indrow][Unit.tenantBuildingHeaders.get("BUILDING/UNIT")] = "PORTFOLIO";
		
		return returnVal;
		
	}

//	public Object [][] genCapExReport(LocalDate begDate, LocalDate endDate) {
		public Object [][] genCapExReport(int capExYear) {
	// calculate double declining balance formula
	// Uses 5-yr, mid-quarter IRS GDS convention
		// currently ignore incoming date range and print all capex in current/previous years

/*			
	LocalDate startDate = LocalDate.now();
	int prevYearInt = startDate.getYear()-1;
	String prevYear = Integer.toString(prevYearInt); //when doing taxes, usually interested in previous year
	int curYearInt = startDate.getYear();
	String curYear = Integer.toString(curYearInt);
	yearIndices.put(prevYear, 0);
	yearIndices.put(curYear, 1);
	yearIndices.put(Integer.toString(startDate.getYear()+1), 2);
	yearIndices.put(Integer.toString(startDate.getYear()+2), 3);
	yearIndices.put(Integer.toString(startDate.getYear()+3), 4);
	yearIndices.put(Integer.toString(startDate.getYear()+4), 5);
	yearIndices.put(Integer.toString(startDate.getYear()+5), 6);
*/
	TreeSet<String> yearIndices = new TreeSet<String>();
	yearIndices.add(Integer.toString(capExYear));
	yearIndices.add(Integer.toString(capExYear+1));
	yearIndices.add(Integer.toString(capExYear+2));
	yearIndices.add(Integer.toString(capExYear+3));
	yearIndices.add(Integer.toString(capExYear+4));
	yearIndices.add(Integer.toString(capExYear+5));
	
	LocalDate startDate = new LocalDate();
	double initBalance = 0.0;
	int quarter = 0;
	double [] tempDouble = new double[numColumns];
	double [] tempDoubleTotal = new double[numColumns];
	double numYearsDepr = 5;  //use 5-yr depreciation
	double numYearsLeftDepr = 5;  //this keeps track of straight-line depreciation rate
	double cutOffValue = 0.0; 
	// firstYear is initialized with first year depreciation and then used for later years
	double firstYear = 0.0;
	double accumDep = 0.0; 
	int numCapitalizeItems = 0;
//	this.resetReportStruct(); This was done in main calling Class
	Iterator<Expense> expensesIt = this.expenses.iterator();
	while (expensesIt.hasNext()) {
		Expense expense = expensesIt.next();
/*		if (expense.getCapitalize().equals("YES") && 
				(expense.getDate().isAfter(begDate) && expense.getDate().isBefore(endDate) ||
						expense.getDate().isEqual(begDate) || expense.getDate().isEqual(endDate)) ) { */
			if (expense.getCapitalize().equalsIgnoreCase("YES") && 
					(expense.getDate().getYear() == capExYear) ) {
			numCapitalizeItems += 1;
			tempDouble = reportStruct.get(expense.getBuilding()+expense.getUnit());
			Building tempBuilding;
			if (!expense.getBuilding().equalsIgnoreCase("PORTFOLIO")) {
			tempBuilding = buildings.get(expense.getBuilding());
  		    if (tempBuilding.getUnitsList().size() > 1)  //this rules out SFH 
			{
				tempDoubleTotal = reportStruct.get(expense.getBuilding()+"_TOTAL");
			}
			}
			accumDep = 0.0;
			startDate = expense.getDate();
			quarter = (int)(4*((double)(startDate.getMonthOfYear()-1)/12)+1);
			numYearsLeftDepr = numYearsDepr; 
			initBalance = expense.getAmount();
			firstYear = initBalance*(2.0/numYearsDepr)*((double)(9- 2*quarter))/8.0;
			int index = 0;
			while (firstYear > 0.01) {
				if (numYearsLeftDepr == 5) {
					cutOffValue = (initBalance - accumDep)/numYearsLeftDepr*((double)(9- 2*quarter))/8.0;
				} else {
					cutOffValue = (initBalance - accumDep)/numYearsLeftDepr;
				}
				if (firstYear < cutOffValue) {
					firstYear = cutOffValue;
					if (cutOffValue > (initBalance - accumDep)) {
						firstYear = (initBalance - accumDep);
					}
				}
//				System.out.print("cutoff value is:  " + Double.toString(cutOffValue) + "  \n");
//				System.out.print("num years left is:  " + Double.toString(numYearsLeftDepr) + "  \n");
				if (numYearsLeftDepr == 5) {
					numYearsLeftDepr -= ((double)(9- 2*quarter))/8.0;
				} else {
					numYearsLeftDepr -= 1;
				}
				accumDep += firstYear;
//				System.out.print("Accum Dep: " + Double.toString(accumDep) + "  \n");
				System.out.print(Double.toString(firstYear) + "  \n");
//				tempDouble[yearIndices.get(Integer.toString(startDate.getYear()+index))] += firstYear;
				tempDouble[index] += firstYear;
				tempDoubleTotal[index] += firstYear;
				index += 1;

				
				
				firstYear = (initBalance - accumDep)*(2.0/(double)numYearsDepr);
			}
			reportStruct.put(expense.getBuilding()+expense.getUnit(), tempDouble);
			if (!expense.getBuilding().equalsIgnoreCase("PORTFOLIO")) {
			tempBuilding = buildings.get(expense.getBuilding());
  		      if (tempBuilding.getUnitsList().size() > 1)  //this rules out SFH 
			  {
				reportStruct.put(expense.getBuilding()+"_TOTAL", tempDoubleTotal);
			  }
			}
			
			
			
	/*		
//be sure to add up total building expenses
			if (expense.getBuilding().length() > 0)  //this rules out expenses not linked to individual building 
			{
				System.out.println(expense.getType()); // Print the data line.
				tempDouble = reportStruct.get(expense.getBuilding()+"_TOT");
				tempDouble[mapIndices.get(expense.getType())] += expense.getAmount();
				reportStruct.put(expense.getBuilding()+"_TOT", tempDouble);
			}  */
		}
	}

	
	System.out.print("Total number of capital items is:  " + Integer.toString(numCapitalizeItems) + "  \n");
	Object[][] returnVal = new Object[reportStruct.size()+2][numColumns+1];
	int ind2 = 0;
	returnVal[0][0] = "Building    ";
//	for (String key : yearIndices.keySet()) {
//	for (int i = 0; i < 6; i++ ) {
	for (String item: yearIndices) {	
		returnVal[0][ind2+1] = item;
		ind2+=1;
	}
	ind2=0;
    int ind3 = 0;
	for (String key : indexList) {
		double [] tempArray = reportStruct.get(key);
	    ind2 = 0;
		ind3 += 1;
		for (int i = 0; i < tempArray.length; i++) {
			ind2 += 1;
			returnVal[ind3][ind2] = String.format("%1$,.2f", tempArray[i]);
		}
	    returnVal[ind3][0] = key; 
	}
	returnVal[returnVal.length-1][0] = Integer.toString(numCapitalizeItems);
	return returnVal;
}

	public Object [][] genTenantBuildingReport() {
		
		// The size of this object isn't quite right - overprovision for worst possible case of all single family
		Object[][] returnVal = new Object[2*reportStruct.size()+1][Unit.tenantBuildingHeaders.size()];
		for (int i = 0; i < returnVal.length; i++) { //must initialize to something to avoid later null pointer exceptions
			for (int j = 0; j < returnVal[i].length; j++){
				returnVal[i][j] = ""; //init to blank
			}
		}

		// create header
	    System.out.println("object size:  " + Integer.toString(reportStruct.size()+1) + "   " + 
	    		Integer.toString(Unit.tenantBuildingHeaders.size())); 
		
		int indrow = 1;
//		for (String buildkey : buildings.keySet()) {
		for (String buildkey : buildingNames) {
			List<String> tempListStr = new ArrayList<String>(buildings.get(buildkey).getUnits().keySet());
			if (buildings.get(buildkey).getUnitsList().size() > 1) {
//				tempListStr.add(buildkey);
				returnVal[indrow][Unit.tenantBuildingHeaders.get("BUILDING/UNIT")] = buildkey;
			}
			indrow += 1;
			Collections.sort(tempListStr);
			for (String unitkey : tempListStr) {
				Map<String, Unit> unitMap = buildings.get(buildkey).getUnits();
				Unit curUnit = unitMap.get(unitkey);
				Tenant curTenant = curUnit.getTenant();
				returnVal[indrow][Unit.tenantBuildingHeaders.get("BUILDING/UNIT")] = buildkey + "  " + unitkey;
				returnVal[indrow][Unit.tenantBuildingHeaders.get("RENT")] = String.format("%1$,.2f", curUnit.getRent());
				returnVal[indrow][Unit.tenantBuildingHeaders.get("TENANT")] = curTenant.getTenant();
				returnVal[indrow][Unit.tenantBuildingHeaders.get("DESCRIPTION")] = curTenant.getDescr();
//				returnVal[indrow][Unit.tenantBuildingHeaders.get("DESCRIPTION")] = curUnit.getDescr();
				returnVal[indrow][Unit.tenantBuildingHeaders.get("PHONENUM")] = curTenant.getPhoneNum();
				returnVal[indrow][Unit.tenantBuildingHeaders.get("EMAIL")] = curTenant.getEmail();
				List<OpenItem> tempList = curUnit.getItemList();  //just get first item in list?
				if (!tempList.isEmpty()) {
				    System.out.println("indices:  " + Integer.toString(indrow) + "   "); 
					OpenItem tempItem = new OpenItem();
					tempItem = tempList.get(0); //get most recent item (why?)
					if (tempList.size() > 1) {
						returnVal[indrow][Unit.tenantBuildingHeaders.get("OPEN ITEMS")] = "(...) " + tempItem.getItemDescr();
					} else {
						returnVal[indrow][Unit.tenantBuildingHeaders.get("OPEN ITEMS")] = tempItem.getItemDescr();
					}
					returnVal[indrow][Unit.tenantBuildingHeaders.get("DATE DUE")] = tempItem.getDueDate().toString();
					returnVal[indrow][Unit.tenantBuildingHeaders.get("CONTRACTOR")] = tempItem.getServicer();
				}
				indrow += 1;
			}
			indrow += 1;  //put space between buildings
		}
		returnVal[indrow][Unit.tenantBuildingHeaders.get("BUILDING/UNIT")] = "PORTFOLIO";
		
		return returnVal;
	}
	
	
	
	
	
	public void genPortfolio(String fileIn, TenantManager inTenantManager) {
	  try {	
		  // Want to make all keys to buildings/units to be CAPITALIZED AND TRIMMED
		  BufferedReader CSVFile = new BufferedReader(new FileReader(fileIn));
		  String dataTitle = CSVFile.readLine(); // Read first line that is title, also MANAGEFEE info
		  String [] titleArray = dataTitle.split(DELIMITER, -1);
		  manageFee = Double.parseDouble(titleArray[1].trim());
		  phoneNum = titleArray[2].trim();
		  email = titleArray[3].trim();
		  reportNum = Integer.parseInt(titleArray[4].trim());
		  clientInfo = titleArray[5].trim();
		  String dataHeader = CSVFile.readLine(); // Read second line that is headers
		  String dataRow = CSVFile.readLine(); // Read first data line.
//Building Key| Unit| Rent Amount| Tenant | Phone # | e-mail | move-in date| Lease end  | deposit | balance forward | Descr
// (new) Building Key| Unit| Rent Amount| Tenant ID | Descr
		  while (dataRow != null && dataRow.trim().length() > 1){
			   String [] dataArray = dataRow.split(DELIMITER, -1);
				System.out.println(dataArray);
				indexBuildingUnitSameOrder.add(dataArray[0].replaceAll("\\s+","").toUpperCase() + " " + dataArray[1].replaceAll("\\s+","").toUpperCase());
				numunits += 1;
				Tenant curTenant = inTenantManager.createDummyTenant();
				if (inTenantManager.getTenantMap().containsKey(Integer.parseInt(dataArray[3].trim()))) {
					   curTenant = inTenantManager.getTenantMap().get(Integer.parseInt(dataArray[3].trim()));
				}
			   // first case is where building exists, but adding a new unit
//			   if (buildings.containsKey(dataArray[0].trim().toUpperCase())) {
			   if (buildings.containsKey(dataArray[0].replaceAll("\\s+","").toUpperCase())) {
				   Building tempBuilding = buildings.get(dataArray[0].replaceAll("\\s+","").toUpperCase());
				   tempBuilding.addUnit(dataArray[1].replaceAll("\\s+","").toUpperCase(), Double.parseDouble(dataArray[2].trim()), 
						   curTenant, dataArray[4].trim());
				   buildings.put(dataArray[0].replaceAll("\\s+","").toUpperCase(), tempBuilding);
			   }
			   // this case adds a building and a new unit
			   else {
				   if (dataArray[1].replaceAll("\\s+","").toUpperCase().length() > 0) { //multi-unit
					   indexListSmall.add(dataArray[0].replaceAll("\\s+","").toUpperCase());
				   }
				   this.buildingNames.add(dataArray[0].replaceAll("\\s+","").toUpperCase());
				   Building newBuilding = new Building();
				   newBuilding.setName(dataArray[0].replaceAll("\\s+","").toUpperCase());
				   newBuilding.addUnit(dataArray[1].replaceAll("\\s+","").toUpperCase(), Double.parseDouble(dataArray[2].trim()),
						   curTenant, dataArray[4].trim());
				   buildings.put(dataArray[0].replaceAll("\\s+","").toUpperCase(), newBuilding);
				   
			   }
				indexListSmall.add(dataArray[0].replaceAll("\\s+","").toUpperCase()+
						dataArray[1].replaceAll("\\s+","").toUpperCase());
			   System.out.println(dataArray[0].replaceAll("\\s+","").toUpperCase()); // Print the data line.
			   dataRow = CSVFile.readLine(); // Read next line of data.
		  }
			indexListSmall.add("PORTFOLIO");  //every portfolio needs this key

			  // Close the file once all data has been read.
		  CSVFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

		
	public List<String> getListUnits() {
		return this.indexListSmall;
	}

	public List<String> getListBuildings() {
		return this.buildingNames;
	}

	public List<String> getUnits(String inBuilding) {
		return buildings.get(inBuilding).getUnitsList();
	}

	public void printPort(){
	Iterator<String> buildingsIt = this.buildingNames.iterator();
	while (buildingsIt.hasNext()) {
			String keyId = buildingsIt.next();
			System.out.println(this.buildings.get(keyId).toString());
	}
	}

	public void resetReportStruct() {
		for (String key : reportStruct.keySet()) {
			double [] doubleArray = reportStruct.get(key);
			Arrays.fill(doubleArray, 0.0);
			reportStruct.put(key, doubleArray);
		}
	}

	public void genReportStruct() {
		for (String key : buildings.keySet()) {
			for (String key2 : buildings.get(key).getUnits().keySet()) {
				double [] doubleArray = new double[indexHeaderListTotal.size()];
				Arrays.fill(doubleArray, 0.0);
				reportStruct.put(key+key2, doubleArray);
				indexList.add(key+key2);
			}
			if (buildings.get(key).getUnits().keySet().size() > 1) // must be multi-family building
			{
				double [] doubleArray = new double[indexHeaderListTotal.size()];
				Arrays.fill(doubleArray, 0.0);
				reportStruct.put(key, doubleArray);
				indexList.add(key);
				double [] doubleArray2 = new double[indexHeaderListTotal.size()];
				Arrays.fill(doubleArray2, 0.0);
				reportStruct.put(key+"_TOTAL", doubleArray2); //for total on the building
				indexList.add(key+"_TOTAL");
			}
		}
/*		double [] doubleArray = new double[numColumns];
		Arrays.fill(doubleArray, 0.0);
		reportStruct.put("TOT", doubleArray); //for total on the portfolio
		indexList.add("TOT"); */  
		double [] doubleArray2 = new double[indexHeaderListTotal.size()];
		Arrays.fill(doubleArray2, 0.0);
//		reportStruct.put("", doubleArray2); //for expense not specific to any building
		reportStruct.put("PORTFOLIO", doubleArray2); //for expense not specific to any building
//		indexList.add("");
		indexList.add("PORTFOLIO");
		//size of double array is rent/managementfee/maint/repair/utility/legal/?
		
	}
	public double[] genRevenuePlot(int inYear) {
		// get totals for specified year
		//local date indexes months from 1-12, not 0-11
		double [] returnArray = new double[13];
		Iterator<Revenue> revenueIt = this.revenues.iterator();
		double totalRev = 0.0;
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if (revenue.getDate().getYear() == inYear) {
				// add rent
				returnArray[revenue.getDate().getMonthOfYear()] += revenue.getAmount();
				totalRev += revenue.getAmount();
			}
		}
		returnArray[0]=totalRev;  //since we didn't use the 0th position, return totals
		return returnArray;
	}

	public double[] genRevenuePlotUnit(String buildingUnit, int inYear) {
		// get totals for specified year
		//local date indexes months from 1-12, not 0-11
		String [] splitArray = buildingUnit.trim().split("\\s+");
		int isBuilding = 0;
		if (splitArray.length == 1) {
			isBuilding = 1;
		}
		double [] returnArray = new double[13];
		Iterator<Revenue> revenueIt = this.revenues.iterator();
		double totalRev = 0.0;
		String curBuildingUnit = "";
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if(isBuilding==1) {
				curBuildingUnit = revenue.getBuilding();
			} else {
				curBuildingUnit = revenue.getBuilding()+revenue.getUnit();
			}
			if (curBuildingUnit.equalsIgnoreCase(buildingUnit.replaceAll("\\s+","")) &&
					revenue.getDate().getYear() == inYear) {
				// add rent
				returnArray[revenue.getDate().getMonthOfYear()] += revenue.getAmount();
				totalRev += revenue.getAmount();
			}
		}
		returnArray[0]=totalRev;  //since we didn't use the 0th position, return totals
		return returnArray;
	}

		
	public double[] genExpensePlot(int inYear) {
		double [] returnArray = new double[13];
		double totalExp = 0.0;
		Iterator<Expense> expensesIt = this.expenses.iterator();
		while (expensesIt.hasNext()) {
			Expense expense = expensesIt.next();
			if (expense.getDate().getYear() == inYear) {
				returnArray[expense.getDate().getMonthOfYear()] += expense.getAmount();
				totalExp += expense.getAmount();
			}
		}
		Iterator<Revenue> revenueIt = this.revenues.iterator();
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if (revenue.getDate().getYear() == inYear) {
				// add rent
				returnArray[revenue.getDate().getMonthOfYear()] += (revenue.getManageFee()*revenue.getAmount());
				totalExp += (revenue.getManageFee()*revenue.getAmount());
			}
		}
		returnArray[0]=totalExp;  //since we didn't use the 0th position, return totals
		return returnArray;
	}
	
	public int genNewReport(){
		double [] tempDouble = new double[indexHeaderListTotal.size()];
		double totalExp = 0.0;
// This one must CHANGE STATE and write to OUTPUT file!
// file format is 'ClientName_Year_ReportNum.txt'
		String[] reportInfoArray = Utils.getReportInfo(this).split(DELIMITER, -1);
		if (Integer.parseInt(reportInfoArray[3].trim()) == 0) {
			return 0;
		}
		int newReportNum = incReportNum();
		String fileName = this.name + Integer.toString(LocalDate.now().getYear()) + "_" + Integer.toString(getReportNum());
		File tempFile = new File(fileName);
		if (tempFile.exists()) {
			return 0;
		}
		try {
	  BufferedWriter CSVFile = new BufferedWriter(new FileWriter(fileName));
	if (reportInfoArray[2].equalsIgnoreCase("YES")) {
		CSVFile.write("WARNING (for tax purposes): This report has expenses and or revenues from multiple years!");
	}
	  CSVFile.write("This is report number: "+Integer.toString(newReportNum) +
			  " for year " + Integer.toString(LocalDate.now().getYear())); // Read first line that is headers
	  CSVFile.write("\n The itemized expenses for this period are: \n \n");
		Iterator<Expense> expensesIt = this.expenses.iterator();
		while (expensesIt.hasNext()) {
			Expense expense = expensesIt.next();
			if (expense.getReportNum() == 0) {
				expense.setReportNum(newReportNum);
				CSVFile.write(Utils.printExpense(expense));
				tempDouble = reportStruct.get(expense.getBuilding()+expense.getUnit());
				tempDouble[mapIndices.get(expense.getType())] += expense.getAmount();
				reportStruct.put(expense.getBuilding()+expense.getUnit(), tempDouble);
				totalExp += expense.getAmount();
// be sure to add up total building expenses
				if (expense.getUnit().length() > 0)  //this rules out SFH 
				{
					System.out.println(expense.getType()); // Print the data line.
					tempDouble = reportStruct.get(expense.getBuilding()+"_TOTAL");
					tempDouble[mapIndices.get(expense.getType())] += expense.getAmount();
					reportStruct.put(expense.getBuilding()+"_TOTAL", tempDouble);
				}
			}
		}
		Iterator<Revenue> revenueIt = this.revenues.iterator();
		double totalRev = 0.0;
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if (revenue.getReportNum() == 0) {
				revenue.setReportNum(newReportNum);
				tempDouble = reportStruct.get(revenue.getBuilding()+revenue.getUnit());
				// add rent
				tempDouble[mapIndices.get(revenue.getType())] += revenue.getAmount();
				totalRev += revenue.getAmount();
				// add manage fee
				tempDouble[mapIndices.get("MANAGEFEE")] += (revenue.getManageFee()*revenue.getAmount());
				reportStruct.put(revenue.getBuilding()+revenue.getUnit(), tempDouble);
				totalExp += (revenue.getManageFee()*revenue.getAmount());
				// be sure to add up total building revenues
				if (revenue.getUnit().length() > 0)  //this rules out SFH 
				{
					tempDouble = reportStruct.get(revenue.getBuilding()+"_TOTAL");
					tempDouble[mapIndices.get(revenue.getType())] += revenue.getAmount();
					tempDouble[mapIndices.get("MANAGEFEE")] += (revenue.getManageFee()*revenue.getAmount());
					reportStruct.put(revenue.getBuilding()+"_TOTAL", tempDouble);
				}
			}
		}
		Object[][] returnVal = new Object[reportStruct.size()+2][indexHeaderListTotal.size()+1];
		StringBuilder sBTemp = new StringBuilder();
		for (int ii = 0; ii < indexHeaderListTotal.size(); ii++) {
			sBTemp.append("                        ");
		}
		StringBuilder sBTempTot = new StringBuilder();
		int ind = 20;
		int ind2 = 0;
		StringBuilder sBWhiteSpace = new StringBuilder("                                      ");
		sBTemp.replace(0, ind, "Building"+sBWhiteSpace.substring(0,ind-"Building".length()));
		for (String key : mapIndices.keySet()) {
			sBTemp.replace(ind, ind+15, key+sBWhiteSpace.substring(0,15-key.length())); //reset SB
			ind += 15;
		}
		CSVFile.write("\n" + sBTemp.toString()+"\n \n"); // Print the data line.
		int i_i = 0;
		for (String key : indexList) {
			double [] tempArray = reportStruct.get(key);
			i_i += 1;
			ind = 20;
			if (key.length() > ind) {
				key = key.substring(0, ind);
			}
			sBTemp.replace(0, ind, key + sBWhiteSpace.substring(0,ind-key.length()));
			for (String key2 : mapIndices.keySet()) {
//			for (int i = 0; i < tempArray.length; i++) {
				sBTemp.replace(ind, ind+15, String.format("%1$,.2f", tempArray[mapIndices.get(key2)]) + 
						sBWhiteSpace.substring(0,15-String.format("%1$,.2f", tempArray[mapIndices.get(key2)]).length()));
				ind += 15;
			}
			/*if (i_i == indexList.size()) {
				sBTemp.replace(0, 20, "PORTFOLIO" + sBWhiteSpace.substring(0,20-"PORTFOLIO".length()));
			} */
			CSVFile.write(sBTemp.toString()+"\n"); // Print the data line.
		}
//		returnVal[ind3][0] = "PORTFOLIO"; // need to relabel this to make sense to user
		
		// package totals at the end of the array
		CSVFile.write("The total revenue for this report period is: " + String.format("%1$,.2f", totalRev) + "\n");
		CSVFile.write("The total expenses for this report period is: " + String.format("%1$,.2f", totalExp) + "\n");
		CSVFile.close();

		} catch (IOException e) {
			System.out.println("IO Problems in genNewReport()");
		}
		return 1; // everything is good with report
		
		
		
		
		
		
		
	}

//	public String printReport(LocalDate begDate, LocalDate endDate){
	public Object[][] printReport(LocalDate begDate, LocalDate endDate, String inType){
		// Build dictionary of dictionaries
		double totalClientRevenue = 0.0;
		double totalExp = 0.0;
		double [] tempDouble = new double[indexHeaderListTotal.size()];
		Iterator<Expense> expensesIt = this.expenses.iterator();
		while (expensesIt.hasNext()) {
			Expense expense = expensesIt.next();
			if (expense.getDate().isAfter(begDate) && expense.getDate().isBefore(endDate) ||
				expense.getDate().isEqual(begDate) || expense.getDate().isEqual(endDate)) {
				tempDouble = reportStruct.get(expense.getBuilding()+expense.getUnit());
				if (inType.equalsIgnoreCase("CASHFLOW") || expense.getCapitalize().equalsIgnoreCase("NO")) {
				  tempDouble[mapIndices.get(expense.getType())] += expense.getAmount();
				  reportStruct.put(expense.getBuilding()+expense.getUnit(), tempDouble);
				  totalExp += expense.getAmount();
				  if (expense.getType().equals("LEASING")) {
					totalClientRevenue += expense.getAmount();
				  }
// be sure to add up total building expenses
					Building tempBuilding;
					if (!expense.getBuilding().equalsIgnoreCase("PORTFOLIO")) {
					tempBuilding = buildings.get(expense.getBuilding());
		  		    if (tempBuilding.getUnitsList().size() > 1)  //this rules out SFH 
				  {
					System.out.println(expense.getType()); // Print the data line.
					tempDouble = reportStruct.get(expense.getBuilding()+"_TOTAL");
					tempDouble[mapIndices.get(expense.getType())] += expense.getAmount();
					reportStruct.put(expense.getBuilding()+"_TOTAL", tempDouble);
				  }
					}
				} // end Capitalize clause
			}
		}
		Iterator<Revenue> revenueIt = this.revenues.iterator();
		double totalRev = 0.0;
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if (revenue.getDate().isAfter(begDate) && revenue.getDate().isBefore(endDate) ||
				revenue.getDate().isEqual(begDate) || revenue.getDate().isEqual(endDate)) {
				tempDouble = reportStruct.get(revenue.getBuilding()+revenue.getUnit());
				// add rent
				tempDouble[mapIndices.get(revenue.getType())] += revenue.getAmount();
				totalRev += revenue.getAmount();
				// add manage fee
				tempDouble[mapIndices.get("MANAGEFEE")] += (revenue.getManageFee()*revenue.getAmount());
				reportStruct.put(revenue.getBuilding()+revenue.getUnit(), tempDouble);
				totalExp += (revenue.getManageFee()*revenue.getAmount());
				totalClientRevenue += (revenue.getManageFee()*revenue.getAmount());
				// be sure to add up total building revenues
				Building tempBuilding;
				if (!revenue.getBuilding().equalsIgnoreCase("PORTFOLIO")) {
				tempBuilding = buildings.get(revenue.getBuilding());
     		    if (tempBuilding.getUnitsList().size() > 1)  //this rules out SFH 
				{
					tempDouble = reportStruct.get(revenue.getBuilding()+"_TOTAL");
					tempDouble[mapIndices.get(revenue.getType())] += revenue.getAmount();
					tempDouble[mapIndices.get("MANAGEFEE")] += (revenue.getManageFee()*revenue.getAmount());
					reportStruct.put(revenue.getBuilding()+"_TOTAL", tempDouble);
				}
				}
			}
		}
		Object[][] returnVal = new Object[reportStruct.size()+2][indexHeaderList.size()+1];
		int ind2 = 0;
		returnVal[0][0] = "Building    ";
		for (String key : indexHeaderList) {
			ind2 += 1;
			returnVal[0][ind2] = key;
		}
	    int ind3 = 0;
//		for (String key : reportStruct.keySet()) {
		for (String key : indexList) {
			double [] tempArray = reportStruct.get(key);
		    ind2 = 0;
			ind3 += 1;
// Want to only pull out the categories that we want to print to screen
			for (String keySubset : indexHeaderList) {
				ind2 += 1;
				returnVal[ind3][ind2] = String.format("%1$,.2f", tempArray[mapIndices.get(keySubset)]);
			}
			/*if (key.equalsIgnoreCase("")) {
				returnVal[ind3][0] = "PORTFOLIO"; // need to relabel this to make sense to user
			} else { */
				returnVal[ind3][0] = key;
			//}
		     
		}
		
		// package totals at the end of the array
		returnVal[returnVal.length-1][0] = String.format("%1$,.2f", totalRev);
		returnVal[returnVal.length-1][1] = String.format("%1$,.2f", totalExp);
		returnVal[returnVal.length-1][2] = String.format("%1$,.2f", totalClientRevenue);
		
		//print to file
		String fileName = this.name + "_" + inType + "_Beg_" + begDate.toString() + "_End_" + endDate.toString();
		File tempFile = new File(fileName);
		if (tempFile.exists()) {
			//return;
		}
		try {
	  BufferedWriter CSVFile = new BufferedWriter(new FileWriter(fileName));
	  CSVFile.write("This is a " + inType + " report for the date range " + begDate.toString() +
			  " to " + endDate.toString() + " for portfolio: " + this.name); 
/*	  CSVFile.write("\n The itemized expenses for this period are: \n \n");
		Iterator<Expense> expensesIt = this.expenses.iterator();
		while (expensesIt.hasNext()) {
			Expense expense = expensesIt.next();
			if (expense.getReportNum() == 0) {
				expense.setReportNum(newReportNum);
				CSVFile.write(Utils.printExpense(expense));
				tempDouble = reportStruct.get(expense.getBuilding()+expense.getUnit());
				tempDouble[mapIndices.get(expense.getType())] += expense.getAmount();
				reportStruct.put(expense.getBuilding()+expense.getUnit(), tempDouble);
				totalExp += expense.getAmount();
// be sure to add up total building expenses
				if (expense.getUnit().length() > 0)  //this rules out SFH 
				{
					System.out.println(expense.getType()); // Print the data line.
					tempDouble = reportStruct.get(expense.getBuilding()+"_TOTAL");
					tempDouble[mapIndices.get(expense.getType())] += expense.getAmount();
					reportStruct.put(expense.getBuilding()+"_TOTAL", tempDouble);
				}
			}
		}
		Iterator<Revenue> revenueIt = this.revenues.iterator();
		double totalRev = 0.0;
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if (revenue.getReportNum() == 0) {
				revenue.setReportNum(newReportNum);
				tempDouble = reportStruct.get(revenue.getBuilding()+revenue.getUnit());
				// add rent
				tempDouble[mapIndices.get(revenue.getType())] += revenue.getAmount();
				totalRev += revenue.getAmount();
				// add manage fee
				tempDouble[mapIndices.get("MANAGEFEE")] += (revenue.getManageFee()*revenue.getAmount());
				reportStruct.put(revenue.getBuilding()+revenue.getUnit(), tempDouble);
				totalExp += (revenue.getManageFee()*revenue.getAmount());
				// be sure to add up total building revenues
				if (revenue.getUnit().length() > 0)  //this rules out SFH 
				{
					tempDouble = reportStruct.get(revenue.getBuilding()+"_TOTAL");
					tempDouble[mapIndices.get(revenue.getType())] += revenue.getAmount();
					tempDouble[mapIndices.get("MANAGEFEE")] += (revenue.getManageFee()*revenue.getAmount());
					reportStruct.put(revenue.getBuilding()+"_TOTAL", tempDouble);
				}
			}
		} */
		StringBuilder sBTemp = new StringBuilder();
		for (int ii = 0; ii < indexHeaderListTotal.size(); ii++) {
			sBTemp.append("                        ");
		}
		StringBuilder sBTempTot = new StringBuilder();
		int ind = 20;
		StringBuilder sBWhiteSpace = new StringBuilder("                                      ");
		sBTemp.replace(0, ind, "Building"+sBWhiteSpace.substring(0,ind-"Building".length()));
		for (String key : mapIndices.keySet()) {
			sBTemp.replace(ind, ind+15, key+sBWhiteSpace.substring(0,15-key.length())); //reset SB
			ind += 15;
		}
		CSVFile.write("\n" + sBTemp.toString()+"\n \n"); // Print the data line.
		int i_i = 0;
		for (String key : indexList) {
			double [] tempArray = reportStruct.get(key);
			i_i += 1;
			ind = 20;
			if (key.length() > ind) {
				key = key.substring(0, ind);
			}
			sBTemp.replace(0, ind, key + sBWhiteSpace.substring(0,ind-key.length()));
			for (String key2 : mapIndices.keySet()) {
//			for (int i = 0; i < tempArray.length; i++) {
				sBTemp.replace(ind, ind+15, String.format("%1$,.2f", tempArray[mapIndices.get(key2)]) + 
						sBWhiteSpace.substring(0,15-String.format("%1$,.2f", tempArray[mapIndices.get(key2)]).length()));
				ind += 15;
			}
			/*if (i_i == indexList.size()) {
				sBTemp.replace(0, 20, "PORTFOLIO" + sBWhiteSpace.substring(0,20-"PORTFOLIO".length()));
			} */
			CSVFile.write(sBTemp.toString()+"\n"); // Print the data line.
		}
//		returnVal[ind3][0] = "PORTFOLIO"; // need to relabel this to make sense to user
		
		// package totals at the end of the array
		CSVFile.write("The total revenue for this report period is: " + String.format("%1$,.2f", totalRev) + "\n");
		CSVFile.write("The total expenses for this report period is: " + String.format("%1$,.2f", totalExp) + "\n");
		CSVFile.close();

		} catch (IOException e) {
			System.out.println("IO Problems in printReport()");
		}
		returnVal[returnVal.length-1][3] = fileName; // print this filename out to dialog
		return returnVal;
	}

	public Object[][] printReportByReportNum(int inReportNum){
		// Build dictionary of dictionaries
		double totalClientRevenue = 0.0;
		double totalExp = 0.0;
		double [] tempDouble = new double[numColumns];
		Iterator<Expense> expensesIt = this.expenses.iterator();
		while (expensesIt.hasNext()) {
			Expense expense = expensesIt.next();
			if (expense.getReportNum() == inReportNum) {
				tempDouble = reportStruct.get(expense.getBuilding()+expense.getUnit());
				tempDouble[mapIndices.get(expense.getType())] += expense.getAmount();
				reportStruct.put(expense.getBuilding()+expense.getUnit(), tempDouble);
				totalExp += expense.getAmount();
				if (expense.getType().equals("LEASING")) {
					totalClientRevenue += expense.getAmount();
				}
			}
		}
		Iterator<Revenue> revenueIt = this.revenues.iterator();
		double totalRev = 0.0;
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if (revenue.getReportNum() == inReportNum) {
				tempDouble = reportStruct.get(revenue.getBuilding()+revenue.getUnit());
				// add rent
				tempDouble[mapIndices.get(revenue.getType())] += revenue.getAmount();
				totalRev += revenue.getAmount();
				// add manage fee
				tempDouble[mapIndices.get("MANAGEFEE")] += (revenue.getManageFee()*revenue.getAmount());
				reportStruct.put(revenue.getBuilding()+revenue.getUnit(), tempDouble);
				totalExp += (revenue.getManageFee()*revenue.getAmount());
				totalClientRevenue += (revenue.getManageFee()*revenue.getAmount());
			}
		}
		Object[][] returnVal = new Object[reportStruct.size()+2][indexHeaderList.size()+1];
		int ind2 = 0;
		returnVal[0][0] = "Building    ";
		for (String key : indexHeaderList) {
			ind2 += 1;
			returnVal[0][ind2] = key;
		}
	    int ind3 = 0;
		for (String key : indexListSmall) {
			double [] tempArray = reportStruct.get(key);
		    ind2 = 0;
			ind3 += 1;
			for (String keySubset : indexHeaderList) {
				ind2 += 1;
				returnVal[ind3][ind2] = String.format("%1$,.2f", tempArray[mapIndices.get(keySubset)]);
			}
		    returnVal[ind3][0] = key; 
		}
		//returnVal[ind3+2][0] = "PORTFOLIO"; // need to relabel this to make sense to user
		
		// package totals at the end of the array
		returnVal[returnVal.length-1][0] = String.format("%1$,.2f", totalRev);
		returnVal[returnVal.length-1][1] = String.format("%1$,.2f", totalExp);
		returnVal[returnVal.length-1][2] = String.format("%1$,.2f", totalClientRevenue);
		
		return returnVal;
	}

/*	public void printExpensesMonth(Month monthIn){
		Iterator<Expense> expenseIt = this.expenses.iterator();
		double sum = 0.0;
		while (expenseIt.hasNext()) {
			Expense keyId = expenseIt.next();
			if (keyId.getDate().getMonth().equals(monthIn)) {
				System.out.println(keyId.toString() + keyId.getDescr());
				sum += keyId.getAmount();
				
			}
		}
		System.out.println("Total expenses for " + monthIn.toString() + " is: " + Double.toString(sum));
	}  

	public void printRevenuesMonth(Month monthIn){
		Iterator<Revenue> revenueIt = this.revenues.iterator();
		double sum = 0.0;
		int index = 0;
		while (revenueIt.hasNext()) {
			index += 1;
			Revenue keyId = revenueIt.next();
			if (keyId.getDate().getMonth().equals(monthIn)) {
				System.out.println(keyId.toString() + keyId.getBuilding() + keyId.getUnit());
				sum += keyId.getAmount();
				
			}
		}
		System.out.println("Total revenues for " + monthIn.toString() + " is: " + Double.toString(sum));
		System.out.println("Total new revenues is: " + Integer.toString(index));
	}  */

	public void printExpensesBuilding(String typeIn, String buildingIn){
		Iterator<Expense> expenseIt = this.expenses.iterator();
		double sum = 0.0;
		while (expenseIt.hasNext()) {
			Expense keyId = expenseIt.next();
			if (keyId.getBuilding().equals(buildingIn) && keyId.getType().equals(typeIn)) {
				System.out.println(keyId.toString() + keyId.getDescr());
				sum += keyId.getAmount();
				
			}
		}
		System.out.println("Total expenses for " + buildingIn + " is: " + Double.toString(sum));
	}

	public void addProp(Building prop){
		this.buildingNames.add(prop.getName());
		this.buildings.put(prop.getName(), prop);
// **** Must add building to report struct
		if (numunits == 0) {  //first building in this portfolio so add total portfolio key
			double [] doubleArray = new double[indexHeaderListTotal.size()];
			Arrays.fill(doubleArray, 0.0);
			reportStruct.put("PORTFOLIO", doubleArray); //for expense not specific to any building
			indexList.add("PORTFOLIO");
			indexListSmall.add("PORTFOLIO");
			/*Building building = new Building();
			building.setName("PORTFOLIO");
			building.addUnit("", 0.0, "VACANT", "", "", "", "", 0.0, 0.0, "None"); */
		}
		if (prop.getUnitsList().size() > 1) { //multi-unit
			   indexListSmall.add(prop.getName());
				double [] doubleArray = new double[indexHeaderListTotal.size()];
				Arrays.fill(doubleArray, 0.0);
				reportStruct.put(prop.getName(), doubleArray);
				indexList.add(prop.getName());
				double [] doubleArray2 = new double[indexHeaderListTotal.size()];
				Arrays.fill(doubleArray2, 0.0);
				reportStruct.put(prop.getName()+"_TOTAL", doubleArray2); //for total on the building
				indexList.add(prop.getName()+"_TOTAL");
		}
		for (String key: prop.getUnitsList()) {
			indexBuildingUnitSameOrder.add(prop.getName() + " " + key);
			numunits += 1;
			indexListSmall.add(prop.getName() +	key);
			double [] doubleArray = new double[indexHeaderListTotal.size()];
			Arrays.fill(doubleArray, 0.0);
			reportStruct.put(prop.getName()+key, doubleArray);
			indexList.add(prop.getName()+key);
		}
		

	}

	public void addExpense(String [] itemList) {
//Format: Portfolio, Invoice num, Building, Unit, Category, Date, Amount, Capitalize, Description 		
		   Expense newExpense = new Expense();
		   
		   this.expenses.add(newExpense.build(itemList));
		   if (newExpense.getCapitalize().equalsIgnoreCase("YES")) { //treeset should remove duplicate items
			   capExYears.add(Integer.toString(newExpense.getDate().getYear()));
		   }
		   expYears.add(Integer.toString(newExpense.getDate().getYear()));
	}

	public void addRevenue (String [] itemList) {
		//Format: Portfolio, Invoice num, Building, Unit, Category, Date, Amount, Capitalize, Description 		
		   Revenue newRevenue = new Revenue();
				   
		   this.revenues.add(newRevenue.build(itemList));
		   revYears.add(Integer.toString(newRevenue.getDate().getYear()));
			}

	
	
	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}
	public int getRevenueListSize(){
		return revenues.size();
	}
	public int getExpenseListSize(){
		return expenses.size();
	}
	public List<Expense> getExpenseList(){
		return expenses;
	}
	
	public List<Revenue> getRevenueList(){
		return revenues;
	}
	
	public int incReportNum() {
		/*yearReportCounts.put(LocalDate.now().getYear(), 
				yearReportCounts.get(LocalDate.now().getYear())+1); */
		reportNum += 1;
		return reportNum;
	}
	public int getReportNum() {
		return reportNum;
	}
	public String getClientInfo(){
		return this.clientInfo;
	}
	public void setClientInfo(String inClientInfo){
		this.clientInfo = inClientInfo;
	}
	public List<String> getIndexBuildingUnitSameOrder(){
		return this.indexBuildingUnitSameOrder;
	}
	public int getNumUnits() {
		return numunits;
	}
	public  TreeSet<String> getCapExYears() {
		return capExYears;
	}
	public  TreeSet<String> getRevYears() {
		return revYears;
	}
	public  TreeSet<String> getExpYears() {
		return expYears;
	}
	public  Map<String, Building> getBuildings() {
		return buildings;
	}
	
	// this function only works when table conventions for building/unit are followed
	public boolean isUnit(String inBuildingUnit) {
		String [] splitArray = inBuildingUnit.trim().split("\\s+");
		boolean returnVal = false;
		if (splitArray.length == 1) { // not multi-family
			if (inBuildingUnit.trim().equalsIgnoreCase("PORTFOLIO")) {
				returnVal = false;
			}
			if (buildings.containsKey(inBuildingUnit.trim().toUpperCase())) {
				if (buildings.get(inBuildingUnit.trim().toUpperCase()).getUnits().size() == 1) {
					returnVal = true;
				}
			}
		}
		else if ((splitArray.length == 2) && buildings.containsKey(splitArray[0].trim().toUpperCase())){
			returnVal = true;
		}
		return returnVal;
	}


}
