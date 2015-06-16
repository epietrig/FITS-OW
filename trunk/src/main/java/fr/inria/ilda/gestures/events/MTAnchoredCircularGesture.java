package fr.inria.ilda.gestures.events;

import java.awt.geom.Point2D;

import fr.inria.ilda.gesture.InputSource;
import fr.inria.ilda.gestures.Circle;

public class MTAnchoredCircularGesture extends MTCircularGesture {

	protected Point2D anchorPoint;

	public MTAnchoredCircularGesture(InputSource source, boolean clockwise, Point2D anchorPoint, double angle, Circle circle, boolean external, int fingers) {
		super(source, true, external, clockwise, angle, circle, fingers);
		this.anchorPoint = anchorPoint;
	}

}
