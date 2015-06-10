package fr.inria.ilda.gestures;



import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Created by appert on 08/03/15.
 */
public class Finger {

	public String id;										// Finger ID
	protected boolean inContact;						// Tells if the finger is present on the screen

	protected ArrayList<Long> timeStamps;
	protected ArrayList<Point> positions;				// List of all the position since the last PointerDown event
	
	protected double traceLength = 0;

	//    protected boolean lastAnchoredState;

	public static final int TRACE_LENGTH = 200;

	// being anchored means staying in contact for at least 500ms in a tolerance area of 5mm
	public static int ANCHOR_TOLERANCE = 10; // 50; // 30;
	public static int ANCHOR_DURATION = 500;

	protected Rectangle boundingBoxTrace = new Rectangle(0, 0, 1, 1);
	protected int durationTrace = 0;

	public Finger(String id, boolean present, ArrayList<Point> positions, ArrayList<Long> timeStamps){
		this.id = id;
		this.inContact = present;
		this.positions = positions;
		this.timeStamps = timeStamps;
	}

	public Finger(Finger finger){
		this(
				finger.id,
				finger.inContact,
				finger.positions,
				finger.timeStamps);
	}

	public Finger(String id) {
		this(
				id,
				false,
				new ArrayList<Point>(),
				new ArrayList<Long>());
	}
	
	public Point getLastMove() {
		if(positions.size() < 2) {
			return new Point(0, 0);
		} else {
			Point last = positions.get(positions.size()-1);
			Point oneButLast = positions.get(positions.size()-2);
			return new Point(last.x - oneButLast.x, last.y - oneButLast.y);
		}
	}

	public Point getLastPoint() {
		if(positions.size() == 0) {
			return null;
		} else {
			return positions.get(positions.size()-1);
		}
	}

	public int getStartIndexTrace(int traceLength) {
		return getStartIndexTrace(traceLength, positions);
	}

	public static int getStartIndexTrace(int traceLength, ArrayList<Point> positions) {
		int start = positions.size()-1;
		double dist = 0;
		Point p1, p2;
		while((start-1) >= 0 && dist < traceLength) {
			p1 = positions.get(start);
			p2 = positions.get(start-1);
			dist += Utils.distance(p1, p2);
			start--;
		}
		return start;
	}

	public static int getStartIndexTrace(long duration, ArrayList<Long> times) {
		int start = times.size()-1;
		long d = 0;
		long t1, t2;
		while((start-1) >= 0 && d < duration) {
			t1 = times.get(start);
			t2 = times.get(start-1);
			d += (t1 - t2);
			start--;
		}
		return start;
	}

	//    public static Path getTrace(int traceLength, ArrayList<Point> positions) {
		//        int start = getStartIndexTrace(traceLength, positions);
		//        Path path = new Path();
		//        for(int i = start; i<positions.size();i++){
			//            if(i == start){
				//                path.moveTo(positions.get(i).x, positions.get(i).y);
				//            }else{
	//                path.lineTo(positions.get(i).x, positions.get(i).y);
	//            }
	//        }
	//        return path;
	//    }
	//
	//    public Path getTrace(int traceLength) {
	//        return getTrace(traceLength, positions);
	//    }

	public Point getPoint(int pathLength) {
		int start = getStartIndexTrace(pathLength, positions);
		return positions.get(start);
	}

	// Getters
	public String getID() {
		return id;
	}
	public boolean isInContact() {
		return inContact;
	}

	/**
	 * Add a new position (newPosition is a point in mms in the physical space) and compute the movement
	 * @param newPosition The new position
	 */
	public void addPosition(Point newPosition){
//		System.out.println("add position "+newPosition+" to finger "+id);
		if(positions.size() > 0) {
			Point lastPosition = positions.get(positions.size()-1);
			if(newPosition.x != lastPosition.x || newPosition.y != lastPosition.y){
				positions.add(newPosition);
				timeStamps.add(System.currentTimeMillis());
				traceLength += lastPosition.distance(newPosition);
			}
		} else {
			positions.add(newPosition);
			timeStamps.add(System.currentTimeMillis());
			traceLength = 0;
		}


		int index = positions.size()-1;
		boundingBoxTrace.setBounds(newPosition.x, newPosition.y, 1, 1);
		durationTrace = 0;
		int maxX, maxY;
		while(durationTrace < ANCHOR_DURATION && index > 0) {
			Point pos = positions.get(index-1);
			maxX = boundingBoxTrace.x + boundingBoxTrace.width;
			maxY = boundingBoxTrace.y + boundingBoxTrace.height;
			boundingBoxTrace.x = Math.min(pos.x, boundingBoxTrace.x);
			boundingBoxTrace.y = Math.min(pos.y, boundingBoxTrace.y);
			maxX = Math.max(maxX, pos.x);
			maxY = Math.max(maxY, pos.y);
			boundingBoxTrace.width = maxX - boundingBoxTrace.x;
			boundingBoxTrace.height = maxY - boundingBoxTrace.y;
			durationTrace += (timeStamps.get(index)-timeStamps.get(index-1));
			index--;
		}
	}

	public static boolean straight(int distance, ArrayList<Point> points) {
		// to avoid problems with change in direction, we consider the longest segment
		// that is along the main cardinal direction
		int startIndex = points.size() - 1;
		Point lastPoint = points.get(points.size()-1);
		Point currentPoint = points.get(points.size()-1);
		int index = startIndex - 1;
		double dist = 0;
		CardinalDirection currentDirection = CardinalDirection.UNKNOWN;

		while(dist < distance && index >= 0) {

			CardinalDirection lastDirection = Utils.cardinalDirection(points.get(index), currentPoint);
			if(currentDirection != CardinalDirection.UNKNOWN
					&& lastDirection != CardinalDirection.UNKNOWN
					&& lastDirection != currentDirection) {
				break;
			}
			currentDirection = lastDirection;

			dist += Utils.distance(currentPoint, points.get(index));
			currentPoint = points.get(index);
			index--;
		}
		index++;
		if(dist == 0 || index >= points.size()) {
			return false;
		}
		double straightDistance = Utils.distance(lastPoint, points.get(index));
		double straightnessRatio = straightDistance/dist;
		return straightnessRatio > 0.99;
	}


	public boolean isAnchored() {
		if(getTimeStamps().size() == 0) {
			return false;
		}
		long timeSinceLastSample = System.currentTimeMillis() - getTimeStamps().get(getTimeStamps().size()-1);
		return (durationTrace + timeSinceLastSample) > ANCHOR_DURATION && (boundingBoxTrace.width < ANCHOR_TOLERANCE && boundingBoxTrace.height < ANCHOR_TOLERANCE);
	}

	public boolean isFree() {
		if(getTimeStamps().size() == 0) {
			return false;
		}
		long timeSinceLastSample = System.currentTimeMillis() - getTimeStamps().get(getTimeStamps().size()-1);
		return (durationTrace + timeSinceLastSample) > ANCHOR_DURATION && (boundingBoxTrace.width >= ANCHOR_TOLERANCE || boundingBoxTrace.height >= ANCHOR_TOLERANCE);
	}

	/**
	 * Clear all the stored datas for this finger (basically called on a PointerUp event)
	 */
	public void clear() {
		inContact = false;
		positions.clear();
		timeStamps.clear();
		traceLength = 0;
	}

	public void isPresent(boolean state) {
		inContact = state;
	}

	public ArrayList<Point> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Point> positions) {
		this.positions = positions;
	}

	public ArrayList<Long> getTimeStamps() {
		return timeStamps;
	}

	public double getTraceLength() {
		return traceLength;
	}

	public void setTraceLength(double traceLength) {
		this.traceLength = traceLength;
	}

}

