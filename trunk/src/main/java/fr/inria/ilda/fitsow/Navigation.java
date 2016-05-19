/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.geom.Point2D;

import fr.inria.zvtm.engine.VirtualSpaceManager;
import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.engine.Location;

import fr.inria.zvtm.animation.EndAction;
import fr.inria.zvtm.animation.Animation;
import fr.inria.zvtm.animation.interpolation.SlowInSlowOutInterpolator;

public class Navigation {

    /* Navigation constants */
    static final int ANIM_MOVE_DURATION = 300;
    static final short MOVE_UP = 0;
    static final short MOVE_DOWN = 1;
    static final short MOVE_LEFT = 2;
    static final short MOVE_RIGHT = 3;

    FITSOW app;
    VirtualSpaceManager vsm;

    Navigation(FITSOW app){
        this.app = app;
        vsm = VirtualSpaceManager.INSTANCE;
    }

    /* -------------- pan-zoom ------------------- */

    public void getGlobalView(EndAction ea){
        app.getGlobalView(ea);
        //vb app.sm.getGlobalView(app.zfCamera, Navigation.ANIM_MOVE_DURATION, ea);
    }

    /* Higher view */
    void getHigherView(Camera c){
        Float alt = new Float(c.getAltitude() + c.getFocal());
        Animation a = vsm.getAnimationManager().getAnimationFactory().createCameraAltAnim(
                        Navigation.ANIM_MOVE_DURATION, c, alt, true,
                        SlowInSlowOutInterpolator.getInstance(), null);
        vsm.getAnimationManager().startAnimation(a, false);
    }

    /* Lower view */
    void getLowerView(Camera c){
        Float alt = new Float(-(c.getAltitude() + c.getFocal())/2.0f);
        Animation a = vsm.getAnimationManager().getAnimationFactory().createCameraAltAnim(
                        Navigation.ANIM_MOVE_DURATION, c, alt, true,
                        SlowInSlowOutInterpolator.getInstance(), null);
        vsm.getAnimationManager().startAnimation(a, false);
    }

    /* Direction should be one of WorldExplorer.MOVE_* */
    void translateView(Camera c, short direction){
        Point2D.Double trans;
        double[] rb = c.getOwningView().getVisibleRegion(c);
        if (direction==MOVE_UP){
            double qt = (rb[1]-rb[3])/4.0;
            trans = new Point2D.Double(0,qt);
        }
        else if (direction==MOVE_DOWN){
            double qt = (rb[3]-rb[1])/4.0;
            trans = new Point2D.Double(0,qt);
        }
        else if (direction==MOVE_RIGHT){
            double qt = (rb[2]-rb[0])/4.0;
            trans = new Point2D.Double(qt,0);
        }
        else {
            // direction==MOVE_LEFT
            double qt = (rb[0]-rb[2])/4.0;
            trans = new Point2D.Double(qt,0);
        }
        Animation a = vsm.getAnimationManager().getAnimationFactory().createCameraTranslation(
                            Navigation.ANIM_MOVE_DURATION, c, trans, true,
                            SlowInSlowOutInterpolator.getInstance(), null);
        vsm.getAnimationManager().startAnimation(a, false);
    }

    /** Camera zoom-in. Called e.g. when using the mouse wheel or circular gestures.
     *@param c camera to zoom in
     *@param idfactor input device factor
     *@param zcx center of zoom x-coord (in virtual space)
     *@param zcy center of zoom y-coord (in virtual space)
     */
    public void czoomIn(Camera c, float idfactor, double zcx, double zcy){
        double a = (c.focal+Math.abs(c.altitude)) / c.focal;
        //wheelDirection == WHEEL_DOWN, zooming in
        if (c.getAltitude()-a*idfactor >= c.getZoomFloor()){
            // this test to prevent translation when camera is not actually zoming in
            c.move((zcx - c.vx) * idfactor / c.focal,
                   ((zcy - c.vy) * idfactor / c.focal));

        }
        c.altitudeOffset(-a*idfactor);
        c.getOwningView().repaint();
    }

    /** Camera zoom-out. Called e.g. when using the mouse wheel or circular gestures.
     *@param c camera to zoom in
     *@param idfactor input device factor
     *@param zcx center of zoom x-coord (in virtual space)
     *@param zcy center of zoom y-coord (in virtual space)
     */
    public void czoomOut(Camera c, float idfactor, double zcx, double zcy){
        double a = (c.focal+Math.abs(c.altitude)) / c.focal;
        // zooming out
        c.move(-((zcx - c.vx) * idfactor / c.focal),
               -((zcy - c.vy) * idfactor / c.focal));
        c.altitudeOffset(a*idfactor);
        c.getOwningView().repaint();
    }

    public void pan(Camera c, int dx, int dy, double f){
        double a = (c.focal+Math.abs(c.altitude)) / c.focal;
        synchronized(c){
            c.move(a*dx*f, a*dy*f);
            app.eh.cameraMoved(c, null, 0);
        }
    }

    // -----------------------------------------------------------
    // from zraildar

    /* x,y in (X Window) display coordinate */
    public void directTranslate(Camera c, double x, double y){
        double a = (c.focal+Math.abs(c.altitude)) / c.focal;
        Location l = c.getLocation();
        double newx = l.getX() + a*x;
        double newy = l.getY() + a*y;
        c.setLocation(new Location(newx, newy, l.getAltitude()));
    }

    void centeredZoom(Camera c, double f, double x, double y){
        Location l = c.getLocation();
        double a = (c.focal+Math.abs(c.altitude)) / c.focal;
        double newz = c.focal * a * f - c.focal;
        if (newz < 0){
            newz = 0;
            f = c.focal / (a*c.focal);
        }
        double[] r = windowToViewCoordinates(x, y, c);
        double dx = l.getX() - r[0];
        double dy = l.getY() - r[1];
        double newx = l.getX() + (f*dx - dx); // *a/(zfCamera.altitude+ zfCamera.focal));
        double newy = l.getY() + (f*dy - dy);
        c.setLocation(new Location(newx, newy, newz));
    }

    public double[] windowToViewCoordinates(double x, double y, Camera c){
        Location l = c.getLocation();
        double a = (c.focal + c.getAltitude()) / c.focal;
        //
        double xx = (long)((double)x - ((double)app.getDisplayWidth()/2.0));
        double yy = (long)(-(double)y + ((double)app.getDisplayHeight()/2.0));
        //
        xx = l.getX()+ a*xx;
        yy = l.getY()+ a*yy;
        double[] r = new double[2];
        r[0] = xx;
        r[1] = yy;
        return r;
    }

}
