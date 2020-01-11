import java.io.Serializable;
import org.joda.time.LocalDate;
//import Unit;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;

public class Expense implements Serializable {
	private int id;
	private int reportNum=0;
	private String typeStr;
	private String capitalizeStr;
	private String descriptionStr;
	private String buildingName;
	private String unitName;
	private double amount;
	private LocalDate date;
	
//format:Portfolio, Invoice Num, Building, Unit, Category, Date, Amount, Capitalize, Description
	
	public Expense build(String [] itemList){
		this.typeStr = itemList[4].trim().toUpperCase();
		this.amount = Double.parseDouble(itemList[6].trim());
		this.unitName = itemList[3].replaceAll("\\s+","").toUpperCase();
		this.buildingName = itemList[2].replaceAll("\\s+","").toUpperCase();
		// DateTimeFormatter.ISO_LOCAL_DATE format is "yyyy-mm-dd"
//		this.date = LocalDate.parse(itemList[5].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
		this.date = LocalDate.parse(itemList[5].trim());
		this.capitalizeStr = itemList[7].trim().toUpperCase();
		this.reportNum = Integer.parseInt(itemList[8].trim());
		this.descriptionStr = itemList[9].trim().toUpperCase();
		if (itemList[1].trim().length() > 0)
			this.id = Integer.parseInt(itemList[1].trim());
		else
			this.id = 0;
		
		return this;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return this.id;
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

	public LocalDate getDate(){
		return this.date;
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
	public String getCapitalize(){
		return this.capitalizeStr;
	}

}
