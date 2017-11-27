package tradeHero;

import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.WakerBehaviour;
import sajas.core.behaviours.WrapperBehaviour;
import sajas.domain.DFService;
import sajas.proto.ContractNetInitiator;
import sajas.proto.SubscriptionInitiator;

public class StockAgent extends Agent {
	private ArrayList<Stock> stockHistory = new ArrayList<Stock>();		// mapa de historicos de acoes de empresas extraidos de google finance
	private ArrayList<AID> goodUsers = new ArrayList<AID>();
	private ArrayList<AID> normalUsers = new ArrayList<AID>();
	private ActualStock actualStockValue;
	
	private Context<?> context;
	private Network<Object> net;
	private RepastEdge<Object> edge = null;
	
	protected ACLMessage myCfp;
	
	class ActualStock
	{
		public int index;
		public Stock stock;
		
		public ActualStock(int index, Stock stock) {
			this.index = index;
			this.stock = stock;
		}		
	}
	
	class Stock
	{
	    public int day;
	    public int month;
	    public int year;
	    public double value;
	    
	    public Stock(String stockDate, String open, String close) {
	    	this.parseDate(stockDate);
	    	
	    	double openInt = Double.parseDouble(open);
	    	double closeInt = Double.parseDouble(close);
	    	
	    	this.value = (openInt + closeInt) / 2;
	    }
	    
	    public void parseDate(String stockDate) {
	    	String[] parts = stockDate.split("-");
	    	this.day = Integer.parseInt(parts[0]);	        
	    	this.year = Integer.parseInt(parts[2]);
	    	
	        Date date = null;
			try {
				date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(parts[1]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        this.month = cal.get(Calendar.MONTH) + 1;
	    }
	 };
	
	public StockAgent() {}
	
	@Override
	public void setup() {
		
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("trade-hero");
		template.addServices(sd);
		addBehaviour(new DFSubscInit(this, template));
		
		// prepare cfp message
		myCfp = new ACLMessage(ACLMessage.CFP);
		myCfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		
		// waker behaviour for starting CNets
		addBehaviour(new StartCNets(this, 2000));
	}
	
	protected void addUser(AID user) {
		String normalUser = "NormalUser";
		String goodUser = "GoodUser";
		
		String aid = user.toString();
		
		if (aid.toLowerCase().contains(goodUser.toLowerCase())){
			goodUsers.add(user);
		} else if (aid.toLowerCase().contains(normalUser.toLowerCase())){
			normalUsers.add(user);
		}
	}
	
	public void readHistory(String csvFile) {
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Stock> stockHistTmp = new ArrayList<Stock>();
		
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	br.readLine();
            while ((line = br.readLine()) != null) {

                String[] stockValue = line.split(cvsSplitBy);
                String date = stockValue[0];
                String open = stockValue[1];
                String close = stockValue[4];
                
                Stock st = new Stock(date, open, close);
                stockHistTmp.add(st);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(stockHistTmp);
        stockHistory = stockHistTmp;
       
        ActualStock st = new ActualStock(0, stockHistory.get(0));
        actualStockValue = st;
	}

	
	private class DFSubscInit extends SubscriptionInitiator {	
		private static final long serialVersionUID = 1L;

		DFSubscInit(Agent agent, DFAgentDescription dfad) {
			super(agent, DFService.createSubscriptionMessage(agent, getDefaultDF(), dfad, null));
		}
		
		protected void handleInform(ACLMessage inform) {
			try {
				DFAgentDescription[] dfads = DFService.decodeNotification(inform.getContent());
				for(int i = 0; i < dfads.length; i++) {
					AID agent = dfads[i].getName();
					((StockAgent) myAgent).addUser(agent);
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}

		}
	}
	
	private class StartCNets extends WakerBehaviour {
		private static final long serialVersionUID = 1L;

		public StartCNets(Agent a, long timeout) {
			super(a, timeout);
		}
		
		@Override
		public void onWake() {
			// context and network (RepastS)
			context = ContextUtils.getContext(myAgent);
			net = (Network<Object>) context.getProjection("TradeHero network");
			
			// initiate CNet protocol
			CNetInit cNetInit = new CNetInit(myAgent, (ACLMessage) myCfp.clone());
			addBehaviour(new CNetInitWrapper(cNetInit));
		}

	}
	
	private class CNetInitWrapper extends WrapperBehaviour {
		private static final long serialVersionUID = 1L;

		public CNetInitWrapper(Behaviour wrapped) {
			super(wrapped);
		}		
	}
	
	private class CNetInit extends ContractNetInitiator {
		private static final long serialVersionUID = 1L;

		public CNetInit(Agent owner, ACLMessage cfp) {
			super(owner, cfp);
		}

		@Override
		public Vector prepareCfps(ACLMessage cfp) {
		 				    
			for(AID gUser : goodUsers) {	
				cfp.addReceiver(gUser);
				cfp.setContent("I sell seashells at $10/kg");
			}
			for(AID nUser : normalUsers) {	
				cfp.addReceiver(nUser);
				cfp.setContent("I sell seashells at $10/kg");
			}
			
			send(cfp);
			
			return null;
		}
	}

	public ArrayList<Stock> getStockHistory() {
		return stockHistory;
	}

	public void setStockHistory(ArrayList<Stock> stockHistory) {
		this.stockHistory = stockHistory;
	}

	public ActualStock getActualStockValue() {
		return actualStockValue;
	}

	public void setActualStockValue(ActualStock actualStockValue) {
		this.actualStockValue = actualStockValue;
	}
	
	public ActualStock updateStockValue() {
		/*String actualDay = String.valueOf(actualStockValue.day);
		String actualMonth = String.valueOf(actualStockValue.month);
		String actualYear = String.valueOf(actualStockValue.year);
		
		String actualDate = actualDay + "-" + actualMonth + "-" + actualYear;
		
		String dt = actualDate;					// start date
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
		Calendar c = Calendar.getInstance();
		
		try {
			c.setTime(sdf.parse(dt));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
		c.add(Calendar.DATE, 1);  				// number of days to add
		dt = sdf.format(c.getTime());  			// dt is now the new date
		
    	String[] parts = dt.split("-");
    	int day = Integer.parseInt(parts[0]);
    	int month = Integer.parseInt(parts[1]);
    	int year = Integer.parseInt(parts[2]);*/
		
		if(actualStockValue.index+1 < stockHistory.size()) {
			Stock st = stockHistory.get(actualStockValue.index+1);
			actualStockValue = new ActualStock(actualStockValue.index+1, st);
			
			return actualStockValue;
		} else return null;
	}
}