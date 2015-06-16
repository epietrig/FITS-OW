package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gesture.InputSource;
import fr.inria.ilda.gestures.Circle;

public abstract class MTCircularGesture extends MTGestureEvent {

	protected boolean clockwise;
	protected double angle;
	protected Circle circle;
	
	public MTCircularGesture(InputSource source, boolean anchored, boolean external, boolean clockwise, double angle, Circle circle, int fingers) {
		super(source, anchored, external, fingers);
		this.clockwise = clockwise;
		this.angle = angle;
		this.circle = circle;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}
	
	public boolean isClockwise() {
		return clockwise;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof MTCircularGesture && super.equals(o));
	}
	
	@Override
	public String toString() {
		String res = "";
		res += anchored ? "A_" : "F_";
		res += (fingers+"_");
		res += external ? "EXT_" : "INT_";
		return res+"CIRC_"+(isClockwise() ? "CW" : "CCW");
	}
	
}
