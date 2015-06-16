package fr.inria.ilda.fitsow.cursors;

import java.awt.geom.Point2D;
import fr.inria.zvtm.engine.VirtualSpace;

public abstract class AbstractCursor
{

	final VirtualSpace targetSpace;
	double xPos, yPos;
	boolean visible;

	public AbstractCursor(VirtualSpace targetSpace, double x, double y)
	{
		this.targetSpace = targetSpace;
		this.xPos = x; this.yPos = y;
		visible = true;
	}
	
	public AbstractCursor(VirtualSpace targetSpace)
	{
		this(targetSpace, 0, 0);
	}

	public abstract void dispose();

	public void moveTo(double x, double y){
		xPos = x; yPos = y;
	}
	
	public void move(double x, double y){
		xPos = xPos+x; yPos = yPos+y;
	}

	/**
     * Gets the cursor position, in virtual space units.
     * @return the cursor position, in virtual space units.
     */
   	public Point2D.Double getLocation(){
   		return new Point2D.Double(xPos, yPos);
   	}

    /**
     * Gets the cursor position x-coordinate, in virtual space units.
     * @return the cursor position x-coordinate, in virtual space units.
     */
    public double getX() { return xPos; }

    /**
     * Gets the cursor position y-coordinate, in virtual space units.
     * @return the cursor position y-coordinate, in virtual space units.
     */
    public double getY() { return yPos; }


    public void setVisible(boolean v){
    	visible = v;
    }

    public boolean isVisible() { return visible; }

    public abstract double[] getBounds();

}