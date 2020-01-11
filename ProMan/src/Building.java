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
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.LocalDate;
//import Unit;

public class Building implements Serializable {
	private int id;
	private String name;
	private List<String> unitNames = new ArrayList<String>();
	private Map<String, Unit> units = new HashMap<String, Unit>();
	
	
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
	public Map<String, Unit> getUnits(){
		return this.units;
	}
	public void setUnits(Map<String, Unit> inUnits){
		this.units = inUnits;
	}
	public List<String> getUnitsList(){
		return this.unitNames;
	}
	public void addUnit(String name, double rentAmt, Tenant tenant, String descr){
		Unit newUnit = new Unit();
		newUnit.setName(name);
		newUnit.setRent(rentAmt);
		newUnit.setTenant(tenant);
		newUnit.setDescr(descr);
		unitNames.add(name);
		this.units.put(name, newUnit);
	}
/*	public void addUnit(String name, double rentAmt, String tenName, String inPhone, String inEmail, 
			String inMoveIn, String leaseEnd, double dep, double bal, String descr){
		Unit newUnit = new Unit();
		newUnit.setName(name);
		newUnit.setRent(rentAmt);
		newUnit.setTenant(tenName);
		newUnit.setPhoneNum(inPhone);
		newUnit.setEmail(inEmail);
		if (inMoveIn.equalsIgnoreCase("")) {
			newUnit.setMoveInDate(LocalDate.parse("1900-01-01"));
		} else { //assume a valid date
			newUnit.setMoveInDate(LocalDate.parse(inMoveIn));
		}
		newUnit.setLeaseEnd(leaseEnd);
		newUnit.setDeposit(dep);
		newUnit.setBalanceForward(bal);
		newUnit.setDescr(descr);
		unitNames.add(name);
		this.units.put(name, newUnit);
	} */

}
