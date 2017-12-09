
package structures;

public class Tip {
	public String name;			// stock name
	public String type;
	public String date;
	public double stockValue;
	
	public Tip(String name, String type, String date, double value) {
		this.name = name;
		this.date = date;
		this.type = type;		
		this.stockValue = value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDate() {
		return date;
	}

	public double getStockValue() {
		return stockValue;
	}
}