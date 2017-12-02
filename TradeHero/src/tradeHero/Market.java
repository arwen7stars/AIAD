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
import tradeHero.behaviours.UpdateStokes;

public class Market extends Agent {
	
	private AID[] stokeAgents;
	private AID[] userAgents;

	public static final String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public static final int monthsTotal[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	public static int this_day = 3;
	public static int this_month = 11;
	public static int this_year = 16;
	public static Market This = null;
	
	Market() throws Exception { 
		super();
		if(This != null)
			throw new Exception("Only one market can be created");
		 
		This = this;
	}
	
	protected void setup() {
		System.out.println("Hallo! Market-agent "+getAID().getName()+" is ready.");
		
		addBehaviour(new TickerBehaviour(this, 10*1000) {
			protected void onTick() {
				 System.out.println("-----------------------------------------------------------------------------------------------------------");
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
					//System.out.println("Market-agent "+getAID().getName()+":"+"Found the following user agents:");
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
				myAgent.addBehaviour(new UpdateStokes(This));
			}
		} );

	}
	
	// Put agent clean-up operations here
	protected void takeDown() {
	// Printout a dismissal message
		System.out.println("Market-agent "+getAID().getName()+" terminating.");
	}
	
	
		
	
	public static String today() {
				
		return "" + this_day +"-" + months[this_month - 1] + "-" + this_year;
	}
	
	public static String nextDay() {
				
		this_day++;
		 
		if(((this_day - 1) % monthsTotal[this_month - 1]) == 0) {
			if(this_month == 12) {
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

	public AID[] getStokeAgents() {
		// TODO Auto-generated method stub
		return this.stokeAgents;
	}

	public AID[] getUserAgents() {
		// TODO Auto-generated method stub
		return this.userAgents;
	}

	
	

}
