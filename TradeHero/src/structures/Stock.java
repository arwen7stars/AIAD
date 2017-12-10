package structures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Stock
{
	
	public String name; 					// Stock
    public int day;							
    public int month;
    public int year;
    public double value;					// Stock
    public double savedValue = 0.0;			// StockOwned
    public int quantity = 0;				// StockOwned
    
    public Stock(String stockDate, String open, String close) {
    	this.parseDate(stockDate);
    	
    	double openInt = Double.parseDouble(open);
    	double closeInt = Double.parseDouble(close);
    	
    	this.value = (openInt + closeInt) / 2;
    }
    
    public Stock(String name, String value) {
    	this.name = name;
    	this.value = Double.parseDouble(value);
    }
    
    public Stock(String name, int quantity, double savedValue) {
    	this.name = name;
    	this.quantity = quantity; 
    	this.savedValue = savedValue;
    }
    
    public void parseDate(String stockDate) {
    	String[] parts = stockDate.split("-");
    	this.day = Integer.parseInt(parts[0]);	        
    	this.year = Integer.parseInt(parts[2]);
    	
        Date date = null;
		try {
			date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(parts[1]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        this.month = cal.get(Calendar.MONTH) + 1;
    }
    
    public double getValue() {
    	return this.value;
    }

	public String getName() {
		return name;
	}
    
	public double getSavedValue() {
		return savedValue;
	}
	
	public void saveValue(double value) {
		savedValue = value;
	}

	public Integer getQuantity() {
		return quantity;
	}
    
 }