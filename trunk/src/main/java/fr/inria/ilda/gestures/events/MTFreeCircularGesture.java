package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gestures.Circle;

public class MTFreeCircularGesture extends MTCircularGesture {

	public MTFreeCircularGesture(boolean clockwise, double angle, Circle circle, boolean external, int fingers) {
		super(false, external, clockwise, angle, circle, fingers);
	}

}