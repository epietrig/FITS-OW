package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gestures.CardinalDirection;

public class MTFreeExternalLinearGesture extends MTGestureEvent {

	protected CardinalDirection cardinalDirection; // attribute linear external gestures
	
	public MTFreeExternalLinearGesture(CardinalDirection cardinalDirection, int fingers) {
		super(false, true, fingers);
		this.cardinalDirection = cardinalDirection;
	}

	public CardinalDirection getCardinalDirection() {
		return cardinalDirection;
	}

	public void setCardinalDirection(CardinalDirection cardinalDirection) {
		this.cardinalDirection = cardinalDirection;
	}
	
	public String toString() {
		String res = "";
		res += anchored ? "A_" : "F_";
		res += (fingers+"_");
		res += external ? "EXT_" : "INT_";
		return res+"LIN_"+cardinalDirection;
	}
	
}
