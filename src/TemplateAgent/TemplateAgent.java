package TemplateAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import TemplateAgent.etc.bidSearch;
import TemplateAgent.etc.negotiationInfo;
import TemplateAgent.etc.negotiationStrategy;
import list.Tuple;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.persistent.PersistentDataContainer;
import negotiator.persistent.PersistentDataType;
import negotiator.persistent.StandardInfo;
import negotiator.persistent.StandardInfoList;
import negotiator.timeline.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class TemplateAgent extends AbstractNegotiationParty {
    private TimeLineInfo timelineInfo; // タイムライン
    private AbstractUtilitySpace utilitySpace;

    private Bid lastReceivedBid = null;
    private int nrChosenActions = 0; // number of times chosenAction was called.
    private StandardInfoList history;
    private negotiationStrategy mStrategy;
    private negotiationInfo mNegotiationInfo;
    private bidSearch mBidSerch;

    private boolean isPrinting = false;

    @Override
    public void init(AbstractUtilitySpace utilSpace, Deadline dl, TimeLineInfo tl, long randomSeed, AgentID agentId,
                     PersistentDataContainer data) {
        super.init(utilSpace, dl, tl, randomSeed, agentId, data);

        if (isPrinting) {
            System.out.println("*** Template Agent ***");
        }
        System.out.println("Discount Factor is " + utilSpace.getDiscountFactor());
        System.out.println("Reservation Value is " + utilSpace.getReservationValueUndiscounted());


        mNegotiationInfo = new negotiationInfo(utilitySpace, isPrinting);
        try {
            mBidSerch = new bidSearch(utilSpace, mNegotiationInfo, isPrinting);
        } catch (Exception e) {
            throw new RuntimeException("init failed: " + e);
        }

        // need standard data
        if (getData().getPersistentDataType() != PersistentDataType.STANDARD) {
            throw new IllegalStateException("need standard persistent data");
        }
        history = (StandardInfoList) getData().get();

        if (!history.isEmpty()) {
            // example of using the history. Compute for each party the maximum
            // utility of the bids in last session.
            Map<String, Double> maxutils = new HashMap<String, Double>();
            StandardInfo lastinfo = history.get(history.size() - 1);
            for (Tuple<String, Double> offered : lastinfo.getUtilities()) {
                String party = offered.get1();
                Double util = offered.get2();
                maxutils.put(party, maxutils.containsKey(party) ? Math.max(maxutils.get(party), util) : util);
            }
            System.out.println(maxutils); // notice tournament suppresses all
            // output.
        }

        mStrategy = new negotiationStrategy(utilSpace, mNegotiationInfo, isPrinting);
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> validActions) {
        nrChosenActions++;
        if (nrChosenActions > history.size() & lastReceivedBid != null) {
            return new Accept(getPartyId(), lastReceivedBid);
        } else {
            return new Offer(getPartyId(), generateRandomBid());
        }
//
//        double time = this.timeline.getTime();
//        return (Action) (validActions.contains(Accept.class) && this.strategy.selectAccept(this.offeredBid, time) ? new Accept(this.getPartyId(), this.offeredBid) : (this.strategy.selectEndNegotiation(time) ? new EndNegotiation(this.getPartyId()) : this.OfferAction()));
    }

    @Override
    public void receiveMessage(AgentID sender, Action action) {
        super.receiveMessage(sender, action);
        if (action instanceof Offer) {
            lastReceivedBid = ((Offer) action).getBid();
        }
    }

    @Override
    public String getDescription() {
        return "accept Nth offer";
    }


}
