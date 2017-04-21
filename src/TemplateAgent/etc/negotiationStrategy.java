package TemplateAgent.etc;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.utility.AbstractUtilitySpace;

public class negotiationStrategy {
	private AbstractUtilitySpace utilitySpace;
	private negotiationInfo negotiationInfo;
	private double rv = 0.0; // 留保価格

	private boolean isPrinting = false; // デバッグ用
	
	public negotiationStrategy(AbstractUtilitySpace utilitySpace, negotiationInfo negotiationInfo, boolean isPrinting) {
		this.utilitySpace = utilitySpace;
		this.negotiationInfo = negotiationInfo;
		this.isPrinting = isPrinting;
		rv = utilitySpace.getReservationValue();
	}
	
	// 受容判定
	public boolean selectAccept(Bid offeredBid, double time) {
		try {
			if(utilitySpace.getUtility(offeredBid) >= getThreshold(time)){ return true; }
			else{ return false; }
		} catch (Exception e) {
			System.out.println("受容判定に失敗しました");
			e.printStackTrace();
			return false;
		}
	}
	
	// 交渉終了判定
	public boolean selectEndNegotiation(double time) {
		return false;
	}
	
	// 閾値を返す
	public double getThreshold(double time) {
		double threshold = 1.0;

		/* 交渉戦略に基づきthreshold(t)を設計する */
		/* negotiationInfoから提案履歴の統計情報を取得できるので使っても良い */
		/* （統計情報についてはあまりデバッグできていないので，バグが見つかった場合は報告をお願いします） */
		
		// 例：
		ArrayList<Object> opponents =  negotiationInfo.getOpponents();
		for(Object sender:opponents){
			double m = negotiationInfo.getAverage(sender);
			double v = negotiationInfo.getVariancer(sender);
			double sd = negotiationInfo.getStandardDeviation(sender);
		}
		
		return threshold;
	}
}
