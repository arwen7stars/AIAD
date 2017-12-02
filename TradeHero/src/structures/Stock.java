package structures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Stock
{
	
	public String name; 
    public int day;
    public int month;
    public int year;
    public double value;
    
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
    
    
 }