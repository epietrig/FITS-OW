/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 */

package fr.inria.zvtm.cluster;

import java.awt.geom.Rectangle2D;

import fr.inria.zvtm.cluster.Identifiable;
import fr.inria.zvtm.glyphs.JSkyFitsImage;
import fr.inria.ilda.simbad.SimbadCriteria;
import fr.inria.ilda.simbad.SimbadFluxFilter;
import fr.inria.ilda.simbad.SimbadMFilter;
import fr.inria.ilda.simbad.SimbadOTypeFilter;
import fr.inria.ilda.simbad.SimbadParallaxFilter;
import fr.inria.ilda.simbad.SimbadPMFilter;
import fr.inria.ilda.simbad.SimbadRVFilter;
import fr.inria.ilda.simbad.SimbadSTFilter;
import fr.inria.ilda.simbad.SimbadQueryTypeSelector;
import fr.inria.ilda.simbad.SimbadResults;
import fr.inria.ilda.simbad.AstroObject;
import fr.inria.ilda.simbad.Tabs;

import fr.inria.zvtm.glyphs.Glyph;
import fr.inria.zvtm.glyphs.Composite;
import fr.inria.zvtm.glyphs.VRectangle;
import fr.inria.zvtm.glyphs.IcePDFPageImg;
import fr.inria.zvtm.glyphs.BrowsableDocument;

import java.util.Vector;
import java.util.List;

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
         execution(public void IcePDFPageImg.setInterpolationMethod(Object) ) ||
         execution(public void IcePDFPageImg.flush() ) ||
         execution(public void BrowsableDocument.setPage(int) ) ||
         execution(public void SimbadCriteria.updateQueryParameters(double, double) ) ||
         execution(public void SimbadCriteria.cleanQueryParameters() ) ||
         execution(public void SimbadFluxFilter.select(int, String) ) ||
         execution(public void SimbadMFilter.select(int, String) ) ||
         execution(public void SimbadOTypeFilter.select(int, String) ) ||
         execution(public void SimbadParallaxFilter.select(int, String) ) ||
         execution(public void SimbadPMFilter.select(int, String) ) ||
         execution(public void SimbadRVFilter.select(int, String) ) ||
         execution(public void SimbadSTFilter.select(int, String) ) ||
         execution(public void SimbadQueryTypeSelector.select(int) ) ||
         execution(public boolean SimbadResults.highlight(int) ) ||
         execution(public void SimbadResults.setResults(List<AstroObject>) ) ||
         execution(public void SimbadResults.highlightCorrespondingGlyph(Vector<Glyph>, int) ) ||
         execution(public void Tabs.activateBasicDataTab(VRectangle, Composite, Composite) ) ||
         execution(public void Tabs.activateMeasurementsTab(VRectangle, Composite, Composite) )
        );
}
