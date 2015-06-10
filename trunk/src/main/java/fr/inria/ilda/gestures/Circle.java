package fr.inria.ilda.gestures;

import java.awt.geom.Point2D;

public class Circle {

	private Point2D center;
	private double radius;
	
	public Circle() { }
	
	public Circle(Point2D center, double radius) {
		super();
		this.center = center;
		this.radius = radius;
	}

	public Point2D getCenter() {
		return center;
	}
	
	public void setCenter(Point2D center) {
		this.center = center;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
}

