package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gesture.InputSource;

public class MTFreeInternalGesture extends MTInternalLinearGesture {

	public MTFreeInternalGesture(InputSource source, boolean towards, int fingers) {
		super(source, false, towards, fingers);
	}


}
