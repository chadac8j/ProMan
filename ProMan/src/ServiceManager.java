import java.util.HashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;


public class ServiceManager {

    final String DELIMITER = "\\|";
	private List<Service> serviceList = new ArrayList<Service>();
	private Map<String, Service> serviceMap = new HashMap<String, Service>();

	public List<Service> getServiceList() {
		return this.serviceList;
	}
	
	public Map<String, Service> getServiceMap() {
		return this.serviceMap;
	}
	
	public Object [][] genServiceTable() {
		
		// The size of this object isn't quite right
		Object[][] returnVal = new Object[serviceList.size()+1][Service.serviceHeaders.size()];
		for (int i = 0; i < returnVal.length; i++) { //must initialize to something to avoid later null pointer exceptions
			for (int j = 0; j < returnVal[i].length; j++){
				returnVal[i][j] = ""; //init to blank
			}
		}

		// create header
		int indrow = 1;
		for (Service serviceItem : serviceList) {
				returnVal[indrow][Service.serviceHeaders.get("COMPANY")] = serviceItem.getName();
				returnVal[indrow][Service.serviceHeaders.get("SERVICE")] = serviceItem.getServiceType();
				returnVal[indrow][Service.serviceHeaders.get("WEBSITE")] = serviceItem.getWebsite();
				returnVal[indrow][Service.serviceHeaders.get("DESCRIPTION")] = serviceItem.getDescr();
				returnVal[indrow][Service.serviceHeaders.get("PHONENUM")] = serviceItem.getPhoneNum();
				returnVal[indrow][Service.serviceHeaders.get("EMAIL")] = serviceItem.getEmail();
				List<ServiceItem> tempList = serviceItem.getItemList();  //just get first item in list?
				if (!tempList.isEmpty()) {
				    System.out.println("indices:  " + Integer.toString(indrow) + "   "); 
					ServiceItem tempServiceItem = tempList.get(0); //get most recent item (why?)
					OpenItem tempItem = tempServiceItem.getItem();
					if (tempList.size() > 1) {
						returnVal[indrow][Service.serviceHeaders.get("OPEN ITEMS")] = "(...) " + tempItem.getItemDescr();
					} else {
						returnVal[indrow][Service.serviceHeaders.get("OPEN ITEMS")] = tempItem.getItemDescr();
					}
					
					returnVal[indrow][Service.serviceHeaders.get("DATE DUE")] = tempItem.getDueDate().toString();
				}
				indrow += 1;
		}
		return returnVal;
	}

	public void genServiceList(String fileIn) {
		  try {	
			  // Want to make all keys to buildings/units to be CAPITALIZED AND TRIMMED
			  BufferedReader CSVFile = new BufferedReader(new FileReader(fileIn));
			  String dataTitle = CSVFile.readLine(); // Read first line that is title, also MANAGEFEE info
			  String dataHeader = CSVFile.readLine(); // Read second line that is headers
			  
			  String dataRow = CSVFile.readLine(); // Read first data line.
			  while (dataRow != null && dataRow.trim().length() > 0){
				   String [] dataArray = dataRow.split(DELIMITER, -1);
				   Service newServiceItem = new Service();
//				   Company Name | Service | Web Address | Phone # | e-mail | Descr
				   newServiceItem.setName(dataArray[0].trim());
				   newServiceItem.setServiceType(dataArray[1].trim());
				   newServiceItem.setWebsite(dataArray[2].trim());
				   newServiceItem.setPhoneNum(dataArray[3].trim());
				   newServiceItem.setEmail(dataArray[4].trim());
				   newServiceItem.setDescr(dataArray[5].trim());
				   serviceList.add(newServiceItem);
				   serviceMap.put(newServiceItem.getName(), newServiceItem);
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


	public boolean validateService(String inputServiceTest) {
//		boolean isInList = false;
		for (Service curService: serviceList) {
			if (curService.getName().equalsIgnoreCase(inputServiceTest.trim())) {
				return true;
//				isInList = true;
			}
		}
		return false;
//		return isInList;
	}
}
