package tradeHero;

import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.domain.DFService;
import tradeHero.behaviours.ReceiveStockUpdate;
import tradeHero.behaviours.ReceiveTip;

public class UserGoodAgent extends UserAgent {
	
	public UserGoodAgent() {
		super();
	}
	
	@Override
	public void setup() {
		
		// Register user service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("buyers");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		
		DFAgentDescription dfd2 = new DFAgentDescription();
		dfd2.setName(getAID());
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("goodbuyers");
		sd2.setName(getLocalName());
		dfd2.addServices(sd2);
		
		
		try {
			DFService.register(this, dfd);
			DFService.register(this, dfd2);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new ReceiveStockUpdateAgent());
		addBehaviour(new ReceiveTip(this));
	}
	
	
	class ReceiveStockUpdateAgent extends ReceiveStockUpdate {

		@Override
		public void buyerAction() {
			/* Implementação do comportamento do agente após receber a informação sobre o valor das ações 
			 * 		Atributos:	
			 * 			today: 						String
			 * 			stoksPrice<Stock> :	 		ArrayList<Stock> com nome da stock e valor
			*/	
			
		}
		
		
		
		}
		
	
	

}
