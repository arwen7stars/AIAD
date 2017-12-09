package tradeHero.behaviours;

import java.util.ArrayList;
import java.util.Map;

import com.sun.corba.se.impl.copyobject.ReferenceObjectCopierImpl;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.AID;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.domain.DFService;
import structures.Stock;
import structures.randomCalc;
import tradeHero.UserAgent;

public abstract class ReceiveStockUpdate extends CyclicBehaviour {

	protected ArrayList<Stock> stocksPrice;
	protected String today = "";
	private UserAgent myUserAgent = null;
	
	protected ReceiveStockUpdate(UserAgent agent){
		super();
		this.myUserAgent = agent;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub

		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("stoke-value"));
				
		ACLMessage msg = myAgent.receive(mt);
				
	
		if(msg != null) {
		 			
			stocksPrice = new ArrayList<Stock>();
			String[] parts = msg.getContent().split("\n");
			
			today = parts[0];			
			for(int i = 1; i < parts.length; i++) {
				String[] stock = parts[i].split("&");
				if(stock.length == 2)
					stocksPrice.add(new Stock(stock[0], stock[1]));
				
			}
			myUserAgent.stocksPrice = stocksPrice;
			buyerAction();	
			
		}else {
			block();
		}
		
		
		
	}
	
	public abstract void buyerAction();
	
	/* Informs followers about today's purchases/sales */
	public void alertFollowers(String s) {
		/* Common to all users */
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		ArrayList<AID> followers = myUserAgent.getFollowers();
		
		String ss = "";
		
		for(int i = 0; i < followers.size(); i++)
			ss +=  followers.get(i).getLocalName() + " ; ";
			
		System.out.println("I, " + myAgent.getLocalName() + ", will try to send to " + ss +": " + s);
		
		if(followers.size() == 0)
			return;
		
		for(int i = 0; i <  followers.size(); i++) {
			msg.addReceiver( followers.get(i));
			
		}
		
		msg.setContent(s);
		msg.setConversationId("follow-tip");
		
		myAgent.send(msg);
	}
	
	/* Informs Market  */
	public void updateGain(double gain) {
		/* Common to all users */
		 
		DFAgentDescription template = new DFAgentDescription();
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("market");
		template.addServices(sd);
		
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(result[0].getName());
			msg.setContent(myAgent.getLocalName() + "&" + gain);
			msg.setConversationId("gains");
			System.out.println("I," + myAgent.getLocalName() +  ", update my gains with the following message: " + msg.getContent() );
			myAgent.send(msg);
						
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	protected String getProb(Stock stock) {
		// TODO:
		double answer = 0.2;
		
		
		ACLMessage mesg = new ACLMessage(ACLMessage.REQUEST);
		mesg.setConversationId("ROC");
		mesg.setContent(today);
		mesg.addReceiver(new AID(stock.getName(), AID.ISLOCALNAME));
		
		myAgent.send(mesg);
		
		
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("ROC"));
		ACLMessage msg = myAgent.receive(mt);
		
		
		
		if(msg != null) {				
			
			String content = msg.getContent();
			answer = Double.parseDouble(content);
			
			
		}else {
			block();
		}
		
		
		myUserAgent.updateRoc(stock.getName(), answer);
		
		System.out.println("[ROC] I, " +  myAgent.getLocalName() + ", received the following ROC: " + myUserAgent.getRocValue(stock.getName()));
		
		int maxStocks = ((int)(myUserAgent.getCash()/stock.getValue()));
		int total = (int)(Math.random()*maxStocks);
		String s = "";
		
		if(answer > randomCalc.ROC_CONST && Math.random() < randomCalc.ROC_SUCCESS) {
			
			
			
			myUserAgent.buyStocks(stock.getName(), stock.getValue(), total);
			
			s += "buy&" + stock.getName() + "&" + total  +"&" + stock.getValue() + "\n";
			
			
		}else if(answer < randomCalc.ROC_CONST && Math.random() < randomCalc.ROC_SUCCESS) {
			
			
			myUserAgent.sellStocks(stock.getName(), stock.getValue(), total);

			s += "sell&" + stock.getName() + "&" + total +"&" + stock.getValue() + "\n";
			
		}
			
		System.out.print("[BUY|SELL] I, " + myAgent.getLocalName() + " performed the following: " + s);	
		
		return s;
		
	}
	
	
	private int getNoStocks(double actualPrice, double stockTip, boolean type) {
		int maxStocks = ((int)(myUserAgent.getCash()/actualPrice));
		int noStocks = -1;
		double elapsedValue = 0.0;
		
		if(type) {		// tip is a maximum value (most likely will sell stocks)
			elapsedValue = (stockTip - actualPrice)/actualPrice;
		} else {		// tip is a minimum value (most likely will buy stocks)
			elapsedValue = (actualPrice - stockTip)/stockTip;
		}
		
		noStocks = ((int)(5*elapsedValue*maxStocks));
		
		if(noStocks < 1) {
			if(maxStocks > 5) {
				noStocks = 5;
			} else noStocks = maxStocks;
		} else if(noStocks > maxStocks) {
			noStocks = ((int)(0.5*maxStocks));
		}

		return noStocks;
	}
	
	
	
	
	
	
}
