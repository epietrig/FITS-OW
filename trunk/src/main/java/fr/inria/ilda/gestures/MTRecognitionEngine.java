package fr.inria.ilda.gestures;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import fr.inria.ilda.gesture.AbstractGestureRecognizer;
import fr.inria.ilda.gesture.AbstractInputEvent;
import fr.inria.ilda.gesture.GestureStateEnum;
import fr.inria.ilda.gesture.IGestureEventListener;
import fr.inria.ilda.gesture.InputEvent2D;
import fr.inria.ilda.gestures.events.MTAnchoredCircularGesture;
import fr.inria.ilda.gestures.events.MTAnchoredInternalLinearGesture;
import fr.inria.ilda.gestures.events.MTFreeCircularGesture;
import fr.inria.ilda.gestures.events.MTFreeExternalLinearGesture;
import fr.inria.ilda.gestures.events.MTFreeInternalLinearGesture;
import fr.inria.ilda.gestures.events.MTGestureEvent;
import fr.inria.ilda.gestures.events.MTStartGestureEvent;
import fr.inria.ilda.gestures.events.MTStopGestureEvent;

public class MTRecognitionEngine extends AbstractGestureRecognizer {

	protected HashMap<String,Finger> fingers;		///< List of fingers position
	protected int pointerDownCount;

	protected ArrayList<MTGestureEvent> recentEvents = new ArrayList<MTGestureEvent>();
	protected int recentEventsCount = 3;//4;
	
	public static final int TRACE_LENGTH = 25;//20;

	public MTRecognitionEngine(String ID){
		super(ID);
		fingers = new HashMap<String,Finger>();
		pointerDownCount = 0;
	}
	
	public void startRecognition(AbstractInputEvent event) {
		state = GestureStateEnum.RUNNING; 
		InputEvent2D event2D = (InputEvent2D)event;  
		Point pt = new Point((int)event2D.x,(int)event2D.y);
		fingerDown(event2D.source.getID(), pt);
		
		MTStartGestureEvent startEvent = new MTStartGestureEvent();
		startEvent.setRecognizerSource(this);
		for (IGestureEventListener listener : listeners) {
			listener.gestureOccured(startEvent);
		}
	}
	
	protected void addGestureEvent(MTGestureEvent lastEvent) {
		if(recentEvents.size() >= recentEventsCount) {
			recentEvents.remove(recentEvents.size()-1);
		}
		recentEvents.add(0, lastEvent);
	}
	
	public MTGestureEvent getStableGesture() {
		if(recentEvents.size() == 0) {
			MTGestureEvent event = new MTGestureEvent(fingersInContactCount());
			event.setRecognizerSource(MTRecognitionEngine.this);
			return event;
		}
		String gestureName = recentEvents.get(0).toString();
		for (int i = 1; i < recentEvents.size(); i++) {
			MTGestureEvent tt = recentEvents.get(i);
			if(tt.toString().compareTo(gestureName) != 0) {
				MTGestureEvent event = new MTGestureEvent(fingersInContactCount());
				event.setRecognizerSource(MTRecognitionEngine.this);
				return event;
			}
		}
		return recentEvents.get(0);
	}
	
	@Override
	public void updateRecognition(AbstractInputEvent event) {
		InputEvent2D event2D = (InputEvent2D)event;  
		Point pt = new Point((int)event2D.x,(int)event2D.y);
		switch (event.state) {
		case START:
			fingerDown(event2D.source.getID(), pt);
			break;
		case UPDATE:
			fingerMove(event2D.source.getID(), pt);
			MTGestureEvent recognized = recognize();
			recognized.setRecognizerSource(MTRecognitionEngine.this);
			addGestureEvent(recognized);
			MTGestureEvent filteredResult = getStableGesture();
			for (IGestureEventListener listener : listeners) {
				listener.gestureOccured(filteredResult);
			}
			break;
		case STOP:
			fingerUp(event.source.getID());
			break;
		}    
		

	}

	@Override
	public void stopRecognition(AbstractInputEvent event) {
		state = GestureStateEnum.IDLE; 	
		InputEvent2D event2D = (InputEvent2D)event;  
		fingerUp(event2D.source.getID());
		
		MTStopGestureEvent stopEvent = new MTStopGestureEvent();
		stopEvent.setRecognizerSource(this);
		for (IGestureEventListener listener : listeners) {
			listener.gestureOccured(stopEvent);
		}
		
		recentEvents.clear();
	}

	public void fingerDown(String id, Point position){
		//    	System.out.println("fingerDown "+id+" x "+position.x+" y "+position.y);
		for (Entry<String, Finger> entry : fingers.entrySet()) {
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			Finger finger = entry.getValue();
			if(finger.isInContact()) {
				finger.addPosition(finger.getLastPoint());
			}
		}     
		Finger finger = new Finger(id);
		finger.isPresent(true);
		finger.addPosition(position);
		pointerDownCount++;
		fingers.put(id, finger);
	}

	public double fingerMove(String id, Point position) {
		double distance = 0;
		for (Entry<String, Finger> entry : fingers.entrySet()) {
			Finger finger = entry.getValue();
			if(finger.isInContact()) {
				if(entry.getKey() == id) {
					finger.addPosition(position);
					if((finger.positions.size()-2) >= 0) {
						distance = Utils.distance(
								finger.positions.get(finger.positions.size()-2),
								finger.positions.get(finger.positions.size()-1));
					}
				} else {
					finger.addPosition(finger.getLastPoint());
				}
			}
		}     
		return distance;
	}

	public void fingerUp(String id) {
		Finger finger = fingers.get(id);
		if (finger!=null){
			if(finger.isInContact()){
				finger.clear();
				pointerDownCount--;                
			}
			fingers.remove(id);
		}
		else 
			System.out.println("fingerUp can't remove finger id "+id);
	}

	public ArrayList<Finger> getAnchoredFingersWithoutId() {
		ArrayList<Finger> staticFingers = new ArrayList<Finger>();
		for (Finger finger : fingers.values()) {
			if(finger.isInContact() && finger.isAnchored()) {
				staticFingers.add(finger);
			}
		}
		return staticFingers;
	}

	public ArrayList<Finger> getFreeFingersWithoutId() {
		ArrayList<Finger> movingFingers = new ArrayList<Finger>();
		for (Finger finger : fingers.values()) {
			if(finger.isInContact() && finger.isFree())  {
				movingFingers.add(finger);
			}
		}
		return movingFingers;
	}

	public ArrayList<Finger> getFingersInContact() {
		ArrayList<Finger> contactFingers = new ArrayList<Finger>();
		for (Finger finger : fingers.values()) {
			if(finger.isInContact())  {
				contactFingers.add(finger);
			}
		}
		return contactFingers;
	}

	public int fingersInContactCount() {
		int count = 0;
		ArrayList<Finger> contactFingers = new ArrayList<Finger>();
		for (Finger finger : fingers.values()) {
			if(finger.isInContact())  {
				contactFingers.add(finger);
				count++;
			}
		}
		return count;
	}

	public Object[] getPolygons(int traceLength) {
		ArrayList<Finger> fingersInContact = getFingersInContact();
		if(fingersInContact.size() < 2) {
			return null;
		}
		Object[] res = new Object[2];
		ArrayList<Point> polygonStart = new ArrayList<Point>();
		ArrayList<Point> polygonEnd = new ArrayList<Point>();

		int stepBack = Integer.MAX_VALUE;
		for(int i = 0; i < fingersInContact.size(); i++) {
			Finger f = fingersInContact.get(i);
			int sb = (f.getPositions().size()-1) - Finger.getStartIndexTrace(traceLength, f.getPositions());
			stepBack = Math.min(stepBack, sb);
		}
		for(int i = 0; i < fingersInContact.size(); i++) {
			Finger f = fingersInContact.get(i);
			polygonStart.add(f.getPositions().get(f.getPositions().size()-1-stepBack));
			polygonEnd.add(f.getLastPoint());
		}
		res[0] = polygonStart;
		res[1] = polygonEnd;
		return res;
	}

	public ArrayList<Circle> getAllInternalCircles(int traceLength) {
		ArrayList<Finger> fingersInContact = getFingersInContact();
		if(fingersInContact.size() <= 2) {
			return null;
		}
		ArrayList<Circle> trace = new ArrayList<Circle>();
		int stepBack = Integer.MAX_VALUE;
		for(int i = 0; i < fingersInContact.size(); i++) {
			Finger f = fingersInContact.get(i);
			int sb = (f.getPositions().size()-1) - Finger.getStartIndexTrace(traceLength, f.getPositions());
			stepBack = Math.min(stepBack, sb);
		}
		for(int j = 0; j <= stepBack; j++) {
			ArrayList<Point> polygon = new ArrayList<Point>();
			for(int i = 0; i < fingersInContact.size(); i++) {
				Finger f = fingersInContact.get(i);
				polygon.add(f.getPositions().get(f.getPositions().size()-1-j));
			}
			Circle circle = null;
			if(polygon.size() == 2) {
				circle = Utils.getCircle(polygon.get(0), polygon.get(1));
			} else {
				circle = Utils.getCircle(polygon.get(0), polygon.get(1), polygon.get(2));
			}
			trace.add(0, circle);
		}
		return trace;
	}

	public double getDistanceToSegment(Point2D pt, Point2D v, Point2D w) {
		double l2 = (v.getX() - w.getX())*(v.getX() - w.getX()) + (v.getY() - w.getY())*(v.getY() - w.getY());
		if(l2 == 0) {
			return 0;
		} else {
			double t = ((pt.getX() - v.getX()) * (w.getX() - v.getX()) + (pt.getY() - v.getY()) * (w.getY() - v.getY())) / l2;
			Point2D point = new Point2D.Double(v.getX() + t * (w.getX() - v.getX()), v.getY() + t * (w.getY() - v.getY()));
			double d = (pt.getX() - point.getX())*(pt.getX() - point.getX()) + (pt.getY() - point.getY())*(pt.getY() - point.getY());
			return Math.sqrt(d);
		}
	}

	public double polygonAreaChange(ArrayList<Point> polygonStart, ArrayList<Point> polygonEnd) {
		Point centroidStart = Utils.centroid(polygonStart);
		Point centroidEnd = Utils.centroid(polygonEnd);
		double distanceStart = 0;
		double distanceEnd = 0;
		for (Iterator<Point> iterator = polygonStart.iterator(); iterator.hasNext();) {
			Point point = iterator.next();
			distanceStart += Utils.distance(point, centroidStart);
		}
		distanceStart /= polygonStart.size();
		for (Iterator<Point> iterator = polygonEnd.iterator(); iterator.hasNext();) {
			Point point = iterator.next();
			distanceEnd += Utils.distance(point, centroidEnd);
		}
		distanceEnd /= polygonEnd.size();
		return (distanceEnd-distanceStart);
	}

	// computes the standard deviation of the distance between the first anchored finger and the first free finger 
	public double sdDistancesToAnchor() {
		Finger anchor = getAnchoredFingersWithoutId().get(0);
		Point anchorLocation = anchor.getLastPoint();
		ArrayList<Finger> freeFingers = getFreeFingersWithoutId();
		Finger freeFinger = freeFingers.get(0);
		int start = freeFinger.getStartIndexTrace(TRACE_LENGTH);
		double[] distancesToAnchor = new double[freeFinger.getPositions().size()-start];
		for (int i = 0; i < distancesToAnchor.length; i++) {
			distancesToAnchor[i] = Utils.distance(anchorLocation, freeFinger.getPositions().get(start+i));
		}
		return Utils.standardDeviation(distancesToAnchor);
	}
	
	public MTGestureEvent recognize() {
		boolean clockwiseStatus = false;
		boolean towardsStatus = false;
		CardinalDirection cardinalDirection = CardinalDirection.NORTH;

		ArrayList<Finger> anchoredFingers = getAnchoredFingersWithoutId();
		ArrayList<Finger> freeFingers = getFreeFingersWithoutId();
		int anchoredFingersCount = anchoredFingers.size();
		int freeFingersCount = freeFingers.size();
		int fingersCount = fingersInContactCount();

		if(freeFingersCount == 0) {
			return new MTGestureEvent(fingersCount);
		}
		
		if(fingersCount < 2) {
			return new MTGestureEvent(fingersCount);
		}
		if(anchoredFingersCount == fingersCount) {
			System.out.println("DWELL");
			return new MTGestureEvent(true); // TODO make a dwell event
		}
		
		Finger firstFreeFinger = getFreeFingersWithoutId().get(0);
		int index = Finger.getStartIndexTrace(TRACE_LENGTH, firstFreeFinger.getPositions());
		long when = firstFreeFinger.getTimeStamps().get(index);
//		if((System.currentTimeMillis() - when) > 500) { // movement information is too old
//			return new MTGestureEvent(fingersCount);
//		}
		
		Object[] polygons = getPolygons(TRACE_LENGTH);
		if(polygons == null || polygons.length < 2) {
			return new MTGestureEvent(fingersCount);
		}

		ArrayList<Point> startPolygon = (ArrayList<Point>)polygons[0];
		ArrayList<Point> endPolygon = (ArrayList<Point>)polygons[1];
		if(anchoredFingersCount == 0) {
			Point startCentroid = Utils.centroid(startPolygon);
			Point endCentroid = Utils.centroid(endPolygon);
			Point2D vector1 = new Point2D.Double(
					startPolygon.get(0).getX() - startCentroid.getX(),
					startPolygon.get(0).getY() - startCentroid.getY());
			Point2D vector2 = new Point2D.Double(
					endPolygon.get(0).getX() - endCentroid.getX(),
					endPolygon.get(0).getY() - endCentroid.getY());
			double angle = Utils.angleBetweenVectors(vector1, vector2);
			double diffArea = polygonAreaChange(startPolygon, endPolygon);
			if(angle >= Math.PI/8 && angle <= (2* Math.PI - Math.PI/8) ) {//&& distanceBetweenCircles <= 10) {
				clockwiseStatus = (angle <= Math.PI/2);
				
				Circle circle = null;
				if(freeFingersCount == 2) {
					circle = Utils.getCircle(freeFingers.get(0).getLastPoint(), freeFingers.get(1).getLastPoint());
				} else if(freeFingersCount >= 3) {
					circle = Utils.getCircle(freeFingers.get(0).getLastPoint(), freeFingers.get(1).getLastPoint(), freeFingers.get(2).getLastPoint());
				} else {
					return new MTGestureEvent(fingersCount);
				}
				Point2D angleFirstFreeFinger = new Point2D.Double(
						freeFingers.get(0).getLastPoint().getX() - circle.getCenter().getX(),
						freeFingers.get(0).getLastPoint().getY() - circle.getCenter().getY());
				Point2D xAxis = new Point2D.Double(1, 0);
				double a = Utils.angleBetweenVectors(xAxis, angleFirstFreeFinger);
				return new MTFreeCircularGesture(clockwiseStatus, a, circle, false, fingersCount);
			} else {
				if(Math.abs(diffArea) >= 10) {
					towardsStatus = diffArea < 0;
					// TODO
					return new MTFreeInternalLinearGesture(towardsStatus, fingersCount);
				} else {
					ArrayList<Point> midPolygon = (ArrayList<Point>)(getPolygons(TRACE_LENGTH/2)[0]);
					Point midCentroid = Utils.centroid(midPolygon);
					vector1 = new Point2D.Double(midCentroid.getX() - startCentroid.getX(), midCentroid.getY() - startCentroid.getY());
					vector2 = new Point2D.Double(endCentroid.getX() - startCentroid.getX(), endCentroid.getY() - startCentroid.getY());
					double angleSuccessiveCentroids = Utils.angleBetweenVectors(vector1, vector2);
					double sinAngleSuccessiveCentroids = Math.abs(angleSuccessiveCentroids);
					if(sinAngleSuccessiveCentroids < 0.1) {
						cardinalDirection = Utils.cardinalDirection(midPolygon.get(0), endPolygon.get(0));
						// TODO
						return new MTFreeExternalLinearGesture(cardinalDirection, fingersCount);
					} else {
						Point endFirstFreeFinger = firstFreeFinger.getLastPoint();
						int indexStart = Finger.getStartIndexTrace(TRACE_LENGTH, firstFreeFinger.getPositions());
						Point startFirstFreeFinger = firstFreeFinger.getPositions().get(indexStart);
						int indexMid = Finger.getStartIndexTrace(TRACE_LENGTH/2, firstFreeFinger.getPositions());
						Point midFirstFreeFinger = firstFreeFinger.getPositions().get(indexMid);
						int indexBack = Finger.getStartIndexTrace(TRACE_LENGTH + TRACE_LENGTH/2, firstFreeFinger.getPositions());
						Point backFirstFreeFinger = firstFreeFinger.getPositions().get(indexBack);
						Circle c1 = Utils.getCircle(startFirstFreeFinger, midFirstFreeFinger, endFirstFreeFinger);
						Circle c2 = Utils.getCircle(backFirstFreeFinger, startFirstFreeFinger, midFirstFreeFinger);
						if(c1.getCenter().distance(c2.getCenter()) < 10 && Math.abs(c1.getRadius() - c2.getRadius()) < 10) {
							clockwiseStatus = areClockwiseOrdered(startPolygon.get(0), midPolygon.get(0), endPolygon.get(0));
							Point2D angleFirstFreeFinger = new Point2D.Double(
									freeFingers.get(0).getLastPoint().getX() - c1.getCenter().getX(),
									freeFingers.get(0).getLastPoint().getY() - c1.getCenter().getY());
							Point2D xAxis = new Point2D.Double(1, 0);
							double a = Utils.angleBetweenVectors(xAxis, angleFirstFreeFinger);
							return new MTFreeCircularGesture(clockwiseStatus, a, c1, true, fingersCount);
						} else {
							return new MTGestureEvent(fingersCount);
						}
					}
				}
			}
		} else {
			if(freeFingersCount == 0) {
				return new MTGestureEvent(fingersCount);
			}
			Point endFirstFreeFinger = firstFreeFinger.getLastPoint();
			int indexStart = Finger.getStartIndexTrace(TRACE_LENGTH, firstFreeFinger.getPositions());
			Point startFirstFreeFinger = firstFreeFinger.getPositions().get(indexStart);
			int indexMid = Finger.getStartIndexTrace(TRACE_LENGTH/2, firstFreeFinger.getPositions());
			Point midFirstFreeFinger = firstFreeFinger.getPositions().get(indexMid);
			
			int indexBack = Finger.getStartIndexTrace(TRACE_LENGTH + TRACE_LENGTH/2, firstFreeFinger.getPositions());
			Point backFirstFreeFinger = firstFreeFinger.getPositions().get(indexBack);
			
			Circle c1 = Utils.getCircle(startFirstFreeFinger, midFirstFreeFinger, endFirstFreeFinger);
			Circle c2 = Utils.getCircle(backFirstFreeFinger, startFirstFreeFinger, midFirstFreeFinger);
			double sdDistancesToAnchor = sdDistancesToAnchor();
			if(sdDistancesToAnchor < 1.5) {
				clockwiseStatus = areClockwiseOrdered(startFirstFreeFinger, midFirstFreeFinger, endFirstFreeFinger);
				Point2D anchorPosition = getAnchoredFingersWithoutId().get(0).getLastPoint(); 
				Circle c = new Circle(anchorPosition, anchorPosition.distance(endFirstFreeFinger));
				Point2D angleFirstFreeFinger = new Point2D.Double(endFirstFreeFinger.getX() - anchorPosition.getX(), endFirstFreeFinger.getY() - anchorPosition.getY()); 
				Point2D xAxis = new Point2D.Double(1, 0);
				double a = Utils.angleBetweenVectors(xAxis, angleFirstFreeFinger);
				return new MTAnchoredCircularGesture(clockwiseStatus, getAnchoredFingersWithoutId().get(0).getLastPoint(), 
						a, c, false, fingersCount);
			} else {
				if(c1.getCenter().distance(c2.getCenter()) < 10 && Math.abs(c1.getRadius() - c2.getRadius()) < 10) {
					clockwiseStatus = areClockwiseOrdered(startFirstFreeFinger, midFirstFreeFinger, endFirstFreeFinger);  
					Point2D angleFirstFreeFinger = new Point2D.Double(
							endFirstFreeFinger.getX() - c1.getCenter().getX(),
							endFirstFreeFinger.getY() - c1.getCenter().getY());
					Point2D xAxis = new Point2D.Double(1, 0);
					double a = Utils.angleBetweenVectors(xAxis, angleFirstFreeFinger);
					return new MTAnchoredCircularGesture(clockwiseStatus, getAnchoredFingersWithoutId().get(0).getLastPoint(), 
							a, c1, true, fingersCount);
				} else {
					Point2D vector1 = new Point2D.Double(midFirstFreeFinger.getX() - startFirstFreeFinger.getX(), midFirstFreeFinger.getY() - startFirstFreeFinger.getY());
					Point2D vector2 = new Point2D.Double(endFirstFreeFinger.getX() - startFirstFreeFinger.getX(), endFirstFreeFinger.getY() - startFirstFreeFinger.getY());
					double sinAngleSuccessiveFreePoints = Math.abs(Math.sin(Utils.angleBetweenVectors(vector1, vector2)));
					if(sinAngleSuccessiveFreePoints < 0.1) {
						double diffArea = polygonAreaChange(startPolygon, endPolygon);
						towardsStatus = diffArea < 0;
						// TODO
						return new MTAnchoredInternalLinearGesture(getAnchoredFingersWithoutId().get(0).getLastPoint(), towardsStatus, fingersCount);
					} else {
						return new MTGestureEvent(fingersCount);
					}
				}
			}
		}
	}

	public static boolean areClockwiseOrdered(Point pt1, Point pt2, Point pt3) {
		// http://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order
		Point[] points = new Point[3];
		points[0] = pt1;
		points[1] = pt2;
		points[2] = pt3;
		int[] edges = new int[3];
		edges[0] = (points[1].x - points[0].x)*(-points[1].y - points[0].y);
		edges[1] = (points[2].x - points[1].x)*(-points[2].y - points[1].y);
		edges[2] = (points[0].x - points[2].x)*(-points[0].y - points[2].y);
		int sum = 0;
		for (int i = 0; i < edges.length; i++) {
			sum += edges[i];
		}
		return sum >= 0;
	}
}
