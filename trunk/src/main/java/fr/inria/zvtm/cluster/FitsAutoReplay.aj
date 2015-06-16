/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 */

package fr.inria.zvtm.cluster;

import java.awt.geom.Rectangle2D;

import fr.inria.zvtm.cluster.Identifiable;
import fr.inria.zvtm.glyphs.JSkyFitsImage;

/**
 * Add methods that should be replay by the generic Delta here.
 * See the AbstractAutoReplay aspect in ZVTM-cluster for more details.
 * @see fr.inria.zvtm.AbstractAutoReplay
 */

aspect FitsAutoReplay extends AbstractAutoReplay {
    public pointcut autoReplayMethods(Identifiable replayTarget) :
        this(replayTarget) &&
        if(replayTarget.isReplicated()) &&
        (
         execution(public void JSkyFitsImage.setColorLookupTable(String, boolean)) ||
         execution(public void JSkyFitsImage.setCutLevels(double, double, boolean)) ||
         execution(public void JSkyFitsImage.setScaleAlgorithm(JSkyFitsImage.ScaleAlgorithm, boolean)) ||
         execution(public void JSkyFitsImage.autoSetCutLevels(boolean)) ||
         execution(public void JSkyFitsImage.autoSetCutLevels(Rectangle2D.Double, boolean)) ||
         execution(public void JSkyFitsImage.updateDisplayedImage()) ||
         execution(public void JSkyFitsImage.setTranslucencyValue(float) ) ||
         execution(public void JSkyFitsImage.setVisible(boolean) ) ||
         execution(public void JSkyFitsImage.moveTo(double, double) ) ||
         execution(public void JSkyFitsImage.orientTo(double) )
        );
}
