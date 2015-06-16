package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gesture.InputSource;


public abstract class MTInternalLinearGesture extends MTGestureEvent {

	protected boolean towards; // attribute for linear internal gestures

	public MTInternalLinearGesture(InputSource source, boolean anchored, boolean towards, int fingers) {
		super(source, anchored, false, fingers);
		this.towards = towards;
	}
	
	public boolean isTowards() {
		return towards;
	}

	public void setTowards(boolean towards) {
		this.towards = towards;
	}
	
	public String toString() {
		String res = "";
		res += anchored ? "A_" : "F_";
		res += (fingers+"_");
		res += external ? "EXT_" : "INT_";
		return res+"LIN_"+(towards ? "IN" : "OUT");
	}
	
}
