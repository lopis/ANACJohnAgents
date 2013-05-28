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
public class JohnDoeAgent extends Agent
{
	private Action actionOfPartner=null;
	/** Note: {@link JohnDoeAgent} does not account for the discount factor in its computations */ 
	private static double MINIMUM_BID_UTILITY = 0.0;

	/**
	 * init is called when a next session starts with the same opponent.
	 */
	public void init()
	{
		MINIMUM_BID_UTILITY = utilitySpace.getReservationValueUndiscounted();
	}

	public static String getVersion() { return "3.1"; }
	
	@Override
	public String getName()
	{
		return "John Doe Agent";
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
				double offeredUtilFromOpponent = utilitySpace.getUtilityWithDiscount(partnerBid, time);
				// get current time
				action = chooseBidAction(time);
				
				Bid myBid = ((Offer) action).getBid();
				double myOfferedUtil = utilitySpace.getUtilityWithDiscount(myBid, time);
				
				// accept under certain circumstances
				if (isAcceptable(offeredUtilFromOpponent, myOfferedUtil, time))
					action = new Accept(getAgentID());
				
			}
			sleep(0.005); // just for fun
		} catch (Exception e) { 
			System.out.println("Exception in ChooseAction:"+e.getMessage());
			action=new Accept(getAgentID()); // best guess if things go wrong. 
		}
		return action;
	}

	private boolean isAcceptable(double offeredUtilFromOpponent, double myOfferedUtil, double time) throws Exception
	{
		
		/*double P = Paccept(offeredUtilFromOpponent,time);
		if (P > Math.random())
			return true;*/		
		return false;
	}

	/**
	 * Wrapper for getRandomBid, for convenience.
	 * @return new Action(Bid(..)), with bid utility > MINIMUM_BID_UTIL.
	 * If a problem occurs, it returns an Accept() action.
	 */
	private Action chooseBidAction(double time) 
	{
		Bid nextBid=null ;
		try { nextBid = getBid(time); }
		catch (Exception e) { System.out.println("Problem with received bid:"+e.getMessage()+". cancelling bidding"); }
		if (nextBid == null) return (new Accept(getAgentID()));                
		return (new Offer(getAgentID(), nextBid));
	}

	/**
	 * @return a random bid with high enough utility value.
	 * @throws Exception if we can't compute the utility (eg no evaluators have been set)
	 * or when other evaluators than a DiscreteEvaluator are present in the util space.
	 */
	private Bid getBid(double time) throws Exception
	{
		ArrayList<Issue> issues=utilitySpace.getDomain().getIssues();
		//Random randomnr= new Random();
		// create a random bid with utility>MINIMUM_BID_UTIL.
		// note that this may never succeed if you set MINIMUM too high!!!
		// in that case we will search for a bid till the time is up (3 minutes)
		// but this is just a simple agent.
		Bid bid=null;
		//do 
		//{
			HashMap<Integer, ArrayList<Value>> values = new HashMap<Integer,ArrayList<Value>>(); // pairs <issuenumber,chosen value string>
			for(Issue lIssue:issues) 
			{
				switch(lIssue.getType()) {
				case DISCRETE:
					IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
					System.out.println("Number of Outcomes on Issue "+lIssueDiscrete.getNumber()+": "+lIssueDiscrete.getNumberOfValues());
					//int optionIndex=randomnr.nextInt(lIssueDiscrete.getNumberOfValues());
					ArrayList<Value> vals = new ArrayList<Value>();
					for (int i = 0; i < lIssueDiscrete.getNumberOfValues(); i++) {
						System.out.println("Discrete: "+lIssue.getNumber()+" And this is :"+lIssueDiscrete.getValue(i));
						vals.add(lIssueDiscrete.getValue(i));
						//bid=new Bid(utilitySpace.getDomain(),values);
						//bids.add(bid);
					}
					System.out.println("Issue saved "+lIssueDiscrete.getNumber());
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
				default: throw new Exception("issue type "+lIssue.getType()+" not supported by SimpleAgent2");
				}
			}
			System.out.println("bidsmuitantes");
			ArrayList<HashMap<Integer, Value>> bidList = new ArrayList<HashMap<Integer, Value>>();
			for (int i = 0; i < utilitySpace.getDomain().getNumberOfPossibleBids(); i++) {
				HashMap<Integer, Value> newbid = new HashMap<Integer, Value>();
				System.out.println("Values Size:"+values.size());
				for (Integer issueID : values.keySet()) {
					newbid.put(issueID,values.get(issueID).get(0));
					System.out.println(issueID+":"+values.get(issueID).get(0));
				}
				bidList.add(newbid);
			}
			System.out.println("bidsantes");
			int counter=0;	
			for (Integer issueID : values.keySet()) {
				for (int j = 0; j < values.get(issueID).size(); j++) {
					bidList.get(counter).put(issueID,values.get(issueID).get(j));
					counter++;
				}
			}
			System.out.println("bids");
			for (int i = 0; i < bidList.size(); i++) {
				System.out.println("Bid number "+i);
				for (Integer issueID : values.keySet()) {
					System.out.println("\t Issue "+issueID+":"+bidList.get(i).get(issueID));
				}
			}
			
			ArrayList<ComparableBid> bids = new ArrayList<ComparableBid>();
			for (int i = 0; i < bidList.size(); i++) {
				bid=new Bid(utilitySpace.getDomain(),bidList.get(i));
				bids.add(new ComparableBid(bid));
			}
			Collections.sort(bids);
			
		//} while (utilitySpace.getUtilityWithDiscount(bid, time) < MINIMUM_BID_UTILITY);

		return bids.get(0).bid;
	}
	
	private class ComparableBid implements Comparable<ComparableBid>{
		public Bid bid;

		public ComparableBid(Bid bid2) {
			this.bid=bid2;
		}

		@Override
		public int compareTo(ComparableBid b) {
			double time = timeline.getTime();
			return (int) (utilitySpace.getUtilityWithDiscount(b.bid, time) - utilitySpace.getUtilityWithDiscount(this.bid, time))*10;
		}
		
	}

	/**
	 * This function determines the accept probability for an offer.
	 * At t=0 it will prefer high-utility offers.
	 * As t gets closer to 1, it will accept lower utility offers with increasing probability.
	 * it will never accept offers with utility 0.
	 * @param u is the utility 
	 * @param t is the time as fraction of the total available time 
	 * (t=0 at start, and t=1 at end time)
	 * @return the probability of an accept at time t
	 * @throws Exception if you use wrong values for u or t.
	 * 
	 */
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
