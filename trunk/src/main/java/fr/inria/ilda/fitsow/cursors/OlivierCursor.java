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
public class OlivierCursor extends AbstractCursor
{
    private double thickness;
    private double length;
    private double iThickness;
    private double eThickness;
    private double iWidth;
    private double eWidth;
    private double iBorderWidth;
    private double eBorderWidth;
    private double eDelta;
    private double iDelta;
    private Color color;
    private Color borderColor;

    private double[] xAppDelta = {-1, 0, +1, 0};
    private double[] yAppDelta = {0, +1, 0, -1};

    private SIRectangle[] iRects;
    private SIRectangle[] eRects;

    public OlivierCursor(VirtualSpace targetSpace){
        this(targetSpace, 5, 50, Color.RED);
    }

    public OlivierCursor(VirtualSpace targetSpace, double thickness, double length){
        this(targetSpace, thickness, length, Color.RED);
    }

    public OlivierCursor(VirtualSpace targetSpace, double thickness, double length, Color color){
        this(targetSpace, thickness, length, color, Color.WHITE, 1f);
    }

    public OlivierCursor(VirtualSpace targetSpace, double thickness, double length, Color color, Color borderColor, float borderWidth){
        this(targetSpace, thickness, length, Math.max(1,thickness/10), length*0.75,
            color, borderColor, borderWidth, Math.min(1,borderWidth));
    }

    public OlivierCursor(
        VirtualSpace targetSpace, double ethickness, double ewidth, 
        double ithickness, double iwidth, Color color, Color borderColor,
        float ebw, float ibw)
    {
        super(targetSpace);
        this.eThickness = ethickness;
        this.iThickness = ithickness;
        this.eWidth = ewidth;
        this.iWidth = iwidth;
        this.eBorderWidth = ebw;
        this.iBorderWidth = ibw;
        this.color = color;
        this.borderColor = borderColor;

        if (iBorderWidth > 0){
            iThickness = Math.max(4, iThickness);
            iBorderWidth = Math.max(1, iBorderWidth);
            iThickness = Math.max(2*iBorderWidth+1, iThickness);
        }
        if (eBorderWidth > 0){
            eThickness = Math.max(4, eThickness);
            eBorderWidth = Math.max(1, eBorderWidth);
            eThickness = Math.max(2*eBorderWidth+1, eThickness);
        }

        this.iDelta = (iWidth/2 + iThickness/2 - iBorderWidth/2); // + iBorderWidth/4);
        this.eDelta = (eWidth/2 + eBorderWidth/4 + this.iDelta);

        setupCursor();
    }

    private void setupCursor()
    {
        
        BasicStroke ebs = null;
        BasicStroke ibs = null;
        Boolean iDrawBorder = (iBorderWidth > 0);
        Boolean eDrawBorder = (eBorderWidth > 0);
       
        if (eBorderWidth > 0){
            ebs = new BasicStroke((float)eBorderWidth);
        }
        if (iBorderWidth > 0){
            ibs = new BasicStroke((float)eBorderWidth);
        }
        iRects = new SIRectangle[4];
        eRects = new SIRectangle[4];

        for (int i = 0; i < 4; i++)
        {
            iRects[i] =  new SIRectangle(
                xPos-xAppDelta[i]*iDelta, yPos-yAppDelta[i]*iDelta, 100,
                iWidth*Math.abs(xAppDelta[i]) + iThickness*Math.abs(yAppDelta[i]),
                iWidth*Math.abs(yAppDelta[i]) + iThickness*Math.abs(xAppDelta[i]),
                color, borderColor);
            if (ibs != null) { iRects[i].setStroke(ibs);}
            iRects[i].setDrawBorder(iDrawBorder);
            targetSpace.addGlyph(iRects[i]);
            eRects[i] =  new SIRectangle(
                xPos-xAppDelta[i]*eDelta, yPos-yAppDelta[i]*eDelta, 100,
                eWidth*Math.abs(xAppDelta[i]) + eThickness*Math.abs(yAppDelta[i]),
                eWidth*Math.abs(yAppDelta[i]) + eThickness*Math.abs(xAppDelta[i]),
                color, borderColor);
            targetSpace.addGlyph(eRects[i]);
            eRects[i].setDrawBorder(eDrawBorder);
            if (ebs != null) { eRects[i].setStroke(ebs);}
        }
    }

    @Override
    public void dispose(){
        for (int i = 0; i < 4; i++){
            targetSpace.removeGlyph(iRects[i]);
            targetSpace.removeGlyph(eRects[i]);
        }
    }

    @Override
    public void moveTo(double x, double y){
        super.moveTo(x, y);
        for (int i = 0; i < 4; i++){
            iRects[i].moveTo(x-xAppDelta[i]*iDelta, y-yAppDelta[i]*iDelta);
            eRects[i].moveTo(x-xAppDelta[i]*eDelta, y-yAppDelta[i]*eDelta);
        }
    }
    
    @Override
    public void move(double x, double y){
        super.move(x, y);
       for (int i = 0; i < 4; i++){
            iRects[i].moveTo(x, y);
            eRects[i].moveTo(x, y);
        }
    }

    @Override
    public void setVisible(boolean v) {
        super.setVisible(v);
        for (int i = 0; i < 4; i++){
            iRects[i].setVisible(v);
            eRects[i].setVisible(v);
        }
    }


    @Override
    public double[] getBounds(){
        double[] bounds = new double[4];
        bounds[0] = eRects[0].getBounds()[0];
        bounds[1] = eRects[1].getBounds()[1];
        bounds[2] = eRects[2].getBounds()[2];
        bounds[3] = eRects[3].getBounds()[3];
        return bounds;
    }
}
