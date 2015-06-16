package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gesture.InputSource;

public class MTStartGestureEvent extends MTGestureEvent {

	public MTStartGestureEvent(InputSource source) {
		super(source, 1);
	}
	
}
