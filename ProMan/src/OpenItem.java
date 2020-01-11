import java.io.Serializable;

import org.joda.time.LocalDate;


public class OpenItem implements Serializable {
	private LocalDate dueDate;
	private String itemType;
	private String itemDescr;
	private String expenseCategory;
	private String servicerName;
	
	public void setServicer(String inService){
		this.servicerName = inService;
	}

	public String getServicer(){
			return this.servicerName;
	}

	public void setItemDescr(String inDescr){
		this.itemDescr = inDescr;
	}

	public String getItemDescr(){
			return this.itemDescr;
	}

	public void setExpenseCategory(String inExpenseCategory){
		this.expenseCategory = inExpenseCategory;
	}

	public String getExpenseCategory(){
			return this.expenseCategory;
	}

	public void setDueDate(String inDate){
		// check for valid date?
		//LocalDate dueDate = new LocalDate();
			this.dueDate = LocalDate.parse(inDate.trim());
		
	}
	public LocalDate getDueDate(){
		return this.dueDate;
	}
}
