package tradeHero.behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import tradeHero.StockAgent;
import structures.Stock;

public abstract class ReceiveStockUpdate extends CyclicBehaviour {

	protected ArrayList<Stock> stocksPrice;
	protected String today = "";
	
	@Override
	public void action() {
		// TODO Auto-generated method stub

		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("stoke-value"));
				
		ACLMessage msg = myAgent.receive(mt);
		
		
		
	
		if(msg != null) {
			
			System.out.print("I am an user and I received: " + msg.getContent()); 	// stoke value received from market agent 
			
			
			stocksPrice = new ArrayList<Stock>();
			String[] parts = msg.getContent().split("\n");
			
			today = parts[0];			
			for(int i = 1; i < parts.length; i++) {
				String[] stock = parts[i].split("&");
				if(stock.length == 2)
					stocksPrice.add(new Stock(stock[0], stock[1]));
				
			}
			
			buyerAction();	
			
		}else {
			block();
		}
		
		
		
	}
	
	public abstract void buyerAction() ;
	

}
