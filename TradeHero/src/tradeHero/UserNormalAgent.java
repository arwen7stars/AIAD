package tradeHero;

import java.util.ArrayList;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.TickerBehaviour;
import sajas.core.behaviours.WakerBehaviour;
import sajas.domain.DFService;
import structures.Stock;
import structures.randomCalc;
import tradeHero.behaviours.FollowingServer;
import tradeHero.behaviours.InicializeFollowing;
import tradeHero.behaviours.Payments;
import tradeHero.behaviours.ReceivePayments;
import tradeHero.behaviours.ReceiveStockUpdate;

public class UserNormalAgent extends UserAgent {
	
	private ArrayList<String> following = new ArrayList<String>();
	private UserNormalAgent This = this;
	
	
	public UserNormalAgent() {
		super();
	}
	@Override
	public void setup() {
		
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("buyers");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour( new WakerBehaviour(this, 15000) {

			private static final long serialVersionUID = 1L;

			protected void handleElapsedTimeout() {
				/* Common Behaviours to all users */		
				addBehaviour(new ReceiveStockUpdateAgent(This));
				addBehaviour(new FollowingServer(This));
				addBehaviour(new ReceivePayments(This));
				
				/* Normal Agent additional Behaviours */
				addBehaviour(new InicializeFollowing(This));
				addBehaviour(new TickerBehaviour(This, randomCalc.PAYMENT_PERIOD*1000) {
					
					private static final long serialVersionUID = 1L;

					protected void onTick() {
						addBehaviour(new Payments(This));
					}
				});
				addBehaviour(new ReceiveStockUpdateAgent(This));
				addBehaviour(new FollowingTips(This));
			}
			
		}); 
		
	}
	
	public ArrayList<String> getFollowing() {
		return following;
	}
	
	/* Receives tips from agents that are being followed */
	/* Left to do: the user decides whether should, or not, follow   */
	class FollowingTips extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		
		private UserNormalAgent normalAgent;
		
		public FollowingTips(UserNormalAgent usa) {
			super();
			this.normalAgent = usa;
		}
		
		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("follow-tip"));
			ACLMessage msg = myAgent.receive(mt);
			
			if(msg != null) {				
				
				String s = "";
				
				String actions[] = msg.getContent().split("\n");
				System.out.println("[TIP] I " + myAgent.getLocalName() + " received a Tip with the following content: " + s);
				for(int i = 0; i < actions.length; i++) {
										
					
					String parts[] = actions[i].split("&");
					
					double rand = Math.random();
					if(rand < normalAgent.getRocValue(parts[1]) || rand < 0.4) {
						System.out.println("[TIP] I " + myAgent.getLocalName() + " will follow a tip: " + actions[i]);
						String action = parts[0];
						String stockName = parts[1];
						int total = Integer.parseInt(parts[2]);
						double price = Double.parseDouble(parts[3]);
						
						if(action.equals("sell")) {
							normalAgent.sellStocks(stockName, price, (int)(Math.random()*total));
							
						}else if(action.equals("buy")) {
							normalAgent.buyStocks(stockName, price, (int)(Math.random()*total));
						}
						
						
					}
				
				}
				
				updateGain(gains(UserAgent.stocksPrice));
				
				
			}else {
				block();
			}
			
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
		 
	}
	
	
	
	class ReceiveStockUpdateAgent extends ReceiveStockUpdate {
		
		private static final long serialVersionUID = 1L;

		protected ReceiveStockUpdateAgent(UserAgent agent) {
			super(agent);
		}

		@Override
		public void buyerAction() {
			/* Implementação do comportamento do agente após receber a informação sobre o valor das ações 
			 * 		Atributos:	
			 * 			today: 						String
			 * 			stoksPrice<Stock> :	 		ArrayList<Stock> com nome da stock e valor
			*/	
			String s = "";
			
			if(gains(stocksPrice) < randomCalc.MINIMUM_POSSIBLE_GAIN)
				myAgent.doDelete();
			
			for(int i = 0; i < stocksPrice.size(); i++) {
				Stock stock = stocksPrice.get(i);
				
				s += getProb(stock);
				
				if(!s.equals("")) {
					alertFollowers(s);
					
				}
				updateGain(gains(stocksPrice));
			}
		}
		
	}
		
}
