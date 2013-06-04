package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;




import negotiator.Agent;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;


/**
 * @author W.Pasman
 * Some improvements over the standard SimpleAgent.
 * 
 * Random Walker, Zero Intelligence Agent
 */
public class JohnGreedyAgent extends Agent
{
	private Action actionOfPartner=null;
	/** Note: {@link JohnDoeAgent} does not account for the discount factor in its computations */ 
	private static double MINIMUM_BID_UTILITY = 0.0;

	private ArrayList<ComparableBid> bids = new ArrayList<ComparableBid>();
	
	private double expectedUtility=1;
	
	private double counter=1.0;
	
	private int prevpos=0;
	
	
	private Bid bestOpBid= new Bid();
	
	private int numOfOpBids=0;
	
	private int numOfOpDifBids=0;
	

	public void init()
	{
		MINIMUM_BID_UTILITY = utilitySpace.getReservationValueUndiscounted();
		ArrayList<Issue> issues=utilitySpace.getDomain().getIssues();
		//Random randomnr= new Random();
		// create a random bid with utility>MINIMUM_BID_UTIL.
		// note that this may never succeed if you set MINIMUM too high!!!
		// in that case we will search for a bid till the time is up (3 minutes)
		// but this is just a simple agent.
		Bid bid=null;
		//do 
		//{
		HashMap<Integer,Integer> counterbids= new HashMap<Integer,Integer>();	
		HashMap<Integer,Integer> max= new HashMap<Integer,Integer>();
			HashMap<Integer, ArrayList<Value>> values = new HashMap<Integer,ArrayList<Value>>(); // pairs <issuenumber,chosen value string>
			long possibilities = utilitySpace.getDomain().getNumberOfPossibleBids();
			int maxvalue = 0;
			if(possibilities>50000)
				{
					possibilities=50000;
					for (; (Math.pow(maxvalue, utilitySpace.getDomain().getIssues().size()))<50000; maxvalue++) {
						
					}
				}
			for(Issue lIssue:issues) 
			{
				switch(lIssue.getType()) {
				case DISCRETE:
					IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
					counterbids.put(lIssueDiscrete.getNumber(), 0);
					//System.out.println("Number of Outcomes on Issue "+lIssueDiscrete.getNumber()+": "+lIssueDiscrete.getNumberOfValues());
					//int optionIndex=randomnr.nextInt(lIssueDiscrete.getNumberOfValues());
					ArrayList<Value> vals = new ArrayList<Value>();
					for (int i = 0; i < lIssueDiscrete.getNumberOfValues(); i++) {
						if(possibilities>50000 && i>=maxvalue)
							break;
						//System.out.println("Discrete: "+lIssue.getNumber()+" And this is :"+lIssueDiscrete.getValue(i));
						vals.add(lIssueDiscrete.getValue(i));
						//bid=new Bid(utilitySpace.getDomain(),values);
						//bids.add(bid);
					}
					max.put(lIssueDiscrete.getNumber(), vals.size());
					//System.out.println("Issue saved "+lIssueDiscrete.getNumber());
					values.put(lIssueDiscrete.getNumber(), vals);
					break;
				/*case REAL:
					IssueReal lIssueReal = (IssueReal)lIssue;
					int optionInd = randomnr.nextInt(lIssueReal.getNumberOfDiscretizationSteps()-1);
					System.out.println("Real: "+lIssueReal.getNumber()+" And this is "+new ValueReal(lIssueReal.getLowerBound() + (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())*(double)(optionInd)/(double)(lIssueReal.getNumberOfDiscretizationSteps())));
					values.put(lIssueReal.getNumber(), new ValueReal(lIssueReal.getLowerBound() + (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())*(double)(optionInd)/(double)(lIssueReal.getNumberOfDiscretizationSteps())));
					break;
				case INTEGER:
					IssueInteger lIssueInteger = (IssueInteger)lIssue;
					int optionIndex2 = lIssueInteger.getLowerBound() + randomnr.nextInt(lIssueInteger.getUpperBound()-lIssueInteger.getLowerBound());
					System.out.println("Integer: "+lIssueInteger.getNumber()+" And This is :"+new ValueInteger(optionIndex2));
					values.put(lIssueInteger.getNumber(), new ValueInteger(optionIndex2));
					break;*/
				default: try {
						throw new Exception("issue type "+lIssue.getType()+" not supported by SimpleAgent2");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//System.out.println("bidsmuitantes");
			ArrayList<HashMap<Integer, Value>> bidList = new ArrayList<HashMap<Integer, Value>>();
			for (int i = 0; i < possibilities; i++) {
				HashMap<Integer, Value> newbid = new HashMap<Integer, Value>();
				//System.out.println("Values Size:"+values.size());
				for (Integer issueID : values.keySet()) {
					newbid.put(issueID,values.get(issueID).get(0));
					//System.out.println(issueID+":"+values.get(issueID).get(0));
				}
				bidList.add(newbid);
			}
			//System.out.println("Test");
			ArrayList<Integer> hashvalue= new ArrayList<Integer>();
			for (Integer issueID : values.keySet()) {
				hashvalue.add(issueID);
			}
			//System.out.println("Test2");
			for (int i = 0; i < possibilities; i++) {
				//System.out.println("For bid "+i+" Sequence:");
				for (Integer issueID : values.keySet()) {
					//System.out.println("\t Issue "+issueID+" : "+values.get(issueID).get(counter.get(issueID)));
					bidList.get(i).put(issueID,values.get(issueID).get(counterbids.get(issueID)));
				}
				counterbids.put(hashvalue.get(0), (counterbids.get(hashvalue.get(0))+1));
				for (int j = 0; j < hashvalue.size(); j++) 
				{
					if(counterbids.get(hashvalue.get(j))>=max.get(hashvalue.get(j)))
					{
						if(j==(hashvalue.size()-1))
						{}
						else
						{
							counterbids.put(hashvalue.get(j), 0);
							int inc = j+1;
							counterbids.put(hashvalue.get(inc), (counterbids.get(hashvalue.get(inc))+1));
						}
					}
				}
			
			}
			/*System.out.println("bids");
			for (int i = 0; i < bidList.size(); i++) {
				System.out.println("Bid number "+i);
				for (Integer issueID : values.keySet()) {
					System.out.println("\t Issue "+issueID+":"+bidList.get(i).get(issueID));
				}
			}*/
			for (int i = 0; i < bidList.size(); i++) {
				try {
					bid=new Bid(utilitySpace.getDomain(),bidList.get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bids.add(new ComparableBid(bid));
			}
		//} while (utilitySpace.getUtilityWithDiscount(bid, time) < MINIMUM_BID_UTILITY);
			
			Collections.sort(bids);
	}

	public static String getVersion() { return "3.1"; }
	
	@Override
	public String getName()
	{
		return "John Greedy Agent";
	}

	public void ReceiveMessage(Action opponentAction) 
	{
		actionOfPartner = opponentAction;
	}


	public Action chooseAction()
	{
		Action action = null;
		try 
		{ 
			double time = timeline.getTime();
			if(actionOfPartner==null) {
				action = chooseBidAction(time);
			}
			else if(actionOfPartner instanceof Offer)
			{
				Bid partnerBid = ((Offer)actionOfPartner).getBid();
				if(numOfOpBids==0)
					 {
						bestOpBid=partnerBid;
						numOfOpDifBids++;
					}
				else if(utilitySpace.getUtilityWithDiscount(partnerBid, time)>utilitySpace.getUtilityWithDiscount(bestOpBid, time))
					{
						bestOpBid=partnerBid;
						numOfOpDifBids++;
					}
				numOfOpBids++;
				double offeredUtilFromOpponent = utilitySpace.getUtilityWithDiscount(partnerBid, time);
				// get current time
				action = chooseBidAction(time);
				
				Bid myBid = ((Offer) action).getBid();
				double myOfferedUtil = utilitySpace.getUtilityWithDiscount(myBid, time);
				
				// accept under certain circumstances
				if (isAcceptable(offeredUtilFromOpponent, myOfferedUtil, time))
					action = new Accept(getAgentID());
				
			}
		} catch (Exception e) { 
			System.out.println("Exception in ChooseAction:"+e.getMessage());
			action=new Accept(getAgentID()); // best guess if things go wrong. 
		}
		return action;
	}

	private boolean isAcceptable(double offeredUtilFromOpponent, double myOfferedUtil, double time) throws Exception
	{
		if(offeredUtilFromOpponent>=myOfferedUtil)
			{
			System.out.println("Accepting cause His Offer is Better Than Mine!");
				return true;
			}
		if((1-time) <0.005)
			{
			 int pos = (int) Math.ceil(0.75*bids.size()-1);
			 if(offeredUtilFromOpponent>=utilitySpace.getUtilityWithDiscount(bids.get(pos).bid, time))
			 	{
				 System.out.println("Accepting cause Time is Running Out!"); 
				 return true;
			 	}
			}
		//expectedUtility = Paccept(myOfferedUtil,time);
		double P = Paccept(offeredUtilFromOpponent,time);
		int pos = (int) Math.ceil(0.9*bids.size()-1);
		
		if (P == 1)
			{
				System.out.println("Accepting cause P is "+P+":"+offeredUtilFromOpponent+"And expectedUtility is "+expectedUtility+":"+myOfferedUtil);
				return true;		
			}
		return false;
	}


	private Action chooseBidAction(double time) 
	{
		Bid nextBid=null ;
		try { nextBid = getBid(); }
		catch (Exception e) { System.out.println("Problem with received bid:"+e.getMessage()+". cancelling bidding"); }
		if (nextBid == null) return (new Accept(getAgentID()));                
		return (new Offer(getAgentID(), nextBid));
	}


	private Bid getBid()
	{
		double time = timeline.getTime();
		if(1-time <0.005)
			{
			 int pos = (int) Math.ceil(0.75*bids.size()-1);
			 if(utilitySpace.getUtilityWithDiscount(bestOpBid, time)>=utilitySpace.getUtilityWithDiscount(bids.get(pos).bid, time))
			 		{
				 		System.out.println("Accepting cause Time is Running Out!");
				 		return bestOpBid;
			 		}
			}
		
		if((1-time) <0.1 )
		{
			if(counter<0.9)
				counter=1;
			else
			{
				counter=0.9+(1.0-time);
			}
			int pos = (int) Math.ceil(counter*bids.size()-1);
			if(pos==prevpos)
			{
				if(pos>(int)bids.size()/2)
					pos=prevpos-1;
					prevpos=pos;
			}
			return bids.get(pos).bid;
		}
		
		else{
			counter--;
			if(counter<=0)
				counter=bids.size()-1;
			if(counter<(Math.ceil(0.9*bids.size()-1)))
				counter=bids.size()-1;
			return bids.get( (int)counter).bid;
		}
		
	}
	
	private class ComparableBid implements Comparable<ComparableBid>{
		public Bid bid;

		public ComparableBid(Bid bid2) {
			this.bid=bid2;
		}

		@Override
		public int compareTo(ComparableBid b) {
			double time = timeline.getTime();
			return (int)(utilitySpace.getUtilityWithDiscount(this.bid, time)*1000) - (int)(utilitySpace.getUtilityWithDiscount(b.bid, time)*1000);
		}
		
	}


	double Paccept(double u, double t1) throws Exception
	{
		double t=t1*t1*t1; // steeper increase when deadline approaches.
		if (u<0 || u>1.05) throw new Exception("utility "+u+" outside [0,1]");
		// normalization may be slightly off, therefore we have a broad boundary up to 1.05
		if (t<0 || t>1) throw new Exception("time "+t+" outside [0,1]");
		if (u>1.) u=1;
		if (t==0.5) return u;
		return (u - 2.*u*t + 2.*(-1. + t + Math.sqrt(sq(-1. + t) + u*(-1. + 2*t))))/(-1. + 2*t);
	}

	double sq(double x) { return x*x; }
}

