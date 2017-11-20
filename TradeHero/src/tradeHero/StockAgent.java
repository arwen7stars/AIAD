package tradeHero;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import sajas.core.Agent;

public class StockAgent extends Agent {
	private ArrayList<Stock> stockHistory = new ArrayList<Stock>();		// historico de acoes extraido de google finance
	private Stock actualStockValue;
	
	class Stock
	{
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
	 };
	
	public StockAgent() {}
	
	@Override
	public void setup() {
		
		// subscribe DF
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("service-provider");
		template.addServices(sd);
		
	}
	
	public void readHistory(String csvFile) {
        String line = "";
        String cvsSplitBy = ",";
		
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	br.readLine();
            while ((line = br.readLine()) != null) {

                String[] stockValue = line.split(cvsSplitBy);
                String date = stockValue[0];
                String open = stockValue[1];
                String close = stockValue[4];
                
                Stock st = new Stock(date, open, close);
                stockHistory.add(st);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(stockHistory);
        
        actualStockValue = stockHistory.get(0);		// stock history starts on day one
	}

	public ArrayList<Stock> getStockHistory() {
		return stockHistory;
	}

	public void setStockHistory(ArrayList<Stock> stockHistory) {
		this.stockHistory = stockHistory;
	}

	public Stock getActualStockValue() {
		return actualStockValue;
	}

	public void setActualStockValue(Stock actualStockValue) {
		this.actualStockValue = actualStockValue;
	}
}
