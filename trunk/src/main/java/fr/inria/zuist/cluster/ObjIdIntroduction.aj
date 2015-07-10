/*
 *   Copyright (c) INRIA, 2010-2013. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id: ObjIdIntroduction.aj 5175 2014-06-27 13:46:58Z epietrig $
 */

package fr.inria.zuist.cluster;

import fr.inria.zvtm.cluster.DefaultIdentifiable;

import fr.inria.zuist.engine.Level;
import fr.inria.zuist.od.ObjectDescription;
import fr.inria.zuist.engine.Region;
import fr.inria.zuist.engine.SceneManager;
import fr.inria.zuist.engine.SceneBuilder;

aspect ObjIdIntroduction {
    declare parents: SceneManager extends DefaultIdentifiable;
    declare parents: SceneBuilder extends DefaultIdentifiable;
    declare parents: Region extends DefaultIdentifiable;
    declare parents: Level extends DefaultIdentifiable;
    declare parents: ObjectDescription extends DefaultIdentifiable;
}
