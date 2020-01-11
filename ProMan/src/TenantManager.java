import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.joda.time.LocalDate;


public class TenantManager {

    final String DELIMITER = "\\|";
    static int ID_Max = 0;
	private List<Tenant> tenantList = new ArrayList<Tenant>();
	private Map<Integer, Tenant> tenantMap = new HashMap<Integer, Tenant>();

	public void addTenant(Tenant inTenant) {
		ID_Max += 1;
		inTenant.setID(ID_Max);
		tenantList.add(inTenant);
		tenantMap.put(ID_Max, inTenant);
		
	}
	public void removeTenant(Tenant inTenant) {
// do something here later?		
	}
	public static Tenant createDummyTenant() {
		Tenant dummyTenant = new Tenant();
		dummyTenant.setDescr("None Entered Yet");
		dummyTenant.setID(0); // should I return all vacant units to have tenant with ID 0? or save old tenants to another archive file?
		dummyTenant.setTenant("VACANT"); 
		dummyTenant.setPhoneNum(""); 
		dummyTenant.setEmail(""); 
		dummyTenant.setDeposit(0.0); 
		dummyTenant.setMoveInDate(LocalDate.parse("1900-01-01")); 
		dummyTenant.setLeaseEnd(""); 
		dummyTenant.setBalanceForward(0.0); 
		return dummyTenant;
	}
	
	public List<Tenant> getTenantList() {
		return this.tenantList;
	}
	
	public Map<Integer, Tenant> getTenantMap() {
		return this.tenantMap;
	}

	public void genTenantList(String fileIn) {
		  try {	
			  // Want to make all keys to buildings/units to be CAPITALIZED AND TRIMMED
			  BufferedReader CSVFile = new BufferedReader(new FileReader(fileIn));
			  String dataTitle = CSVFile.readLine(); // Read first line that is title
			  String dataHeader = CSVFile.readLine(); // Read second line that is static number of IDs generated
			  ID_Max = Integer.parseInt(dataHeader.trim());
			  String dataRow = CSVFile.readLine(); // Read first data line.
			  while (dataRow != null && dataRow.trim().length() > 0){
				  
// int id | String tenantName | LocalDate moveInDate | String leaseEnd |double balanceForward | double deposit | 
//			String phoneNum |	String email |	String descr
					
				   String [] dataArray = dataRow.split(DELIMITER, -1);
				   Tenant newTenantItem = new Tenant();
				   newTenantItem.setID(Integer.parseInt(dataArray[0].trim()));
				   newTenantItem.setTenant(dataArray[1].trim());
				   newTenantItem.setMoveInDate(LocalDate.parse(dataArray[2].trim()));
				   newTenantItem.setLeaseEnd(dataArray[3].trim());
				   newTenantItem.setBalanceForward(Double.parseDouble(dataArray[4].trim()));
				   newTenantItem.setDeposit(Double.parseDouble(dataArray[5].trim()));
				   newTenantItem.setPhoneNum(dataArray[6].trim());
				   newTenantItem.setEmail(dataArray[7].trim());
				   newTenantItem.setDescr(dataArray[8].trim());

				   tenantList.add(newTenantItem);
				   tenantMap.put(newTenantItem.getID(), newTenantItem);
				   dataRow = CSVFile.readLine(); // Read next line of data.
			  }
				  // Close the file once all data has been read.
			  CSVFile.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

	}

	public void printTenantList(String fileIn) {
		  try {	
		      File tempFile = new File(fileIn);
		      tempFile.setWritable(true);
			  BufferedWriter CSVFile = new BufferedWriter(new FileWriter(fileIn));
			  CSVFile.write("Version1   ");
			  CSVFile.newLine();
			  CSVFile.write(Integer.toString(ID_Max));
			  CSVFile.newLine();
			  Iterator<Tenant> tenantIt = tenantList.iterator();
			  while (tenantIt.hasNext()) {
					Tenant tenant = tenantIt.next();
					StringBuilder sbTemp = new StringBuilder();
					sbTemp.append(Integer.toString(tenant.getID()) + "|");
					sbTemp.append(tenant.getTenant() + "|");
					sbTemp.append(tenant.getMoveInDate().toString() + "|");
					sbTemp.append(tenant.getLeaseEnd() + "|");
					sbTemp.append(Double.toString(tenant.getBalanceForward()) + "|");
					sbTemp.append(Double.toString(tenant.getDeposit()) + "|");
					sbTemp.append(tenant.getPhoneNum() + "|");
					sbTemp.append(tenant.getEmail() + "|");
					sbTemp.append(tenant.getDescr());
			        CSVFile.write(sbTemp.toString()); 
					CSVFile.newLine();
				}
	   			CSVFile.close();
	        // can't have too many backup files!
	    	File dirTest = new File("/BackUp");
	    	if (!dirTest.exists()) {
	   				dirTest.mkdir();
	    	}
	    	File backUpFile = new File("/BackUp/TenantInput.txt");
	    	if (backUpFile.exists()) {
	    		backUpFile.delete();
	    	}
	    	Files.copy(tempFile.toPath(), backUpFile.toPath());
	    	backUpFile.setReadOnly();
	    	tempFile.setReadOnly();
			  
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
}


	public int genFinalTenantReport(Tenant closedTenant, PropPortfolio inPropPortfolio, String stuff){

		
   		StringBuilder sbTenant = new StringBuilder();
		sbTenant.append("\n Tenant moved in on " + closedTenant.getMoveInDate().toString() + " with a deposit of " + 
		  Double.toString(closedTenant.getDeposit()) + " \n");
		sbTenant.append("Tenant owed " + Double.toString(closedTenant.getBalanceForward()) + " at the beginning of the year. \n");
   		sbTenant.append(stuff);

		
		String fileName = closedTenant.getTenant().trim()+Integer.toString(closedTenant.getID())+"FinalReport.txt";
		File tempFile = new File(fileName);
		if (tempFile.exists()) {
			return 0;
		}
		try {
	  BufferedWriter CSVFile = new BufferedWriter(new FileWriter(fileName));
	  CSVFile.write("This is a final tenant report for "+ closedTenant.getTenant() + "(ID#" +  
	  Integer.toString(closedTenant.getID()) + ") "+  " generated on: " + LocalDate.now().toString()+ "\n");
		CSVFile.write(sbTenant.toString()+"\n"); // Print the data line.
		CSVFile.newLine();
	    List<Revenue> tempList = inPropPortfolio.getRevenueList();
		Iterator<Revenue> revenueIt = tempList.iterator();
		double totalRev = 0.0;
		while (revenueIt.hasNext()) {
			Revenue revenue = revenueIt.next();
			if (revenue.getTenantID() == closedTenant.getID()) {
				totalRev += revenue.getAmount();
				CSVFile.write("\n Rent Payment: " + Double.toString(revenue.getAmount()) + " on: " + 
						revenue.getDate().toString());
			}
		}
		CSVFile.write("\n \n Total Rent Payments: " + Double.toString(totalRev)); 
		CSVFile.close();

		} catch (IOException e) {
			System.out.println("IO Problems in genFinalTenantReport()");
		}
		return 1; // everything is good with report
		
	}
	
	
}