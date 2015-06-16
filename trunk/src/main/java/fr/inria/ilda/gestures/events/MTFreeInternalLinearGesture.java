package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gesture.InputSource;

public class MTFreeInternalLinearGesture extends MTInternalLinearGesture {

	public MTFreeInternalLinearGesture(InputSource source, boolean towards, int fingers) {
		super(source, false, towards, fingers);
	}

}
