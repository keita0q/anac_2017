package HogeAgent;

import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.persistent.PersistentDataContainer;
import negotiator.utility.AbstractUtilitySpace;

/**
 * This is your negotiation party.
 */
public class HogeAgent extends AbstractNegotiationParty {

    private Bid lastReceivedBid = null;
    private long mStart;
    private AbstractUtilitySpace mUtilitySpace;
    private long TIME_LIMIT; //ms

    private final double THRESHOLD_PARAM = 1.0;

    @Override
    public void init(AbstractUtilitySpace utilSpace, Deadline dl, negotiator.timeline.TimeLineInfo tl, long randomSeed,
                     AgentID agentId, PersistentDataContainer storage) {

        super.init(utilSpace, dl, tl, randomSeed, agentId, storage);

        System.out.println("Discount Factor is " + utilSpace.getDiscountFactor());
        System.out.println("Reservation Value is " + utilSpace.getReservationValueUndiscounted());

        // if you need to initialize some variables, please initialize them
        // below

        mStart = System.currentTimeMillis();
        mUtilitySpace = utilSpace;
        TIME_LIMIT = dl.getTimeOrDefaultTimeout() * 1000;
    }

    /**
     * Each round this method gets called and ask you to accept or offer. The
     * first party in the first round is a bit different, it can only propose an
     * offer.
     *
     * @param validActions Either a list containing both accept and offer or only offer.
     * @return The chosen action.
     */
    @Override
    public Action chooseAction(List<Class<? extends Action>> validActions) {

        // with 50% chance, counter offer
        // if we are the first party, also offer.
        double tSpendTime = (System.currentTimeMillis() - mStart) / TIME_LIMIT;
        double tThreshold = 1 - Math.pow(tSpendTime, THRESHOLD_PARAM); // 合意案候補受託の閾値
        List<Issue> tIssues = mUtilitySpace.getDomain().getIssues();
        double tCurrentUtility = this.getUtility(this.lastReceivedBid);

        if (lastReceivedBid == null || !validActions.contains(Accept.class) || Math.random() > 0.5) { // アクセプトするためのロジック -> 譲歩関数
            return new Offer(getPartyId(), generateRandomBid()); // random bid を提案する戦略をとっている
        } else {
            if (tCurrentUtility < tThreshold) { // 閾値との照合
                return new Offer(getPartyId(), generateRandomBid()); // random bid を提案する戦略をとっている
            }
            return new Accept(getPartyId(), lastReceivedBid);
        }
    }

    /**
     * All offers proposed by the other parties will be received as a message.
     * You can use this information to your advantage, for example to predict
     * their utility.
     *
     * @param sender The party that did the action. Can be null.
     * @param action The action that party did.
     */
    @Override
    public void receiveMessage(AgentID sender, Action action) {
        super.receiveMessage(sender, action);
        if (action instanceof Offer) {
            lastReceivedBid = ((Offer) action).getBid();
        }
    }

    @Override
    public String getDescription() {
        return "example party group N";
    }

}
