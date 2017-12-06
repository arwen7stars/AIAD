package tradeHero.behaviours;

import java.util.ArrayList;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;
import structures.randomCalc;
import tradeHero.Market;

public class UpdateStokes extends Behaviour {
	
	private AID[] stokeAgents;
	private AID[] userAgents;
	
	private int step = 0;
	private MessageTemplate mt;
	private int repliesCnt = 0;
	private ArrayList<String> stokeValues = new ArrayList<String>();
	private Market This;
	
	public UpdateStokes(Market agent) {
		super();
		This = agent;
		stokeAgents = This.getStokeAgents();
		userAgents = This.getUserAgents();
	}
	
	
	@Override
	public void action() {	
		
		
		// TODO Auto-generated method stub
		switch(step) {
		case 0:
			
						
			// Send a request to all stokes
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			
			for(int i = 0; i <  stokeAgents.length; i++) {
				request.addReceiver( stokeAgents[i]);
				
			}
			System.out.println("today: " + randomCalc.today());
			request.setContent(randomCalc.today());
			request.setConversationId("stoke-value");
			request.setReplyWith("request" + System.currentTimeMillis());
			myAgent.send(request);
			
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("stoke-value"),MessageTemplate.MatchInReplyTo(request.getReplyWith()));
			
			step = 1;
			
			System.out.println("I sent the message to: " + stokeAgents.length);
			
			break;			
		
		
		case 1:
			
		 
			
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.INFORM) {
					String stokePrice = reply.getContent(); // stockName&value
					stokeValues.add(stokePrice);
					repliesCnt++;
					
					System.out.println("received: " + stokePrice);
					
					if(repliesCnt >= stokeAgents.length) {
						step = 2;
					}
					
				}
				
				
			}else {
				block();
			}
			
			break;
		case 2:
			String msg = "";
			msg += randomCalc.today() + "\n";
			ACLMessage informUsers = new ACLMessage(ACLMessage.INFORM);
			for(int i = 0; i < userAgents.length; i++) {
				informUsers.addReceiver(userAgents[i]);
			}
			
			for(int i = 0; i < stokeValues.size(); i++) {
				msg += stokeValues.get(i) + "\n";
			}
			
			informUsers.setContent(msg);
			informUsers.setConversationId("stoke-value");
			informUsers.setReplyWith("request" + System.currentTimeMillis());
			myAgent.send(informUsers);
			step = 3;
			break;
			
			
			
		}
		
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		if(step != 3) {
			
			return false;
		}
		randomCalc.nextDay();
		System.out.println("WTF");
		return true;
	}
	
}