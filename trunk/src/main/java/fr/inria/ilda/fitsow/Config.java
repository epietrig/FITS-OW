/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;
import java.awt.Font;

class Config {

    static final Color BACKGROUND_COLOR = Color.BLACK;
    static final Color CURSOR_COLOR = Color.WHITE;

    static boolean MASTER_ANTIALIASING = true;
    static boolean CLUSTER_ANTIALIASING = true;

    static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
    static final Font PIEMENU_FONT = DEFAULT_FONT;
    static final Font GLASSPANE_FONT = new Font("Arial", Font.PLAIN, 12);

    /* PIEMENU */

    static Color PIEMENU_FILL_COLOR = Color.BLACK;
    static Color PIEMENU_BORDER_COLOR = Color.WHITE;
    static Color PIEMENU_INSIDE_COLOR_ = Color.DARK_GRAY;
    static Color PIEMENU_INSIDE_COLOR = Color.LIGHT_GRAY;

}
