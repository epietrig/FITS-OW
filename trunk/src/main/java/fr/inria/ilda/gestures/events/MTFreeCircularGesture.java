package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gesture.InputSource;
import fr.inria.ilda.gestures.Circle;

public class MTFreeCircularGesture extends MTCircularGesture {

	public MTFreeCircularGesture(InputSource source, boolean clockwise, double angle, Circle circle, boolean external, int fingers) {
		super(source, false, external, clockwise, angle, circle, fingers);
	}

}