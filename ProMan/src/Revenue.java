import java.io.Serializable;
import org.joda.time.LocalDate;
//import Unit;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;

public class Revenue implements Serializable {
	private int id;
	private int tenantID;
	private int reportNum=0;
	private String typeStr;
//	private String capitalizeStr;
	private String descriptionStr;
	private String buildingName;
	private String unitName;
	private double amount;
	private double manageFee;
	private LocalDate recvdate;
	//private LocalDate date; //have other date for which month applied to?
	
//format:Portfolio, report num, Building, Unit, Category, Date, Amount, ManageFee, tenantID, Description
	
	public Revenue build(String [] itemList){
		this.typeStr = itemList[4].trim().toUpperCase();
		this.reportNum = Integer.parseInt(itemList[1].trim());
		this.amount = Double.parseDouble(itemList[6].trim());
		this.unitName = itemList[3].replaceAll("\\s+","").toUpperCase();
		this.buildingName = itemList[2].replaceAll("\\s+","").toUpperCase();
		// DateTimeFormatter.ISO_LOCAL_DATE format is "yyyy-mm-dd"
//		this.recvdate = LocalDate.parse(itemList[5].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
		this.recvdate = LocalDate.parse(itemList[5].trim());
		this.manageFee = Double.parseDouble(itemList[7].trim());
		this.tenantID = Integer.parseInt(itemList[8].trim());
		this.descriptionStr = itemList[9].trim().toUpperCase();
		return this;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return this.id;
	}

	public void setTenantID(int id){
		this.tenantID = id;
	}

	public int getTenantID(){
		return this.tenantID;
	}

	public void setReportNum(int inReportNum){
		this.reportNum = inReportNum;
	}

	public int getReportNum(){
		return this.reportNum;
	}

	public String getDescr(){
		return this.descriptionStr;
	}

	public void setDescr(String descr){
		this.descriptionStr = descr;
	}
	public void setAmount(double input){
		this.amount = input;
	}

	public double getAmount(){
		return this.amount;
	}

	public double getManageFee(){
		return this.manageFee;
	}

	public LocalDate getDate(){
		return this.recvdate;
	}

	public String getBuilding(){
		return this.buildingName;
	}

	public String getType(){
		return this.typeStr;
	}

	public String getUnit(){
		return this.unitName;
	}
}
