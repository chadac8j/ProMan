import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Service implements Serializable {

	private int id;
	private String companyName;
	private double discountPercent;
	private List<String> contactName = new ArrayList<String>();
	private String serviceType;
	private String descr;
	private String phoneNum;
	private String email;
	private String website;
	private List<ServiceItem> openItemList = new ArrayList<ServiceItem>();
	public static final Map<String, Integer> serviceHeaders = new HashMap<String, Integer>() {{
		put("COMPANY", 0); put("SERVICE", 1); put("WEBSITE", 2);  put("PHONENUM", 3); 
		put("EMAIL", 4); put("DESCRIPTION", 5);
		 put("OPEN ITEMS", 6); put("DATE DUE", 7);
	}};


public void setDescr(String inDescr){
	this.descr = inDescr;
}

public String getDescr(){
		return this.descr;
}

public void setServiceType(String inServiceType){
	this.serviceType = inServiceType;
}

public String getServiceType(){
		return this.serviceType;
}

public void setName(String name){
	this.companyName = name;
}
public String getName(){
	return this.companyName;
}

public void setPhoneNum(String phonenum){
	this.phoneNum = phonenum;
}
public String getPhoneNum(){
	return this.phoneNum;
}

public void setEmail(String inEmail){
	this.email = inEmail;
}
public String getEmail(){
	return this.email;
}

public void setWebsite(String inWebsite){
	this.website = inWebsite;
}
public String getWebsite(){
	return this.website;
}

public void addItem(ServiceItem inItem) {
	this.openItemList.add(inItem);
}

public List<ServiceItem> getItemList() {
	return this.openItemList;
}

public void setItemList(List<ServiceItem> inList) {
	this.openItemList = inList;
}

}
