package fr.inria.ilda.gestures.events;

import java.awt.geom.Point2D;

import fr.inria.ilda.gesture.InputSource;

public class MTAnchoredInternalLinearGesture extends MTInternalLinearGesture {
	
	protected Point2D anchorPoint;

	public MTAnchoredInternalLinearGesture(InputSource source, Point2D anchorPoint, boolean towards, int fingers) {
		super(source, true, towards, fingers);
		this.anchorPoint = anchorPoint;
	}

}
