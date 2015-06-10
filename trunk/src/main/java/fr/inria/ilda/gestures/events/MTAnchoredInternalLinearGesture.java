package fr.inria.ilda.gestures.events;

import java.awt.geom.Point2D;

public class MTAnchoredInternalLinearGesture extends MTInternalLinearGesture {
	
	protected Point2D anchorPoint;

	public MTAnchoredInternalLinearGesture(Point2D anchorPoint, boolean towards, int fingers) {
		super(true, towards, fingers);
		this.anchorPoint = anchorPoint;
	}

}
