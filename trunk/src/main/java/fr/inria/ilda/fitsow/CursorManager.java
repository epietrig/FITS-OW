/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import java.awt.Color;

import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.engine.VirtualSpace;

import fr.inria.ilda.smarties.SmartiesDeviceGestures;

//import ilda.zcsample.cursors.*;
import fr.inria.ilda.fitsow.cursors.*;

public class CursorManager {

    FITSOW app;
    Navigation nav;

    // space that holds cursors
    VirtualSpace crSpace;

    private HashMap<Object, ZcsDevice> _devices;

    CursorManager(FITSOW app){
        this.app = app;
        this.nav = app.nav;
        this.crSpace = app.crSpace;

        _devices = new HashMap<Object, ZcsDevice>();
    }

    // --------------------------
	//
	public void registerDevice(Object obj, String name){
		ZcsDevice dev = getDevice(obj, false);
		if (dev != null){
			System.out.println("CursorManager[registerDevice:]: device already registred "+ obj);
			return;
		}
		_devices.put(obj.hashCode(), new ZcsDevice(name)); 
		System.out.println("CursorManager[registerDevice]: registered "+ obj);
	}

	private ZcsDevice getDevice(Object obj){
		if (obj instanceof SmartiesDeviceGestures){
			SmartiesDeviceGestures sdg = (SmartiesDeviceGestures)obj;
			obj = sdg.getMasterID();
		}
		return getDevice(obj, true);
	}

	private ZcsDevice getDevice(Object obj, boolean warn){
		ZcsDevice dev = _devices.get((obj).hashCode());
		if (warn && dev == null){
			// WARN!
			System.out.println("CursorManager[getDevice]: Device not found!! " + obj);
		}
		return dev;
	}

	private ZcsCursor getCursor(Object obj, int id){
		return getCursor(obj, id, true);
	}

	private ZcsCursor getCursor(Object obj, int id, boolean warn){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return null; }
		ZcsCursor cur = dev.cursors.get(id);
		if (warn && cur == null){
			System.out.println("CursorManager[getCursor]: Cursor not found!! " + obj + " "+ id);
		}
		return cur;
	}

	public void createCursor(Object obj, int id, double x, double y){
		createCursor(obj, id, x, y, Color.RED);
	}

	public void createCursor(Object obj, int id, double x, double y, Color c){
		createCursor(obj, id, x, y, c, false);
	}

	public void createCursor(Object obj, int id, double x, double y, Color c, boolean hide)
	{
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = getCursor(obj, id, false);
		if (cur != null){
			System.out.println("CursorManager[createCursor]: cursor already exists "+ obj+ " "+ id);
			return;
		}
		dev.createCursor(id, x, y, c, hide);
	}

	public void removeCursor(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		cur.dispose();
		dev.deleteCursor(id);
	}

	public void hideCursor(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		cur.hide();
	}

	public void showCursor(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		cur.show();
		//app.vsm.repaint();
	}

	public void startMoveCursor(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		// do something maybe...
	}

	public void moveCursorTo(Object obj, int id, double x, double y){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		cur.moveTo(x, y);
	}

	public void moveCursor(Object obj, int id, double dx, double dy){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		cur.move(dx, dy);
	}

	public void endMoveCursor(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		// do something maybe...
	}

	public void startDrag(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		// do something maybe...
	}

	public void drag(Object obj, int id, double dx, double dy){
		drag(obj, id, dx, dy, 1);
	}
	public void drag(Object obj, int id, double dx, double dy, double speedFac){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		//ZcsCursor cur = dev.cursors.get(id);
		//if (cur == null){ return; }
		nav.directTranslate(speedFac*dx*app.getDisplayWidth(),speedFac*dy*app.getDisplayHeight());
		// CHECK
		//nav.pan(
		//	dx*app.getDisplayWidth(), dy*app.getDisplayHeight(), speedFac);
		
	}

	public void endDrag(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		// do something maybe...
	}

	public void startZoom(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		// do something maybe...
	}

	public void zoom(Object obj, int id, double f){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		double cx = 0.5, cy = 0.5;
		ZcsCursor cur = dev.cursors.get(id);
		if (cur != null){
			cx = cur.x; cy = cur.y;
		}
		//System.out.println("zoom "+ cx+" "+cy+ " "+ f);
		zoom(f, cx, cy);
	}

	public void zoom(Object obj, int id, double f, double x, double y){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		zoom(f, x, y);
	}

	private void zoom(double f, double x, double y){
		nav.centeredZoom(f, x*app.getDisplayWidth(), y*app.getDisplayHeight());

	}

	public void endZoom(Object obj, int id){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		// do something maybe...
	}

	// ---------------------------------------------------------------------------
	// ---------------------------------------------------------------------------
	public class ZcsCursor
	{
		public int id;
		public double x, y;
		public Color color;
		public OlivierCursor wc;
		//public DragMag currentDragMag = null;
		//public DragMag currentVisDragMag = null;
		//public DragMag attachedDragMag = null;
		//public DragMag attachedVisDragMag = null;
		//public DragMag linkedDragMag = null;
		//public double deltax,deltay;

		public void moveTo(double x, double y)
		{
			this.x = x; this.y = y;
			//System.out.println("move "+ x+" "+y);
			double w = app.getDisplayWidth();
			double h = app.getDisplayHeight();
			wc.moveTo(x*w - w/2.0, h/2.0 - y*h);
		}
		public void move(double dx, double dy)
		{
			x = x+dx; y = y+dy;
			wc.moveTo(x, y);
		}
		public void hide() { wc.setVisible(false); }
		public void show() {
			wc.setVisible(true);
		}
		public void dispose() { wc.dispose(); }

		public ZcsCursor(double x, double y, Color c)
		{
			this.id = id;
			this.x = x;
			this.y = y;
			this.color = c;

			wc = new OlivierCursor(
				crSpace,
				(!(app.runningOnWall())) ? 2 : 20, (!(app.runningOnWall())) ? 8 : 160,
				this.color);
			moveTo(x, y);
		}

	} // class ZcsCursor

	// ---------------------------------------------------------------------------
	// ---------------------------------------------------------------------------
	public  class ZcsDevice
	{
		
		public HashMap<Integer, ZcsCursor> cursors;

		public ZcsDevice(String name)
		{
			cursors = new HashMap();
		}

		public void createCursor(int id, double x, double y, Color c, boolean hide)
		{
			if (c == null) c = Color.RED;
				
			ZcsCursor cur = new ZcsCursor(x,y,c);
			cursors.put(id, cur);
			if (hide) { cur.hide(); }
		}

		public void createCursor(int id, double x, double y, Color c)
		{
			createCursor(id, x, y, c, false);
		}

		public void createCursor(int id, double x, double y)
		{
			createCursor(id, x, y, null, false);
		}

		public void deleteCursor(int id)
		{
			cursors.remove(id);
		}
	} // class ZcsCursor
}
