package fr.inria.ilda.gestures;


import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by appert on 08/03/15.
 */public class Utils {

    public static final String TAG = "MyActivity";

    public static boolean clockwise(ArrayList<Point> polygon) {
        int[] edges = new int[polygon.size()];
        int sum = 0;
        for (int i = 0; i < edges.length; i++) {
            int nextIndex = (i+1) < polygon.size() ? i+1 : 0;
            edges[i] = (polygon.get(nextIndex).x - polygon.get(i).x)*(-polygon.get(nextIndex).y - polygon.get(i).y);
            sum += edges[i];
        }
        return sum >= 0;
    }

    public static Point centroid(ArrayList<Point> points) {
        double sumX = 0;
        double sumY = 0;
        for (Iterator<Point> iterator = points.iterator(); iterator.hasNext();) {
            Point next = iterator.next();
            sumX += next.x;
            sumY += next.y;
        }
        int length = points.size();
        return
                points.size() > 1 ?
                        new Point((int)(sumX / length), (int)(sumY / length))
                        : null;
    }
    
    public static double indicativeAngle(ArrayList<Point> points, Point refPoint) {
        Point centroid = centroid(points);
        if(centroid == null) {
            return 0.0;
        }
        double angle = Math.atan2(refPoint.y - centroid.y, centroid.x - refPoint.x);
        return angle;
    }

    // returns two points that belong to a mediatrice of a segment
    public static Point[] mediatrice(Point a, Point b) {
        int dx = b.x - a.x;
        int dy = b.y - a.y;
        Point normal = new Point(-dy, dx);
        Point middle = new Point((a.x+b.x)/2, (a.y+b.y)/2);
        Point other = new Point(middle.x + normal.x, middle.y - normal.y);
        return new Point[]{middle, other};
    }


    public static double variability(ArrayList<ArrayList<Point>> traces) {
        if(traces.size() == 0) {
            return 0.0;
        }
        double res = 0;
        double meanDist;
        for (int i = 0; i < traces.get(0).size(); i++) {
            Point pt = traces.get(0).get(i);
            meanDist = 0;
            for (int j = 1; j < traces.size(); j++) {
                Point p = traces.get(j).get(i);
                meanDist += Utils.distance(pt, p);
            }
            meanDist /= traces.size();
            res += meanDist;
        }
        res /= traces.get(0).size();
        return res;
    }

    public static double standardDeviation(double[] angles) {
        double mean = 0;
        for (int i = 0; i < angles.length; i++) {
            mean += angles[i];
        }
        mean /= angles.length;

        double var = 0;
        for (int i = 0; i < angles.length; i++) {
            var += (mean - angles[i])*(mean - angles[i]);
        }
        var /= angles.length;

        return Math.sqrt(var);
    }

    public static double standardDeviationToFirstPoint(double[] angles) {
        double mean = angles[0];
        double var = 0;
        for (int i = 0; i < angles.length; i++) {
            var += (mean - angles[i])*(mean - angles[i]);
        }
        var /= (angles.length - 1);

        return Math.sqrt(var);
    }

    public static double distance(Point p1, Point p2) {
        return Math.sqrt((p2.x - p1.x)*(p2.x - p1.x) + (p2.y - p1.y)*(p2.y - p1.y));
    }

    public static double pathLength(ArrayList<Point> points) {
        double d = 0;
        for (int i = 1; i < points.size(); i++) {
            d += distance(points.get(i - 1), points.get(i));
        }
        return d;
    }

    public static void resample(ArrayList<Point> points, int n, ArrayList<Point> newPoints) {
        if (points.isEmpty())
            return;
        ArrayList<Point> dstPts = new ArrayList<Point>(n);

        double segLength = pathLength(points) / (n - 1);

        double currentSegLength = 0;
        ArrayList<Point> srcPts = new ArrayList<Point>(points);

        dstPts.add(new Point(srcPts.get(0).x, srcPts.get(0).y));
        for (int i = 1; i < srcPts.size(); i++) {
            Point pt1 = srcPts.get(i - 1);
            Point pt2 = srcPts.get(i);
            double d = distance(pt1, pt2);
            if(d == 0) continue;
            if ((currentSegLength + d) >= segLength) {
                double qx = pt1.x + ((segLength - currentSegLength) / d) * (pt2.x - pt1.x);
                double qy = pt1.y + ((segLength - currentSegLength) / d) * (pt2.y - pt1.y);
                Point q = new Point((int)qx, (int)qy);
                dstPts.add(q); // append new point 'q'
                srcPts.add(i, q); // insert 'q' at position i in points s.t.
                // 'q' will be the next i
                currentSegLength = 0.0;
            } else {
                currentSegLength += d;
            }
        }
        // sometimes we fall a rounding-error short of adding the last point, so
        // add it if so
        while (dstPts.size() < n) {
            dstPts.add(new Point(srcPts.get(srcPts.size() - 1).x,
                    srcPts.get(srcPts.size() - 1).y));
        }
        newPoints.clear();
        newPoints.addAll(dstPts);
    }

    public static double pathDistance(ArrayList<Point> ptsA, ArrayList<Point> ptsB) {
        ArrayList<Point> pointsA = new ArrayList<Point>();
        ArrayList<Point> pointsB = new ArrayList<Point>();
        resample(ptsA, 50, pointsA);
        resample(ptsB, 50, pointsB);

        double d = 0;
        Iterator<Point> iteratorB = pointsB.iterator();
        Point ptA, ptB;
        for (Iterator<Point> iteratorA = pointsA.iterator(); iteratorA.hasNext();) {
            ptA = iteratorA.next();
            ptB = iteratorB.next();
            d += distance(ptA, ptB);
        }
        return d / pointsA.size();
    }

    public static void resample(ArrayList<Point> points, int startIndex, int n, ArrayList<Point> newPoints) {
        if (points.isEmpty())
            return;
        ArrayList<Point> dstPts = new ArrayList<Point>(n);

        double segLength = pathLength(points) / (n - 1);

        double currentSegLength = 0;
        ArrayList<Point> srcPts = new ArrayList<Point>(points);

        dstPts.add(new Point(srcPts.get(startIndex).x, srcPts.get(startIndex).y));
        for (int i = startIndex+1; i < srcPts.size(); i++) {
            Point pt1 = srcPts.get(i - 1);
            Point pt2 = srcPts.get(i);
            double d = distance(pt1, pt2);
            if(d == 0) continue;
            if ((currentSegLength + d) >= segLength) {
                double qx = pt1.x + ((segLength - currentSegLength) / d) * (pt2.x - pt1.x);
                double qy = pt1.y + ((segLength - currentSegLength) / d) * (pt2.y - pt1.y);
                Point q = new Point((int)qx, (int)qy);
                dstPts.add(q); // append new point 'q'
                srcPts.add(i, q); // insert 'q' at position i in points s.t.
                // 'q' will be the next i
                currentSegLength = 0.0;
            } else {
                currentSegLength += d;
            }
        }
        // sometimes we fall a rounding-error short of adding the last point, so
        // add it if so
        while (dstPts.size() < n) {
            dstPts.add(new Point(srcPts.get(srcPts.size() - 1).x,
                    srcPts.get(srcPts.size() - 1).y));
        }
        while (dstPts.size() > n) {
            dstPts.remove(dstPts.size()/2);
        }


        if(dstPts.size() != n) {
            System.out.println("dstPts.size()=" + dstPts.size());
            System.out.println("\t-->" + srcPts.size() +" -- "+startIndex);
            System.out.println("\t-->" + segLength);
        }

        newPoints.clear();
        newPoints.addAll(dstPts);
    }

    public static void translateToZero(ArrayList<Point> points) {
        int dx = points.get(0).x;
        int dy = points.get(0).y;
        for (Iterator<Point> iterator = points.iterator(); iterator.hasNext();) {
            Point p = iterator.next();
            p.x -= dx;
            p.y -= dy;
        }
    }

    public static double pathesVariability(ArrayList<ArrayList<Point>> pts, ArrayList<Integer> startIndices, int nbSamples) {
        ArrayList<ArrayList<Point>> newPoints = new ArrayList<ArrayList<Point>>();
        Iterator<Integer> iteratorStartIndices = startIndices.iterator();
        for (Iterator<ArrayList<Point>> iteratorTraces = pts.iterator(); iteratorTraces.hasNext();) {
            ArrayList<Point> aTrace = iteratorTraces.next();
            Integer startIndex = iteratorStartIndices.next();
            ArrayList<Point> resampledTrace = new ArrayList<Point>();
            //			Log.v(TAG, "aTrace=" + aTrace.size());
            //			Log.v(TAG, "start=" + startIndex);
            resample(aTrace, startIndex, nbSamples, resampledTrace);
            //			Log.v(TAG, "aResampledTrace=" + resampledTrace.size());
            translateToZero(resampledTrace);
            newPoints.add(resampledTrace);
        }
        return variability(newPoints);
    }

    public static double area(ArrayList<Point> polygon) {
        double res = 0;
        Point current, next;
        for (int i = 0; i < polygon.size(); i++) {
            current = polygon.get(i);
            next = (i+1) < polygon.size() ? polygon.get(i+1) : polygon.get(0);
            res += (current.x*next.y - next.x*current.y);
        }
        res /= 2;
        return res;
    }

    public static CardinalDirection cardinalDirection(Point p1, Point p2) {
        if(Math.abs(p2.x - p1.x) > Math.abs(p2.y - p1.y)) {
            // x-axis is dominant
            if((p2.x - p1.x) > 0) {
                return CardinalDirection.EAST;
            } else {
                return CardinalDirection.WEST;
            }
        } else {
            // y-axis is dominant
            if((p2.y - p1.y) > 0) {
                return CardinalDirection.SOUTH;
            } if((p2.y - p1.y) < 0) {
                return CardinalDirection.NORTH;
            } else {
                return CardinalDirection.UNKNOWN;
            }
        }
    }

    public static double straightLength(int distance, ArrayList<Point> points) {
        // to avoid problems with change in direction, we consider the longest segment
        // that is along the main cardinal direction
        int startIndex = points.size() - 1;
        Point lastPoint = points.get(points.size()-1);
        Point currentPoint = points.get(points.size()-1);
        int index = startIndex - 1;
        double dist = 0;

        while(dist < distance && index >= 0) {

            double d = Utils.distance(currentPoint, points.get(index));
            dist += d;
            currentPoint = points.get(index);


            double straightDistance = Utils.distance(lastPoint, points.get(index));
            double straightnessRatio = straightDistance/dist;
            if(straightnessRatio < 0.99) {
                return dist - d;
            }
            if(index == 0) {
                return dist;
            }

            index--;
        }
        return distance;
    }

    public static boolean isArc(int distance, ArrayList<Point> points) {
        // to avoid problems with change in direction, we consider the longest segment
        // that is along the main cardinal direction
        int startIndex = points.size() - 1;
        Point lastPoint = points.get(points.size()-1);
        Point currentPoint = points.get(points.size()-1);
        int index = startIndex;
        double pathdist = 0;

        double maxStraightDistance = -1;
        while(pathdist < distance && index >= 1) {
            index--;
            maxStraightDistance = Math.max(Utils.distance(lastPoint, points.get(index)), maxStraightDistance);
            double d = Utils.distance(currentPoint, points.get(index));
            pathdist += d;
            currentPoint = points.get(index);
        }

        boolean res = true;
        double straightDistance = Utils.distance(lastPoint, points.get(index));
        if(maxStraightDistance > straightDistance) {
            res = false;
        } else {
            double straightnessRatio = straightDistance/pathdist;
            if(straightnessRatio >= 0.99) {
                res = false;
            } else {
                double cornerDistance = Math.abs(lastPoint.x - points.get(index).x) + Math.abs(lastPoint.y - points.get(index).y);
                double cornerRatio = cornerDistance/pathdist;
//				Log.v(Utils.TAG, ": (cornerRatio=" + cornerRatio + ")");
                if(cornerRatio >= 0.9 && cornerRatio <= 1.1) {
                    res = false;
                } else {
                    res = true;
                }
            }
        }
//		Log.v(Utils.TAG, "isArc? " + res);
        return res;
    }
    
    public static double angleBetweenVectors(Point2D vector1, Point2D vector2) {
		//		The (directed) angle from vector1 to vector2 can be computed as
		double angle = Math.atan2(vector2.getY(), vector2.getX()) - Math.atan2(vector1.getY(), vector1.getX());
		//		and you may want to normalize it to the range 0 .. 2 * Pi:
		if (angle < 0) angle += 2 * Math.PI;
		return angle;
	}
    
 // adapted from C++ code found in http://mathforum.org/library/drmath/view/54323.html
 	public static Circle getCircle(Point2D pt1, Point2D pt2, Point2D pt3) {
 		double bx = pt1.getX();
 		double by = pt1.getY(); 
 		double cx = pt2.getX();
 		double cy = pt2.getY();
 		double dx = pt3.getX();
 		double dy = pt3.getY();
 		double temp = cx*cx+cy*cy;
 		double bc = (bx*bx + by*by - temp)/2.0;
 		double cd = (temp - dx*dx - dy*dy)/2.0;
 		double det = (bx-cx)*(cy-dy)-(cx-dx)*(by-cy); 
 		if (Math.abs(det) < 1.0e-6) {
 			return new Circle(new Point2D.Double(1, 1), 1);
 		}
 		det = 1/det;
 		double centerx = (bc*(cy-dy)-cd*(by-cy))*det;
 		double centery = ((bx-cx)*cd-(cx-dx)*bc)*det;
 		Point2D center = new Point2D.Double(centerx, centery); 
 		double radius = center.distance(pt1);
 		return new Circle(center, radius);
 	}

 	public static Circle getCircle(Point2D pt1, Point2D pt2) {
 		Point2D center = new Point2D.Double((pt1.getX()+pt2.getX())/2, (pt1.getY()+pt2.getY())/2);
 		return new Circle(center, pt1.distance(center));
 	}

}

