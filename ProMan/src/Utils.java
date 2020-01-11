import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTimeField;
import org.joda.time.LocalDate;
import org.joda.time.chrono.GregorianChronology;

import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Utils implements Serializable {
    static final String DELIMITER = "\\|";

	public static String updateServiceItemInfo(Map<String, PropPortfolio> inPropPortfolioMap, ServiceManager inServiceManager) {
		boolean errorFlag = false;
		Map<String, Service> myServiceMap = inServiceManager.getServiceMap();
        for (String clientName: inPropPortfolioMap.keySet()) {
   			PropPortfolio activePropPortfolio = inPropPortfolioMap.get(clientName);
   			Map<String, Building> tempBuildings = activePropPortfolio.getBuildings(); 
   			for (String buildkey : tempBuildings.keySet()) {
   				for (String unitkey : tempBuildings.get(buildkey).getUnits().keySet()) {
   					Map<String, Unit> unitMap = tempBuildings.get(buildkey).getUnits();
   					Unit curUnit = unitMap.get(unitkey); 
   					String buildingUnit = buildkey + " " + unitkey;
   					List<OpenItem> itemList = curUnit.getItemList();
   					for (OpenItem curItem: itemList) {
   						if (myServiceMap.containsKey(curItem.getServicer())) {
   						Service myService = myServiceMap.get(curItem.getServicer());
   						ServiceItem inItem = new ServiceItem();
   						inItem.setBuildingUnit(buildingUnit);
   						inItem.setPortfolio(clientName);
   						inItem.setItem(curItem);
   						myService.addItem(inItem);
   						myServiceMap.put(curItem.getServicer(), myService);
   						}  else {
   							//some services appear to be missing?
   							errorFlag = true;
   						}
   					}
   				}
   			}
   			
     }
		if (errorFlag) {
			return "\n Some services appear to be missing. \n Is ServicesInput.txt file present in current directory?\n";
		} else {
			return "";
		}
	}
	public static void updateTenantBalanceInfo(Map<String, PropPortfolio> inPropPortfolioMap) {
// assume this function is done before any tenants are moved out in the new year.
		// this leads to three cases for tenants:  either they moved in partway through previous
		// year, or were there the entire year, or unit is currently vacant and we ignore
		// this function also doesn't account for rent changes in middle of year (it uses current rent for all 12 months)
        for (String clientName: inPropPortfolioMap.keySet()) {
   			PropPortfolio activePropPortfolio = inPropPortfolioMap.get(clientName);
   			Map<String, Building> tempBuildings = activePropPortfolio.getBuildings(); 
   			for (String buildkey : tempBuildings.keySet()) {
   				for (String unitkey : tempBuildings.get(buildkey).getUnits().keySet()) {
   					Map<String, Unit> unitMap = tempBuildings.get(buildkey).getUnits();
   					Unit curUnit = unitMap.get(unitkey); 
   					String buildingUnit = buildkey + " " + unitkey;
   					Tenant curTenant = (Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit);
   					if (!curTenant.getTenant().equalsIgnoreCase("VACANT")) {
   					double curBalance =	curTenant.getBalanceForward();
   					LocalDate begStayThisYear = new LocalDate();
   					begStayThisYear = curTenant.getMoveInDate();
   					if (begStayThisYear.getYear() < StartProMan.yearTotals) { // set begin of period to first of year
   						begStayThisYear = LocalDate.parse(Integer.toString(StartProMan.yearTotals)+"-01-01");
   					}
//   					System.out.println("Input date is: " + jtfMoveOutDate.getText());
   					LocalDate endStayThisYear = LocalDate.parse(Integer.toString(StartProMan.yearTotals)+"-12-31");
   					double curRent = (Double)activePropPortfolio.getUnitLevelInfo("RENT", buildingUnit);
   					double rentOwedThisYear = Utils.getRentMonths(begStayThisYear, endStayThisYear);
   					rentOwedThisYear = curRent*rentOwedThisYear;
   					List<Revenue> revList = activePropPortfolio.getRevenueList();
   					double totalRentPaid = Utils.getRentForYearTenant(revList, curTenant.getID(), StartProMan.yearTotals);
   					double newBalance = rentOwedThisYear + curBalance - totalRentPaid;
   					curTenant.setBalanceForward(newBalance);
   					activePropPortfolio.setUnitLevelInfo("TENANT", buildingUnit, (Object)curTenant);
   					} // end 'if not vacant' clause
   				
   				
   				}
   			}
   			
     }

	}
	
	public static String deadbeatTracker(Map<String, PropPortfolio> inPropPortfolioMap) {
		// assume this function is done after all balanceForward info has been updated (program prompts at year change for this)
		// code modeled after updateTenantBalanceInfo() in Utils
		// assumes no rent increases during current year
				int numDeadBeats = 0;
				StringBuilder sbReturn = new StringBuilder();
				sbReturn.append("\nAcross all portfolios, the tenants that are not paid up are: \n\n");
		        for (String clientName: inPropPortfolioMap.keySet()) {
		   			PropPortfolio activePropPortfolio = inPropPortfolioMap.get(clientName);
		   			Map<String, Building> tempBuildings = activePropPortfolio.getBuildings(); 
		   			for (String buildkey : tempBuildings.keySet()) {
		   				for (String unitkey : tempBuildings.get(buildkey).getUnits().keySet()) {
		   					Map<String, Unit> unitMap = tempBuildings.get(buildkey).getUnits();
		   					Unit curUnit = unitMap.get(unitkey); 
		   					String buildingUnit = buildkey + " " + unitkey;
		   					Tenant curTenant = (Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit);
		   					if (!curTenant.getTenant().equalsIgnoreCase("VACANT")) {
		   					double curBalance =	curTenant.getBalanceForward();
		   					LocalDate begStayThisYear = new LocalDate();
		   					begStayThisYear = curTenant.getMoveInDate();
		   					if (begStayThisYear.getYear() < LocalDate.now().getYear()) { // set begin of period to first of year
		   						begStayThisYear = LocalDate.parse(Integer.toString(LocalDate.now().getYear())+"-01-01");
		   					}
//		   		assume we are looking at total rent owed up until this current day
		   					LocalDate endStayThisYear = LocalDate.now();
		   					double curRent = (Double)activePropPortfolio.getUnitLevelInfo("RENT", buildingUnit);
		   					double rentOwedThisYear = Utils.getRentMonths(begStayThisYear, endStayThisYear);
		   					rentOwedThisYear = curRent*rentOwedThisYear;
		   					List<Revenue> revList = activePropPortfolio.getRevenueList();
		   					double totalRentPaid = Utils.getRentForYearTenant(revList, curTenant.getID(), LocalDate.now().getYear());
		   					double amountStillOwed = rentOwedThisYear + curBalance - totalRentPaid;
		   					if (amountStillOwed > 0.01) {
		   						sbReturn.append("Portfolio: " + clientName + "Building/Unit: " +
		   								buildkey + " " + unitkey + " Tenant " + curTenant.getTenant() +
		   								" still owes: " + String.format("%1$,.2f", amountStillOwed) + ".\n");
		   						numDeadBeats += 1;
		   					}
		   					
		   					} // end 'if not vacant' clause
		   				
		   				
		   				}
		   			}
		   			
		     }
		        if (numDeadBeats == 0) {
		        	sbReturn.append("\n Aren't you lucky!  You have no deadbeats right now.");
		        } else {
		        	sbReturn.append("\n You have a total of " + Integer.toString(numDeadBeats) + " deadbeats right now. \n Might want to get those eviction papers handy.");
		        	
		        }
		        return sbReturn.toString();

			}
			
	public static String findItemsByDay(Map<String, PropPortfolio> inPropPortfolioMap, LocalDate inDay) {
		// code modeled after updateTenantBalanceInfo() in Utils
				int numItems = 0;
				StringBuilder sbReturn = new StringBuilder();
				sbReturn.append("\nAcross all portfolios, the items due on " + inDay.toString() + " are: \n\n");
		        for (String clientName: inPropPortfolioMap.keySet()) {
		   			PropPortfolio activePropPortfolio = inPropPortfolioMap.get(clientName);
		   			Map<String, Building> tempBuildings = activePropPortfolio.getBuildings(); 
		   			for (String buildkey : tempBuildings.keySet()) {
		   				for (String unitkey : tempBuildings.get(buildkey).getUnits().keySet()) {
		   					Map<String, Unit> unitMap = tempBuildings.get(buildkey).getUnits();
		   					Unit curUnit = unitMap.get(unitkey); 
		   					String buildingUnit = buildkey + " " + unitkey;
		   					List<OpenItem> itemList = curUnit.getItemList();
		   					for (OpenItem item: itemList) {
		   						if (item.getDueDate().equals(inDay)) {
		   							sbReturn.append("Portfolio: " + clientName + " Building/Unit: " +
		   								buildkey + " " + unitkey + " Servicer: " + item.getServicer() +
		   								" Description: " + item.getItemDescr() + ".\n");
//		   								" is due on: " + item.getDueDate().toString() + ".\n");
		   						numItems += 1;
		   						}
		   					}
		   					
		   				}
		   			}
		   			
		     }
		     if (numItems == 0) {
		        	sbReturn.append("\n You can relax!  No items are due on this day.");
		        } else {
		        	sbReturn.append("\n You have a total of " + Integer.toString(numItems) + " item(s) due on this day. \n Better get to work.");
		        	
		        }
		        return sbReturn.toString();

			}
			
	
    public static double getRentMonths(LocalDate begStayThisYear, LocalDate endStayThisYear) {
    	if (endStayThisYear.isBefore(begStayThisYear)) {
    		return 0;
    	}
		double rentOwedThisYear = 0.0;
		GregorianChronology calendar = GregorianChronology.getInstance();
		DateTimeField field = calendar.dayOfMonth();
		//Calendar cal = Calendar.getInstance();
		//cal.set(begStayThisYear.getYear(), begStayThisYear.getMonthOfYear(), 1);
		LocalDate tempDate = new LocalDate(begStayThisYear.getYear(), begStayThisYear.getMonthOfYear(), 1, calendar);
		int daysInMonth = field.getMaximumValue(tempDate);
//		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		//first case where only one prorated month
		if (endStayThisYear.getMonthOfYear() == begStayThisYear.getMonthOfYear()) {
			rentOwedThisYear = (double)(endStayThisYear.getDayOfMonth()-begStayThisYear.getDayOfMonth()+1)/daysInMonth;
		} else {
			double firstMonthRent = (double)(daysInMonth-begStayThisYear.getDayOfMonth()+1)/daysInMonth;
			rentOwedThisYear = firstMonthRent;
			LocalDate curDate = new LocalDate();
			curDate = begStayThisYear;
		//	increment to the first of next month
			curDate = begStayThisYear.plusMonths(1);
	//curDate = begStayThisYear.plusDays(daysInMonth - begStayThisYear.getDayOfMonth() + 1);
			System.out.println(curDate.getMonthOfYear() + " " + curDate.getDayOfMonth());
			while (curDate.getMonthOfYear() < endStayThisYear.getMonthOfYear()) {
				rentOwedThisYear += 1.0;
				curDate = curDate.plusMonths(1);
				//System.out.println("in while");
			}
			//cal = Calendar.getInstance();
			//cal.set(curDate.getYear(), curDate.getMonthOfYear(), 1);
			tempDate = new LocalDate(curDate.getYear(), curDate.getMonthOfYear(), 1, calendar);			
			daysInMonth = field.getMaximumValue(tempDate);
//			daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			rentOwedThisYear += (double)(endStayThisYear.getDayOfMonth())/daysInMonth;
			System.out.println("Rent owed this year: " + Double.toString(rentOwedThisYear));
		}
		return rentOwedThisYear;		
	}
	public static double getRentForYear(List<Revenue> inRevList, String buildingUnit, int Year) {
		int curYear = Year;
		Iterator<Revenue> revenueIt = inRevList.iterator();
		double totalRev = 0.0;
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			String curBuildingUnit = revenue.getBuilding()+revenue.getUnit();
			if (curBuildingUnit.equalsIgnoreCase(buildingUnit.replaceAll("\\s+","")) &&
					revenue.getDate().getYear() == Year) {
				
				totalRev += revenue.getAmount();
			}
		}
		return totalRev;		
	}
	public static double getRentForYearTenant(List<Revenue> inRevList, int tenantID, int Year) {
		int curYear = Year;
		Iterator<Revenue> revenueIt = inRevList.iterator();
		double totalRev = 0.0;
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			int curTenantID = revenue.getTenantID();
			if (curTenantID == tenantID &&	revenue.getDate().getYear() == Year) {
				
				totalRev += revenue.getAmount();
			}
		}
		return totalRev;		
	}
	public static List<Expense> getUnitExpenses(PropPortfolio inPropPortfolio, String strBuildingUnit) {
		String [] splitArray = strBuildingUnit.trim().split("\\s+");
		List<Expense> returnList = new ArrayList<Expense>();
		StringBuilder building = new StringBuilder(splitArray[0]);
		StringBuilder unit = new StringBuilder("");
		
		if (splitArray.length == 2) { // multi-family
			unit.append(splitArray[1].trim());
		}
		LocalDate begDate = LocalDate.now().minusYears(2); // look back 2 years
		Iterator<Expense> expensesIt = inPropPortfolio.getExpenseList().iterator();
		while (expensesIt.hasNext()) {
			Expense expense = expensesIt.next();
/*			if (expense.getDate().isAfter(begDate) && expense.getType().toUpperCase().equals("REPAIR") 
					&& expense.getBuilding().equalsIgnoreCase(building.toString())
					&& expense.getUnit().equalsIgnoreCase(unit.toString())) { */
				if (expense.getDate().isAfter(begDate) 
						&& expense.getBuilding().equalsIgnoreCase(building.toString())
						&& expense.getUnit().equalsIgnoreCase(unit.toString())) {
				returnList.add(expense);
				
			}
		}
		return returnList;
	}

	public static String breakBuildingUnit(String input) {
		String [] splitArray = input.trim().split("\\s+");
		if (splitArray.length == 1) { // not multi-family
			return input + "|";
		}
		else {
			return splitArray[0] + "|" + splitArray[1];
		}
	}
	public static boolean validateDate(String input) {
		try {
		LocalDate tempDate = LocalDate.parse(input.trim());
		} catch (IllegalArgumentException eDate) {
			return false;
		}
		return true;
	}

	public static boolean validateNumber(String input) {
		try {
//		String [] tempStr = input.split(".");
//		if (tempStr.length > 1) {
			Double.parseDouble(input);
//		} else {
//			Integer.parseInt(input);
//		}
		} catch (NumberFormatException eNum) {
			return false;
		}
		return true;
	}

	public static String printExpense(Expense inExpense) {
		StringBuilder sbTemp = new StringBuilder();
		sbTemp.append("Invoice# " + inExpense.getId());
		sbTemp.append(" Unit: " + inExpense.getBuilding() + " " + inExpense.getUnit());
		sbTemp.append(" Cost: " + Double.toString(inExpense.getAmount()) + " ");
		sbTemp.append(" Date: " + inExpense.getDate().toString());
		sbTemp.append(" Category: " + inExpense.getType());
		sbTemp.append(" Description: " + inExpense.getDescr() + "\n");
		return sbTemp.toString();
	}

	public static int printAllDataToFile(Map<String, PropPortfolio> inPropPortfolioMap) {
		String fileName = "SavedDataNew.dat";
		File tempFile = new File(fileName);
    	if (tempFile.exists()) {
    		tempFile.delete();
    	}
		try {
	  BufferedWriter CSVFile = new BufferedWriter(new FileWriter(fileName));
	  if (StartProMan.yearTotals == 0) {
		  StartProMan.yearTotals = LocalDate.now().getYear();
	  }
	  CSVFile.write("Version1  |   " + Integer.toString(StartProMan.yearTotals) + "\n");
	  
		// SAVE EXPENSES
		// Is this format ok for expenses? need report #
		// CHAD| 12321|82Terr8702| C| maintenance| 2014-04-08| 45| no|report# | re-key fee
	    int totalExp = 0;
	    int totalRev = 0;
		for(String keys: inPropPortfolioMap.keySet()) {
			PropPortfolio activePP = inPropPortfolioMap.get(keys);
			totalExp += activePP.getExpenseListSize();
			totalRev += activePP.getRevenueListSize();
		}
        CSVFile.write(Integer.toString(totalExp) + "\n"); 
		for(String keys: inPropPortfolioMap.keySet()) {
			PropPortfolio activePP = inPropPortfolioMap.get(keys);

			Iterator<Expense> expensesIt = activePP.getExpenseList().iterator();
			while (expensesIt.hasNext()) {
				Expense expense = expensesIt.next();
				StringBuilder sbTemp = new StringBuilder();
				sbTemp.append(activePP.getName() + "|");
				sbTemp.append(expense.getId() + "|");
				sbTemp.append(expense.getBuilding() + "|");
				sbTemp.append(expense.getUnit() + "|");
				sbTemp.append(expense.getType() + "|");
				sbTemp.append(expense.getDate().toString() + "|");
				sbTemp.append(Double.toString(expense.getAmount()) + "|");
				sbTemp.append(expense.getCapitalize() + "|");
				sbTemp.append(expense.getReportNum() + "|");
				sbTemp.append(expense.getDescr()+ "\n");
		        CSVFile.write(sbTemp.toString()); 
			}
		}
		// SAVE REVENUES
        CSVFile.write(Integer.toString(totalRev) + "\n"); 
		for(String keys: inPropPortfolioMap.keySet()) {
			PropPortfolio activePP = inPropPortfolioMap.get(keys);

			Iterator<Revenue> revenuesIt = activePP.getRevenueList().iterator();
			while (revenuesIt.hasNext()) {
				Revenue revenue = revenuesIt.next();
				StringBuilder sbTemp = new StringBuilder();
//  Portfolio| Report num| Building| Unit| Category| Date| Amount| ManageFee| Description
				sbTemp.append(activePP.getName() + "|");
				sbTemp.append(revenue.getReportNum() + "|");
				sbTemp.append(revenue.getBuilding() + "|");
				sbTemp.append(revenue.getUnit() + "|");
				sbTemp.append(revenue.getType() + "|");
				sbTemp.append(revenue.getDate().toString() + "|");
				sbTemp.append(Double.toString(revenue.getAmount()) + "|");
				sbTemp.append(Double.toString(revenue.getManageFee()) + "|");
				sbTemp.append(Integer.toString(revenue.getTenantID()) + "|");
				sbTemp.append(revenue.getDescr()+ "\n");
		        CSVFile.write(sbTemp.toString()); 

			}
		}

		
		
		
		
		// SAVE OPEN ITEMS
	    int totalOpenItems = 0;
		StringBuilder sbTemp = new StringBuilder();
		for(String keys: inPropPortfolioMap.keySet()) {
			PropPortfolio activePP = inPropPortfolioMap.get(keys);
			for(String keys2: activePP.getListBuildings()) {
				for(String keys3: activePP.getUnits(keys2)) {
					String buildingUnitKey = keys2 + " " + keys3;
					List<OpenItem> itemList = activePP.getUnitItems(buildingUnitKey);
					for (OpenItem keys4: itemList) { 
	//  Portfolio| Building Unit| Category| Date| Amount| ManageFee| Description
						sbTemp.append(activePP.getName() + "|");
						sbTemp.append(buildingUnitKey + "|");
						sbTemp.append(keys4.getExpenseCategory() + "|");
						sbTemp.append(keys4.getServicer() + "|");
						sbTemp.append(keys4.getDueDate().toString() + "|");
						sbTemp.append(keys4.getItemDescr()+ "\n");
						totalOpenItems += 1;
					}
				}
				
			}
		}
		// I think it's ok to keep storing all open items in a string builder - is there any size limitations in SB?
        CSVFile.write(Integer.toString(totalOpenItems) + "\n"); 
        CSVFile.write(sbTemp.toString()); 
		CSVFile.close();


		// SAVE REPORT NUMBER INFO?  I can continue to add stuff to the output file later
		

		// SAVE CLIENT in top right field of .client files
		// save TENANT NOTES in description field - may need separate descriptions for tenants/units
// save client portfolio data
        for (String clientName: inPropPortfolioMap.keySet()) {
        	File dirTest = new File("./ClientData");
        	if (!dirTest.exists()) {
	   			int n = JOptionPane.showConfirmDialog(null, "The 'client' subdirectory doesn't exist. Create it? ");
	   			if (n==0) {
	   				dirTest.mkdir();
	   			}
        	}
    		File tempFileClient = new File("./ClientData/"+clientName+".client");  //overwrite previous file
    		tempFileClient.setWritable(true);
//    		File tempFileClient = new File("./ClientData/"+clientName+"_new.client");
   			BufferedWriter CSVFileClient = new BufferedWriter(new FileWriter(tempFileClient));
//   			Warren Buffet Portfolio Properties | 0.1 | 123-456-7890 | warren@big.bucks
//   			Building Key| Unit| Rent Amount| Tenant | Phone # | e-mail | move-in date | Lease end | deposit | balance forward | Descr
   		// (new) Building Key| Unit| Rent Amount| Tenant ID | Descr
   			PropPortfolio activePropPortfolio = inPropPortfolioMap.get(clientName);
   			printPortfolio(activePropPortfolio, CSVFileClient);
   			
   			CSVFileClient.close();
			tempFileClient.setReadOnly();
     }

		// KEEP BUILDING/TENANT INFO ONLY IN CLIENTNAME.CLIENT FILE B/C IT IS RARELY CHANGED?
			
        
        // can't have too many backup files!
    	File dirTest = new File("/BackUpLandLord");
    	if (!dirTest.exists()) {
   				dirTest.mkdir();
    	}
    	File backUpFile = new File("/BackUpLandLord/SavedData.dat");
    	backUpFile.setWritable(true);
    	if (backUpFile.exists()) {
    		backUpFile.delete();
    	}
    	Files.copy(tempFile.toPath(), backUpFile.toPath());
    	backUpFile.setReadOnly();
			//want to remove old file and change name of newly created file to SavedData.dat
			String fileName2 = "SavedData.dat";
			File tempFile2 = new File(fileName2);
			tempFile2.setWritable(true);
			if (tempFile2.exists()) {
				boolean success = tempFile2.delete();
//				tempFile.renameTo(tempFile2);
		    	Files.copy(tempFile.toPath(), tempFile2.toPath());
			} else {
		    	Files.copy(tempFile.toPath(), tempFile2.toPath());
//				tempFile.renameTo(tempFile2);
			}
			tempFile2.setReadOnly();
	    	File backUpServicesFile = new File("/BackUpLandLord/ServicesInput.txt");
			File servicesFile = new File("ServicesInput.txt");
	    	if (backUpServicesFile.exists()) {
	    		backUpServicesFile.delete();
	    	}
	    	Files.copy(servicesFile.toPath(), backUpServicesFile.toPath());

			} catch (IOException e) {
				System.out.println("IO Problems in printAllDataToFile()");
				System.out.println(e.toString());
				return 0;
			}
			return 1; // everything is good with report
	}

	public static void printPortfolio(PropPortfolio activePropPortfolio, BufferedWriter CSVFileClient) {
		try {
		StringBuilder clientSB = new StringBuilder();
		clientSB.append(activePropPortfolio.getName() + "|");
		clientSB.append(activePropPortfolio.getData("MANAGEFEE")+"|");
		clientSB.append(activePropPortfolio.getData("PHONENUM")+"|");
		clientSB.append(activePropPortfolio.getData("EMAIL")+"|");
		// calculate latest report num/year
		String[] reportInfoArray = getReportInfo(activePropPortfolio).split(DELIMITER, -1);
		clientSB.append(reportInfoArray[1] + "|");
		clientSB.append(activePropPortfolio.getClientInfo()+"\n");
       CSVFileClient.write(clientSB.toString()); 
       //CSVFileClient.write("Building Key| Unit| Rent Amount| Tenant | Phone # | e-mail | move-in date | Lease end | deposit | balance forward | Descr \n");
       CSVFileClient.write("Building Key| Unit| Rent Amount| Tenant ID | Descr \n");
       for(String buildingUnit: activePropPortfolio.getIndexBuildingUnitSameOrder()) {
    	   System.out.println(buildingUnit + "\n");
		StringBuilder clientSB2 = new StringBuilder();
		String[] strArray = buildingUnit.split(" ");
		if (strArray.length == 1) {
			clientSB2.append(strArray[0] + "| | ");
		} else {
			clientSB2.append(strArray[0] + "|" + strArray[1] + "|");
		}
		Tenant curTenant = (Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit);
		clientSB2.append(Double.toString((Double)activePropPortfolio.getUnitLevelInfo("RENT", buildingUnit)) + "|");
		if (curTenant == null) {
			clientSB2.append("0 |");
			System.out.println("A tenant was NULL in 'Utils.printPortfolio' ");
		} else {
			clientSB2.append(Integer.toString(curTenant.getID()) + "|");
		}
		clientSB2.append(activePropPortfolio.getUnitLevelInfo("DESCR", buildingUnit) + "\n");
		CSVFileClient.write(clientSB2.toString() );
       }
	} catch (IOException e) {
		System.out.println("IO Problems in printAllDataToFile()");
		return;
	}
}

	// function to read in all data that was previously saved with printAllDataToFile()
	public static void readAllDataFromFile(Map<String, PropPortfolio> propPortfolioMapIn, String fileIn) {
		  try {	
//  Assume file in same directory as main .jar (which is where it is saved to originally)
		  BufferedReader CSVFile = new BufferedReader(new FileReader(fileIn));
		  String Version = CSVFile.readLine();  //utilize this if I ever change data format
		  String [] strArray = Version.split(DELIMITER, -1);
		  if (strArray.length > 1) {
			  if (Integer.parseInt(strArray[1].trim()) == 0) {
				  StartProMan.yearTotals = LocalDate.now().getYear() - 1; //for testing purposes
			  } else {
				  StartProMan.yearTotals = Integer.parseInt(strArray[1].trim()); 
			  }
		  } else {
			  StartProMan.yearTotals = LocalDate.now().getYear();
		  }

		  Integer numExpenses = Integer.parseInt(CSVFile.readLine().trim()); // Read first line that is an integer
		  String dataRow = "";
		  PropPortfolio pPortCur = new PropPortfolio();
		  for (int i = 0; i < numExpenses; i++){ //assume no empty lines, i.e. nobody messed with output file
 			   dataRow = CSVFile.readLine(); // Read next line of data.
			   String [] dataArray = dataRow.split(DELIMITER, -1);
			   if (propPortfolioMapIn.containsKey(dataArray[0].trim().toUpperCase())) {
				   pPortCur = propPortfolioMapIn.get(dataArray[0].trim().toUpperCase());
				   pPortCur.addExpense(dataArray);
				   propPortfolioMapIn.put(dataArray[0].trim().toUpperCase(), pPortCur);
			   } else {
				   System.out.println("Client not in portfolio");  
			   }
		  }

		  Integer numRevenue = Integer.parseInt(CSVFile.readLine().trim()); // Read first line of revenue that is an integer
		  for (int i = 0; i < numRevenue; i++){ //assume no empty lines, i.e. nobody messed with output file
			   dataRow = CSVFile.readLine(); // Read next line of data.
			   String [] dataArray = dataRow.split(DELIMITER, -1);
			   if (propPortfolioMapIn.containsKey(dataArray[0].trim().toUpperCase())) {
				   pPortCur = propPortfolioMapIn.get(dataArray[0].trim().toUpperCase());
				   pPortCur.addRevenue(dataArray);
				   propPortfolioMapIn.put(dataArray[0].trim().toUpperCase(), pPortCur);
			   } else {
				   System.out.println("Client not in portfolio");  
			   }
		  }
		  
		  Integer numItems = Integer.parseInt(CSVFile.readLine().trim()); // Read first line of items that is an integer
		  for (int i = 0; i < numItems; i++){ //assume no empty lines, i.e. nobody messed with output file
			   dataRow = CSVFile.readLine(); // Read next line of data.
			   String [] dataArray = dataRow.split(DELIMITER, -1);
			   String buildingUnit = dataArray[1];
			   if (propPortfolioMapIn.containsKey(dataArray[0].trim().toUpperCase())) {
				   pPortCur = propPortfolioMapIn.get(dataArray[0].trim().toUpperCase());
				   pPortCur.setUnitLevelInfo("ITEM", 
							buildingUnit, 
							dataArray[4] + " | " + dataArray[5] + " | " +
									dataArray[2] + "|" +
									dataArray[3]);

				   propPortfolioMapIn.put(dataArray[0].trim().toUpperCase(), pPortCur);
			   } else {
				   System.out.println("Client not in portfolio");  
			   }
		  
		  }
		  
		  } catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			  // End the printout with a blank line.
		}

/*	public static void readExpense(Map<String, PropPortfolio> propPortfolioMapIn, String fileIn) {
		  try {	
//		  BufferedReader CSVFile = new BufferedReader(new FileReader("C:\\Users\\Chad\\workspace\\ExpenseInput.txt"));
		  BufferedReader CSVFile = new BufferedReader(new FileReader(fileIn));

		  String dataHeader = CSVFile.readLine(); // Read first line that is headers
		  String dataRow = CSVFile.readLine(); // Read first data line.
			  // The while checks to see if the data is null. If 
			  // it is, we've hit the end of the file. If not, 
			  // process the data.
		  PropPortfolio pPortCur = new PropPortfolio();
		  while (dataRow != null){
			  if (dataRow.trim().length() > 1) { //ignore empty lines
			   String [] dataArray = dataRow.split(DELIMITER, -1);
			   if (propPortfolioMapIn.containsKey(dataArray[0].trim().toUpperCase())) {
				   pPortCur = propPortfolioMapIn.get(dataArray[0].trim().toUpperCase());
				   pPortCur.addExpense(dataArray);
				   propPortfolioMapIn.put(dataArray[0].trim().toUpperCase(), pPortCur);
			   } else {
				   System.out.println("Client not in portfolio");  
			   }
			  }
			  dataRow = CSVFile.readLine(); // Read next line of data.
		  }
			  // Close the file once all data has been read.
			  CSVFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			  // End the printout with a blank line.
		}

	public static void readRevenue(Map<String, PropPortfolio> propPortfolioMapIn, String fileIn) {
		  try {	
//		  BufferedReader CSVFile = new BufferedReader(new FileReader("C:\\Users\\Chad\\workspace\\ExpenseInput.txt"));
		  BufferedReader CSVFile = new BufferedReader(new FileReader(fileIn));

		  String dataHeader = CSVFile.readLine(); // Read first line that is headers
		  String dataRow = CSVFile.readLine(); // Read first data line.
			  // The while checks to see if the data is null. If 
			  // it is, we've hit the end of the file. If not, 
			  // process the data.
		  PropPortfolio pPortCur = new PropPortfolio();
		  while (dataRow != null){
			  if (dataRow.trim().length() > 1) {
			   String [] dataArray = dataRow.split(DELIMITER, -1);
			   if (propPortfolioMapIn.containsKey(dataArray[0].trim().toUpperCase())) {
				   pPortCur = propPortfolioMapIn.get(dataArray[0].trim().toUpperCase());
				   pPortCur.addRevenue(dataArray);
				   propPortfolioMapIn.put(dataArray[0].trim().toUpperCase(), pPortCur);
			   } else {
				   System.out.println("Client not in portfolio");  
			   }
			   
			  }
            dataRow = CSVFile.readLine(); // Read next line of data.
		  }
			  // Close the file once all data has been read.
			  CSVFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			  // End the printout with a blank line.
		}  */

	
	//return "maxyear | maxreportnum | yes/no as to whether multiple years spanned
	public static String getReportInfo(PropPortfolio inPort) {
		// check for report#0's with different years
		// check for max report# and max year
		List<Expense> expenses = inPort.getExpenseList();
		Iterator<Expense> expensesIt = expenses.iterator();
		int maxYear = 0;
		int maxReportNum = 0;
		int numTotalExpRevThisReport = 0;
		Map<Integer, String> dummyMap = new HashMap<Integer, String>();
		while (expensesIt.hasNext()) {
			Expense expense = expensesIt.next();
			if (expense.getReportNum() > maxReportNum) {
				maxReportNum = expense.getReportNum();
			}
			if (expense.getDate().getYear() > maxYear) {
				maxYear = expense.getDate().getYear();
			}
			if (expense.getReportNum() == 0) {
				dummyMap.put(expense.getDate().getYear(), "dummy");
				numTotalExpRevThisReport += 1;
			}
		}

		List<Revenue> revenues = inPort.getRevenueList();
		Iterator<Revenue> revenueIt = revenues.iterator();
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if (revenue.getReportNum() > maxReportNum) {
				maxReportNum = revenue.getReportNum();
			}
			if (revenue.getDate().getYear() > maxYear) {
				maxYear = revenue.getDate().getYear();
			}
			if (revenue.getReportNum() == 0) {
				dummyMap.put(revenue.getDate().getYear(), "dummy");
				numTotalExpRevThisReport += 1;
			}
		}
	if (dummyMap.size() > 1) {
		// more than one year represented
		return Integer.toString(maxYear) + "|" + Integer.toString(maxReportNum) + "|" + "YES" + "|" + Integer.toString(numTotalExpRevThisReport); 
	}
	else {
		return Integer.toString(maxYear) + "|" + Integer.toString(maxReportNum) + "|" + "NO" + "|" + Integer.toString(numTotalExpRevThisReport); 
		
	}
	

	}
	public static void archiveAllDataToFile(Map<String, PropPortfolio> propPortfolioMapIn, String fileOut, String archiveYears) {
		// Should they be able to choose file name?  Must prevent them from using "SavedData.dat".
		//String fileOut = "DataArchive" + Integer.toString(LocalDate.now().getYear()-1) + ".dat";
		// Archive all data up to and including archiveYears year?
		// Must remove all revenue/expense/open items associated with portfolios that have been deleted
	}
}  //end class
