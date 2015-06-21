/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 */

package fr.inria.zvtm.cluster;

import java.awt.geom.Point2D;
import java.awt.Color;

import java.net.URL;

import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.zvtm.glyphs.Glyph;

public aspect FitsGlyphCreation {

    @Override public GlyphReplicator JSkyFitsImage.getReplicator(){
        return new JSkyFitsImageReplicator(this);
    }

    private static class JSkyFitsImageReplicator extends GlyphCreation.ClosedShapeReplicator {
        private final URL imageLocation;
        private final double scaleFactor;

        JSkyFitsImageReplicator(JSkyFitsImage source){
            super(source);
            this.scaleFactor = source.scaleFactor;
            this.imageLocation = source.getFITSImageURL();
        }

        Glyph doCreateGlyph(){
            try{
                return new JSkyFitsImage(0, 0, 0, imageLocation, scaleFactor, 1);
            } catch(Exception e){
                System.out.println("JSkyFitsGlyphCreation - doCreateGlyph - catch");
                throw new Error(e);
            }
        }
    }

}
