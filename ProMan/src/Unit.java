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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.joda.time.LocalDate;


public class Unit implements Serializable {
	private int id;
	private String name;
	private double rentAmount;
	private Tenant tenant;
	private String descr;
	private List<OpenItem> openItemList = new ArrayList<OpenItem>();

/*	//	private LocalDate moveInDate = LocalDate.parse(LocalDate.now().getYear()+"-01-01");
	private LocalDate moveInDate;
	private String leaseEnd;
	private double balanceForward;
	private double deposit;
	private String tenantName;
	private String phoneNum;
	private String email;
	*/
	public static final Map<String, Integer> tenantBuildingHeaders = new HashMap<String, Integer>() {{
		put("BUILDING/UNIT", 0); put("RENT", 1); put("TENANT", 2);  put("PHONENUM", 3); 
		put("EMAIL", 4); put("DESCRIPTION", 5);
		 put("OPEN ITEMS", 6); put("DATE DUE", 7); put("CONTRACTOR", 8);
	}};
	public static final Map<Integer, String> mapMonths = new HashMap<Integer, String>() {{
		put(0, "JANUARY"); put(1, "FEBRUARY"); put(2, "MARCH");  put(3, "APRIL"); 
		put(4, "MAY"); put(5, "JUNE"); put(6, "JULY"); put(7, "AUGUST");
		put(8, "SEPTEMBER"); put(9, "OCTOBER"); put(10, "NOVEMBER"); put(11, "DECEMBER");
	}}; // notice indexing is 0-based for calendar objects and 1-based for joda time!!!
	public static final Map<String, Integer> mapMonthsBack = new HashMap<String, Integer>() {{
		put("JANUARY", 1); put("FEBRUARY", 2); put("MARCH", 3);  put("APRIL", 4); 
		put("MAY", 5); put("JUNE", 6); put("JULY", 7); put("AUGUST", 8);
		put("SEPTEMBER", 9); put("OCTOBER", 10); put("NOVEMBER", 11); put("DECEMBER", 12);
	}};

public void setDescr(String inDescr){
	this.descr = inDescr;
}

public String getDescr(){
		return this.descr;
}

public void setName(String name){
	this.name = name;
}
public String getName(){
	return this.name;
}

public void setTenant(Tenant inTenant){
	this.tenant = inTenant;
}
public Tenant getTenant(){
	return this.tenant;
}

public void addItem(OpenItem inItem) {
	this.openItemList.add(inItem);
}

public List<OpenItem> getItemList() {
	return this.openItemList;
}

public void setItemList(List<OpenItem> inList) {
	this.openItemList = inList;
}

public void setRent(double rent){
	this.rentAmount = rent;
}
public double getRent(){
	return this.rentAmount;
}
/*
public void setLeaseEnd(String inLeaseEnd){
	this.leaseEnd = inLeaseEnd;
}
public String getLeaseEnd(){
	return this.leaseEnd;
}

public void setMoveInDate(LocalDate inMoveInDate){
	this.moveInDate = inMoveInDate;
}
public LocalDate getMoveInDate(){
	return this.moveInDate;
}


public void setBalanceForward(double inBalanceForward){
	this.balanceForward = inBalanceForward;
}
public double getBalanceForward(){
	return this.balanceForward;
}

public void setDeposit(double inDeposit){
	this.deposit = inDeposit;
}
public double getDeposit(){
	return this.deposit;
}

public void setTenant(String tenant){
	this.tenantName = tenant;
}
public String getTenant(){
	return this.tenantName;
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
*/
}