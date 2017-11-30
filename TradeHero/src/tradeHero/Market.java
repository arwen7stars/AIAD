package tradeHero;

import java.util.ArrayList;

import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.TickerBehaviour;
import sajas.domain.DFService;

public class Market extends Agent {
	
	private AID[] stokeAgents;
	private AID[] userAgents;

	public static final String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public static final int monthsTotal[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	public static int this_day = 3;
	public static int this_month = 11;
	public static int this_year = 16;
	
	
	Market() { super(); }
	
	protected void setup() {
		System.out.println("Hallo! Market-agent "+getAID().getName()+" is ready.");
		
		addBehaviour(new TickerBehaviour(this, 10*1000) {
			protected void onTick() {
				 
				// Update the list of seller agents
				DFAgentDescription template = new DFAgentDescription();
				DFAgentDescription template2 = new DFAgentDescription();
				
				ServiceDescription sd = new ServiceDescription();
				sd.setType("stock");
				template.addServices(sd);
				
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType("buyers");
				template2.addServices(sd2);
				
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					//System.out.println("Market-agent "+getAID().getName()+":"+"Found the following stoke agents:");
					stokeAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						stokeAgents[i] = result[i].getName();
						//System.out.println(stokeAgents[i].getName());
					}
					
					result = DFService.search(myAgent, template2);
					//System.out.println("Market-agent "+getAID().getName()+":"+"Found the following stoke agents:");
					userAgents = new AID[result.length];
					for(int i = 0; i < result.length; ++i) {
						userAgents[i] = result[i].getName();
						//System.out.println(userAgents[i].getName());
					}
					
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}

				// Perform the request
				myAgent.addBehaviour(new updateStokes());
			}
		} );

	}
	
	// Put agent clean-up operations here
	protected void takeDown() {
	// Printout a dismissal message
		System.out.println("Market-agent "+getAID().getName()+" terminating.");
	}
	
	public class updateStokes extends Behaviour {
		private int step = 0;
		private MessageTemplate mt;
		private int repliesCnt = 0;
		private ArrayList<String> stokeValues = new ArrayList<String>();
		
		@Override
		public void action() {
			
			
			
			// TODO Auto-generated method stub
			switch(step) {
			case 0:
				
				System.out.println("Im here!");
				
				// Send a request to all stokes
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
				
				for(int i = 0; i < stokeAgents.length; i++) {
					request.addReceiver(stokeAgents[i]);
					
				}
				System.out.println("today: " + today());
				request.setContent(today());
				request.setConversationId("stoke-value");
				request.setReplyWith("request" + System.currentTimeMillis());
				myAgent.send(request);
				
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("stoke-value"),MessageTemplate.MatchInReplyTo(request.getReplyWith()));
				
				step = 1;
				
				System.out.println("I sent the message to: " + stokeAgents.length);
				
				break;			
			
			
			case 1:
				
				System.out.println("Im Still here!");
				
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
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			if(step == 2) {
				return false;
			}
			nextDay();
			return true;
		}
		
	}
		
	
	public static String today() {
				
		return "" + this_day +"-" + months[this_month - 1] + "-" + this_year;
	}
	
	public static String nextDay() {
				
		this_day++;
		System.out.println("wtf");
		if(((this_day - 1) % monthsTotal[this_month - 1]) == 0) {
			if(this_month == 11) {
				this_year++;
				this_day = 1;
				this_month = 1;
			}else {
				this_day = 1;
				this_month++;
			}			
		}		
		return today();
	}
	
	

}
