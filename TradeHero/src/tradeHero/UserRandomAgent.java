package tradeHero;

import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.domain.DFService;
import structures.randomCalc;
import tradeHero.behaviours.ReceiveStockUpdate;

public class UserRandomAgent extends UserAgent {

	public UserRandomAgent() {
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
		
		addBehaviour(new ReceiveStockUpdateAgent(this));
	}
	
	
	class ReceiveStockUpdateAgent extends ReceiveStockUpdate {
		
		ReceiveStockUpdateAgent(UserAgent agent){
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
				String stock = stocksPrice.get(i).getName();
			
				if(randomCalc.buyAction()) {
					System.out.println("I Will buy a stock");
					/* 1. Decidir quantas ações serão compradas  		*/	
					int noStocks = randomCalc.numberOfStocks((int)(cash/(stocksPrice.get(i).getValue())));	
					
					/* 2. Efetuar a compra 								*/		
					//////////////////////////////////////////////////////////////
					//////////////////////////////////////////////////////////////
					// TODO: Verificar se isto funciona!!!!
					System.out.println("Im " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I Will buy: " +  noStocks + " caches");
					
					buyStocks(stocksPrice.get(i).getName(), stocksPrice.get(i).value, noStocks);
					
					System.out.println("Im " +  myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash);
					
					/* 3. Avisar o mercado sobre o ganho atual 			*/
					s += "buy&" + stock + "&" + noStocks  + "\n";
					
					
					/* 4. Avisar aos followers que efetuou uma compra 	*/
				}
				
					
				if(((UserAgent)myAgent).getStocksOwned().containsKey(stock)) {
					boolean seller = stocksPrice.get(i).getValue() > stocksOwned.get(stock).getSavedValue() ? true : false;
								
					if(randomCalc.sellAction(seller)) {
						
						
						/* 1. Decidir quantas ações serão vendidas */
						
						int noStocks = randomCalc.numberOfStocks(stocksOwned.get(stock).getQuantity());
						System.out.println("Im " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I Will sell: " +  noStocks + " caches");
						
						/* 2. Efetuar a venda */
						sellStocks(stock, stocksPrice.get(i).getValue(), noStocks);
						
						System.out.println("Im " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash  + " ; SavedValue: " + stocksOwned.get(stock).getSavedValue());
						/* 3 Avisar o seguidores que efetuou uma venda */
						s += "sell&" + stock + "&" + noStocks  + "\n";
						
					}	
				}
				
				
			}
			if(!s.equals("")) {
				alertFollowers(s);
				
			}
			updateGain(gains(stocksPrice));
			
			
			
		}
		
		
		
		}
		
	
}
