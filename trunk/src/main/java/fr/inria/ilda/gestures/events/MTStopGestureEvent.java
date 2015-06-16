package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gesture.InputSource;

public class MTStopGestureEvent extends MTGestureEvent {

	public MTStopGestureEvent(InputSource source) {
		super(source, 0);
	}
	
}
