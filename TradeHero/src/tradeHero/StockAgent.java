package tradeHero;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.domain.DFService;
import structures.Stock;
import structures.randomCalc;
import tradeHero.behaviours.StockInformationServer;

public class StockAgent extends Agent {
	private Map<String, Stock> stockHistory = new HashMap<String, Stock>();		// historico de acoes extraido de google finance
	private Stock actualStockValue;
	private String name ="";
	
	
	
	
	public StockAgent(String name) {this.name = name;}
	
	@Override
	public void setup() {
		
		// subscribe DF
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("stock");
		sd.setName(getLocalName());
		template.addServices(sd);
		
		try {
			DFService.register(this, template);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new StockInformationServer(this, stockHistory, name));
		
		
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
                stockHistory.put(date, st);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //Collections.reverse(stockHistory);
        
        actualStockValue = stockHistory.get(0);		// stock history starts on day one
	}

	public Map<String, Stock> getStockHistory() {
		return stockHistory;
	}

	public void setStockHistory(Map<String, Stock> stockHistory) {
		this.stockHistory = stockHistory;
	}

	public Stock getActualStockValue() {
		return actualStockValue;
	}

	public void setActualStockValue(Stock actualStockValue) {
		this.actualStockValue = actualStockValue;
	}

	public String getStockName() {
		return name;
	}

	public void setStockName(String name) {
		this.name = name;
	}

	public String newTip(String today) {
		
		boolean stop = false;
		
		boolean unknown = true;
		boolean dec = false;
		
		double maxOrmin =  stockHistory.get(today).getValue();
		String day = today;
		
		 
		while(true) {
			
			String tomorrow = randomCalc.tomorrow(day);
			double aux = stockHistory.get(tomorrow).getValue();
			
			if(unknown && aux < maxOrmin) {
				unknown = false;
				dec = true;
			}else if(unknown) {
				unknown = false;
			}
			
			
			if(dec && aux < maxOrmin) {
				day = tomorrow;
				
			}else if (!dec && aux > maxOrmin ) {
				day = tomorrow;
			}else {
				break;
			}
			
			day = tomorrow;
			maxOrmin = aux;
			
		}
			
		return getStockName() + (dec ? "&min&" : "&max&" ) + day + "&" + maxOrmin;
	}
	

	
	
	
	
	
	
	
}
