/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;
import fr.inria.zvtm.glyphs.VRectangle;

import java.awt.Color;
import java.awt.geom.Point2D;

import java.io.IOException;

import java.util.List;
import java.util.Vector;

import fr.inria.zvtm.engine.VirtualSpaceManager;
import fr.inria.zvtm.engine.SwingWorker;

import fr.inria.zvtm.animation.Animation;
import fr.inria.zvtm.animation.AnimationManager;
import fr.inria.zvtm.animation.EndAction;
import fr.inria.zvtm.animation.interpolation.IdentityInterpolator;

import fr.inria.zvtm.glyphs.VText;
import fr.inria.zvtm.glyphs.VCross;
import fr.inria.zvtm.glyphs.VCircle;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zvtm.glyphs.Glyph;

import fr.inria.ilda.simbad.AstroObject;
import fr.inria.ilda.simbad.SimbadCatQuery;
import fr.inria.ilda.simbad.SimbadResults;

import jsky.coords.WorldCoords;

public class SimbadQuery {

    FITSOW app;

    AnimationManager am = VirtualSpaceManager.INSTANCE.getAnimationManager();
    JSkyFitsImage centerImg, onCircleImg;

    Point2D.Double queryRegionCenter;
    VCircle queryRegionG = new VCircle(0, 0, Config.Z_QUERY_REGION, 1,
                                       Color.BLACK, Config.QUERY_REGION_COLOR, Config.QUERY_REGION_ALPHA);
    // VText queryRegionLb = new VText(0, 0, Config.Z_QUERY_REGION,
    //                                 Config.QUERY_REGION_COLOR, EMPTY_STRING, VText.TEXT_ANCHOR_MIDDLE);

    public SimbadQuery(FITSOW app){
        this.app = app;
    }


    void setCenter(Point2D.Double p, JSkyFitsImage cImg){
        if (cImg == null){return;}
        this.centerImg = cImg;
        app.dSpace.addGlyph(queryRegionG);
        // app.dSpace.addGlyph(queryRegionLb);
        queryRegionG.setVisible(false);
        // queryRegionLb.setVisible(false);
        // queryRegionLb.setScaleIndependent(true);
        queryRegionG.sizeTo(1);
        queryRegionCenter = p;
        queryRegionG.moveTo(queryRegionCenter.x, queryRegionCenter.y);
        // queryRegionLb.moveTo(queryRegionCenter.x, queryRegionCenter.y + Config.QUERY_REGION_LB_OFFSET * queryRegionG.getSize());
        queryRegionG.setVisible(true);
        // queryRegionLb.setVisible(true);

    }

    void setRadius(Point2D.Double onCircle){
        queryRegionG.sizeTo(2*Math.sqrt((onCircle.x-queryRegionCenter.x)*(onCircle.x-queryRegionCenter.x) + (onCircle.y-queryRegionCenter.y)*(onCircle.y-queryRegionCenter.y)));
        // queryRegionLb.setText();
        // queryRegionLb.moveTo(queryRegionG.vx, queryRegionG.y + Config.QUERY_REGION_LB_OFFSET * queryRegionG.getSize());
    }

    void querySimbad(Point2D.Double onCircle, final JSkyFitsImage ocImg){
        this.onCircleImg = ocImg;
        if (centerImg == null || onCircleImg == null){return;}
        Point2D.Double centerWCS = centerImg.vs2wcs(queryRegionCenter.x, queryRegionCenter.y);
        Point2D.Double onCircleWCS = onCircleImg.vs2wcs(onCircle.x, onCircle.y);
        if (centerWCS == null || onCircleWCS == null){
            String queryInfo = "Invalid query";
            app.scene.setStatusBarMessage(queryInfo);
            fadeOutQueryRegion();
            return;
        }
        //compute radius in arcmin
        final WorldCoords wc = new WorldCoords(centerWCS.getX(), centerWCS.getY());
        WorldCoords wcDummy = new WorldCoords(onCircleWCS.getX(), onCircleWCS.getY());
        final double distArcMin = wc.dist(wcDummy);
        //perform catalog query
        String queryInfo = "Querying Simbad at " + wc + " with a radius of " + Config.ARCMIN_FORMATTER.format(distArcMin) + " arcminutes";
        app.scene.setStatusBarMessage(queryInfo);
        // symbolSpace.removeAllGlyphs();
        new SwingWorker(){
            @Override public List<AstroObject> construct(){
                List<AstroObject> objs = null;
                try{
                    objs = SimbadCatQuery.makeSimbadCoordQuery(wc.getRaDeg(), wc.getDecDeg(), distArcMin);
                } catch(IOException ioe){
                    ioe.printStackTrace();
                } finally {
                    return objs;
                }
            }
            @Override public void finished(){
                List<AstroObject> objs = (List<AstroObject>)get();
                displayQueryResults(objs, centerImg);
                fadeOutQueryRegion();
            }
        }.start();
    }

    void displayQueryResults(List<AstroObject> objs, JSkyFitsImage img){
      try{
        clearQueryResults();
        for(AstroObject obj: objs){
          Point2D.Double p = img.wcs2vs(obj.getRa(), obj.getDec());
          VCross cr = new VCross(p.x, p.y, Config.Z_ASTRO_OBJ_CR, 10, 10,
                                 Config.SIMBAD_AO_COLOR, Color.WHITE, .8f);
          VText lb = new VText(p.x+10, p.y+10, Config.Z_ASTRO_OBJ_LB,
                               Config.SIMBAD_AO_LBCOLOR, obj.getIdentifier(),
                               VText.TEXT_ANCHOR_START);
          app.dSpace.addGlyph(lb);
          app.dSpace.addGlyph(cr);
          cr.setStroke(Config.SIMBAD_AO_STROKE);
          lb.setFont(Config.SIMBAD_FONT);
          lb.setBorderColor(Config.SIMBAD_AO_BACKGROUND);
          lb.setTranslucencyValue(Config.SIMBAD_AO_ALPHA);
          lb.setScaleIndependent(true);
          cr.setOwner(obj);
          lb.setOwner(obj);
          img.stick(cr);
          img.stick(lb);
          cr.setType(Config.T_ASTRO_OBJ_CR);
          lb.setType(Config.T_ASTRO_OBJ_LB);
      }
      Vector<Glyph> gs = app.dSpace.getAllGlyphs();
      if(!objs.isEmpty()){
        SimbadResults results = new SimbadResults(objs, 200, 200, app.sqSpace);
      }
      }catch(NullPointerException e){
        e.printStackTrace();
      }
    }

    void clearQueryResults(){
      Vector<Glyph> toBeRemoved = app.dSpace.getGlyphsOfType(Config.T_ASTRO_OBJ_CR);
      Vector<Glyph> toBeRemoved2 = app.dSpace.getGlyphsOfType(Config.T_ASTRO_OBJ_LB);
      app.dSpace.removeGlyphs(toBeRemoved.toArray(new Glyph[toBeRemoved.size()]));
      app.dSpace.removeGlyphs(toBeRemoved2.toArray(new Glyph[toBeRemoved2.size()]));
      Vector<Glyph> toBeRemoved3 = app.sqSpace.getGlyphsOfType(Config.T_ASTRO_OBJ_SR);
      Vector<Glyph> toBeRemoved4 = app.sqSpace.getGlyphsOfType("SimbadInfo");
      app.sqSpace.removeGlyphs(toBeRemoved3.toArray(new Glyph[toBeRemoved3.size()]));
      app.sqSpace.removeGlyphs(toBeRemoved4.toArray(new Glyph[toBeRemoved4.size()]));
    }

    void fadeOutQueryRegion(){
        Animation a = am.getAnimationFactory().createTranslucencyAnim(1000,
                            queryRegionG, 0f, false, IdentityInterpolator.getInstance(),
                            new EndAction(){
                                public void execute(Object subject, Animation.Dimension dimension){
                                    queryRegionG.setTranslucencyValue(Config.QUERY_REGION_ALPHA);
                                    app.dSpace.removeGlyph(queryRegionG);
                                    app.scene.setStatusBarMessage(null);
                                }
                            });
        am.startAnimation(a, true);
    }

}
