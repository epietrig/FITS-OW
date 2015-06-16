package fr.inria.ilda.fitsow.cursors;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.BasicStroke;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.glyphs.SIRectangle;
import fr.inria.zvtm.glyphs.Glyph;

/**
 * A cursor suitable for interacting with the clustered viewer on a large display.
 */
public class FernandoCursor extends AbstractCursor
{
    private double thickness;
    private double length;
    private Color color;

    private static double PROP = 2.5;

    private SIRectangle hRect;
    private SIRectangle vRect;
    private SIRectangle hRectLeft;
    private SIRectangle hRectRight;
    private SIRectangle vRectUp;
    private SIRectangle vRectDown;
    private double displace;

    public FernandoCursor(VirtualSpace targetSpace){
        this(targetSpace, 5, 50, Color.RED);
    }

    public FernandoCursor(VirtualSpace targetSpace, double thickness, double length){
        this(targetSpace, thickness, length, Color.RED);
    }

    public FernandoCursor(VirtualSpace targetSpace, double thickness, double length, Color color){
        this(targetSpace, thickness, length, Color.RED, Color.WHITE, 5);
    }

    public FernandoCursor(VirtualSpace targetSpace, double thickness, double length, Color color, Color borderColor, float borderWidth){

        super(targetSpace);
        this.thickness = thickness;
        this.length = length;
        this.color = color;

        double centerSize = length/PROP;
        double otherSize = (length - centerSize)/2;
        displace = centerSize/2+otherSize/2-1;

        hRect = new SIRectangle(xPos, yPos, 0, centerSize, 1, color);
        hRect.setDrawBorder(false);
        hRectLeft = new SIRectangle(xPos-displace, yPos, 0, otherSize, thickness, color);
        hRectLeft.setDrawBorder(false);
        hRectRight = new SIRectangle(xPos+displace, yPos, 0, otherSize, thickness, color);
        hRectRight.setDrawBorder(false);
        vRect = new SIRectangle(xPos, yPos, 0, 1, centerSize, color);
        vRect.setDrawBorder(false);
        vRectUp = new SIRectangle(xPos, yPos-displace, 0, thickness, otherSize, color);
        vRectUp.setDrawBorder(false);
        vRectDown = new SIRectangle(xPos, yPos+displace, 0, thickness, otherSize, color);
        vRectDown.setDrawBorder(false);
        targetSpace.addGlyph(hRect);
        targetSpace.addGlyph(vRect);
        targetSpace.addGlyph(hRectLeft);
        targetSpace.addGlyph(vRectUp);
        targetSpace.addGlyph(hRectRight);
        targetSpace.addGlyph(vRectDown);
    }

    @Override
    public void dispose(){
        targetSpace.removeGlyph(hRect);
        targetSpace.removeGlyph(vRect);
        targetSpace.removeGlyph(hRectLeft);
        targetSpace.removeGlyph(hRectRight);
        targetSpace.removeGlyph(vRectUp);
        targetSpace.removeGlyph(vRectDown);
    }

    @Override
    public void moveTo(double x, double y){
        super.moveTo(x, y);
        hRect.moveTo(x,y);
        hRectLeft.moveTo(x-displace, y);
        hRectRight.moveTo(x+displace, y);
        vRect.moveTo(x,y);
        vRectUp.moveTo(x, y-displace);
        vRectDown.moveTo(x, y+displace);   
    }
    
    @Override
    public void move(double x, double y){
        super.move(x, y);
        hRect.move(x, y);
        hRectLeft.move(x, y);
        hRectRight.move(x, y);
        vRect.move(x, y);
        vRectUp.move(x, y);
        vRectDown.move(x, y);
    }

    @Override
    public void setVisible(boolean v) {
        super.setVisible(v);
        hRect.setVisible(v); vRect.setVisible(v); hRectLeft.setVisible(v);
        hRectRight.setVisible(v); vRectUp.setVisible(v); vRectDown.setVisible(v);
    }


    @Override
    public double[] getBounds(){
        double[] hRectBounds = hRect.getBounds();
        double[] vRectBounds = vRect.getBounds();
        double[] bounds = new double[4];
        bounds[0] = hRectBounds[0];
        bounds[1] = vRectBounds[1];
        bounds[2] = hRectBounds[2];
        bounds[3] = vRectBounds[3];
        return bounds;
    }
}
