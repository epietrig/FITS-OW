/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.engine.VirtualSpace;

public class CursorManager {

    FITSOW app;

    // space that holds cursors
    VirtualSpace crSpace;
    Camera crCamera;

    CursorManager(FITSOW app){
        this.app = app;
        this.crSpace = app.crSpace;
        this.crCamera = app.crCamera;
    }

}
