package tradeHero;

import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.domain.DFService;
import tradeHero.behaviours.ReceiveStockUpdate;

public class UserNormalAgent extends UserAgent {

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
		
		addBehaviour(new ReceiveStockUpdateAgent());

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
