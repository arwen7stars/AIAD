package tradeHero.behaviours;

import java.util.Map;

import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.domain.DFService;
import structures.Stock;
import structures.randomCalc;
import tradeHero.StockAgent;

public class StockInformationServer extends CyclicBehaviour {
	
	private Map<String, Stock> stockHistory;
	private String name;
	
	public StockInformationServer(Map<String, Stock> stk, String name) {
		super();
		stockHistory = stk;
		this.name = name;
	}
	
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("stoke-value"));
				
					
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null ) {
			
			/* 1. Mensagem enviada ao mercado avisando sobre o valor da ação consoante a data recebida */
			String date = msg.getContent();
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			Stock st = stockHistory.get(date); 

			if (st != null) {
				
				reply.setContent(name +  "&" + st.getValue());
				System.out.println("Stoke-agent "+ myAgent.getAID().getName()+": sent: " + reply.toString());
			}
			else {
				// The requested book has been sold to another buyer in the meanwhile .
				
				reply.setContent("error");
			}
			
			myAgent.send(reply);
			
			
			
			/* 2. Enviar dicas aos bons utilizadores							 */
			DFAgentDescription template = new DFAgentDescription();
						
			ServiceDescription sd = new ServiceDescription();
			sd.setType("goodbuyers");
			template.addServices(sd);
			
			
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				AID[] goodAgents = new AID[result.length];
				
				for (int i = 0; i < result.length; ++i) {
					goodAgents[i] = result[i].getName();
				}
				
				goodAgents = randomCalc.getRandomGoodAgents(goodAgents);
				
				ACLMessage tips = new ACLMessage(ACLMessage.INFORM);
				for(int i = 0; i <  goodAgents.length; i++) {
					tips.addReceiver( goodAgents[i]);
					
				}
				tips.setContent(getDica());
				tips.setConversationId("stoke-tip");
				myAgent.send(tips);
				
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			
			/* END 2*/
							
			
			
		}
		else {
			block();
		}
	}

	private String getDica() {
		// TODO Auto-generated method stub
		/* Construção da dica, se atraves de maximos e minimos locais: goog&maximo&17-Nov-16*/
		return "Toma la esta dica\n";
	}
}