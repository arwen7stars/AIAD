package tradeHero;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.domain.DFService;

public class StockAgent extends Agent {
	private Map<String, Stock> stockHistory = new HashMap<String, Stock>();		// historico de acoes extraido de google finance
	private Stock actualStockValue;
	private String name ="";
	
	public class Stock
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
	    
	    public double getValue() {
	    	return this.value;
	    }
	    
	    
	 };
	
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
		
		addBehaviour(new StockInformationServer());
		
		
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
	
	
	private class StockInformationServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				
				System.out.println("Hi Market");
				
				String date = msg.getContent();
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				
				Stock st = stockHistory.get(date); 
				if (st != null) {
					
					reply.setContent(name +  "&" + st.getValue());
					System.out.println("Stoke-agent "+getAID().getName()+": sent: " + reply.toString());
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					
					reply.setContent("error");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer

	
	
	
	
	
	
}
