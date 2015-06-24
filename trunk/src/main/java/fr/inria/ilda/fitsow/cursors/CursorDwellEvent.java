package fr.inria.ilda.fitsow.cursors;

import java.util.EventObject;

import fr.inria.ilda.fitsow.CursorManager.ZcsCursor;

public class CursorDwellEvent extends EventObject {
	
	public CursorDwellEvent(ZcsCursor cursorSource) {
		super(cursorSource);
	}

}
