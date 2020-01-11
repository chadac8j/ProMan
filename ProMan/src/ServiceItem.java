import java.io.Serializable;


public class ServiceItem implements Serializable {

	
	private String buildingUnit;
	private String portfolio;
	private OpenItem item;
	
	public void setBuildingUnit(String inBuildingUnit){
		this.buildingUnit = inBuildingUnit;
	}

	public String getBuildingUnit(){
			return this.buildingUnit;
	}

	public void setPortfolio(String inPortfolio){
		this.portfolio = inPortfolio;
	}

	public String getPortfolio(){
			return this.portfolio;
	}

	public void setItem(OpenItem inItem){
		this.item = inItem;
	}

	public OpenItem getItem(){
			return this.item;
	}

	public String printItem(){
		StringBuilder sbOut = new StringBuilder();
		sbOut.append("  Portfolio: ");
		sbOut.append(portfolio);
		sbOut.append("  Building/Unit: ");
		sbOut.append(buildingUnit);
//		sbOut.append("  Expense Category: ");
//		sbOut.append(item.getExpenseCategory());
		sbOut.append("  Due Date: ");
		sbOut.append(item.getDueDate().toString());
		sbOut.append("  Description: ");
		sbOut.append(item.getItemDescr());
		return sbOut.toString();
}

}
