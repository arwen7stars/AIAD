/* Simple implementation of Momentum Trading: "Evaluation of News-Based Trading Strategies" from Stefan Feuerriegel*/


package tradeHero.behaviours;

import java.util.Map;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import structures.Stock;
import structures.randomCalc;
import tradeHero.StockAgent;


public class stockROC extends CyclicBehaviour{
	
	private static final long serialVersionUID = 1L;
	
	private StockAgent stk;
	
	public stockROC(StockAgent stk) {
		super();
		this.stk = stk;
	}
	
	
	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("ROC"));
		ACLMessage msg = myAgent.receive(mt);
		
		if(msg != null) {				
			
			String date = msg.getContent();
			
			String yesterday = randomCalc.yesterday(date);
			String lastMonthDay = randomCalc.lastMonth(date);
			
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			reply.setConversationId("ROC");
			
			Map<String, Stock> stockHistory = stk.getStockHistory();
			System.out.println("[ROC COMPUTATION] Today: " + date + " ; Yesterday: " + yesterday + " ; lastMonthDay: " + lastMonthDay);
			
			
			double ROC = (stockHistory.get(yesterday).getValue() - stockHistory.get(lastMonthDay).getValue())/(stockHistory.get(lastMonthDay).getValue());
			reply.setContent("" + ROC);
			
			myAgent.send(reply);				
			
		}else {
			block();
		}
	}
	
}