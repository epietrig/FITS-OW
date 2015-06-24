/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Timer;

import fr.inria.ilda.fitsow.cursors.CursorDwellEvent;
//import ilda.zcsample.cursors.*;
import fr.inria.ilda.fitsow.cursors.CursorDwellListener;
import fr.inria.ilda.fitsow.cursors.OlivierCursor;
import fr.inria.ilda.gestures.CardinalDirection;
import fr.inria.ilda.smarties.SmartiesDeviceGestures;
import fr.inria.zvtm.engine.VirtualSpace;

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
//		updateScaleSubMenu(dev, cur, x, y);
	}

	public void moveCursor(Object obj, int id, double dx, double dy){
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		if (cur == null){ return; }
		cur.move(dx, dy);
	}

	public void addCursorDwellListener(Object obj, int id, CursorDwellListener cursorDwellListener) {
		ZcsCursor cursor = getCursor(obj, id);
		cursor.addCursorDwellListener(cursorDwellListener);
	}

	public void removeCursorDwellListener(Object obj, int id, CursorDwellListener cursorDwellListener) {
		ZcsCursor cursor = getCursor(obj, id);
		cursor.removeCursorDwellListener(cursorDwellListener);
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

	// MENU MANAGEMENT

	public void down(Object obj, int id) {
		ZcsCursor cursor = getCursor(obj, id);
		if(cursor != null) {
			cursor.down();
		}
	}

	public void up(Object obj, int id) {
		ZcsCursor cursor = getCursor(obj, id);
		if(cursor != null) {
			cursor.up();
		}
	}

	public void displayScaleSubMenu(Object obj, int id){ 
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		cur.displaySubPieMenu();
	}

	public void hideScaleSubMenu(Object obj, int id){ 
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		cur.hideSubPieMenu();
	}

	// ZOOM MANAGEMENT

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

		protected int dwellDuration = 1000; // in ms
		protected ArrayList<CursorDwellListener> cursorDwellListeners = new ArrayList<CursorDwellListener>();
		protected ActionListener dwellTimerListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireDwellEvent(new CursorDwellEvent(ZcsCursor.this));
			}
		};
		protected Timer dwellTimer = new Timer(dwellDuration, dwellTimerListener);

		protected boolean down = false;

		protected double scaleSubMenuX = -1;
		protected double scaleSubMenuY = -1;

		public void down() {
			down = true;
			if(cursorDwellListeners.size() > 0) {
				dwellTimer.start();
			}
		}

		public void up() {
			down = false;
			if(dwellTimer.isRunning()) {
				dwellTimer.stop();
			}
		}

		public void displaySubPieMenu() {
			if(app.getMenuEventHandler().subPieMenu == null) {
				System.out.println("DISPLAY SUB PIE MENU");
				app.getMenuEventHandler().displayScaleSubMenu(new Point2D.Double(x*app.getDisplayWidth() - app.getDisplayWidth()/2, -y*app.getDisplayHeight() + app.getDisplayHeight()/2));
				scaleSubMenuX = x;
				scaleSubMenuY = y;
			}
		}

		public void hideSubPieMenu() {
			if(app.getMenuEventHandler().subPieMenu != null) {
				app.getMenuEventHandler().hideSubPieMenu();
				System.out.println("HIDE SUB PIE MENU");
				scaleSubMenuX = -1;
				scaleSubMenuY = -1;
			}
		}

		public void addCursorDwellListener(CursorDwellListener cursorDwellListener) {
			cursorDwellListeners.add(cursorDwellListener);
			if(!dwellTimer.isRunning()) {
				dwellTimer.start();
			}
		}

		public void removeCursorDwellListener(CursorDwellListener cursorDwellListener) {
			cursorDwellListeners.remove(cursorDwellListener);
			if(cursorDwellListeners.size() == 0 && dwellTimer.isRunning()) {
				dwellTimer.start();
			}
		}

		// TODO synchronize iteration to avoid concurrent modifications
		protected void fireDwellEvent(CursorDwellEvent cursorDwellEvent) {
			for (Iterator<CursorDwellListener> iterator = cursorDwellListeners.iterator(); iterator.hasNext();) {
				CursorDwellListener cursorDwellListener = iterator.next();
				cursorDwellListener.cursorDwelled(cursorDwellEvent);
			}
		}

		public void moveTo(double x, double y) {
			if(dwellTimer.isRunning()) {
				dwellTimer.stop();
			}
			if(cursorDwellListeners.size() > 0) {
				dwellTimer.restart();
			}

			this.x = x; this.y = y;
			//System.out.println("move "+ x+" "+y);
			double w = app.getDisplayWidth();
			double h = app.getDisplayHeight();
			wc.moveTo(x*w - w/2.0, h/2.0 - y*h);

			if(!(scaleSubMenuX == -1 && scaleSubMenuY == -1)) {
				updateScaleSubMenu();
			}
		}

		public void move(double dx, double dy) {
			if(dwellTimer.isRunning()) {
				dwellTimer.stop();
			}
			if(cursorDwellListeners.size() > 0) {
				dwellTimer.restart();
			}
			
			x = x+dx; y = y+dy;
			wc.moveTo(x, y);
			
			if(!(scaleSubMenuX == -1 && scaleSubMenuY == -1)) {
//				app.scene.app.zfSpacePicker.pickOnTop(v)
				
				updateScaleSubMenu();
			}
		}

		public void updateScaleSubMenu() { 
			CardinalDirection direction = fr.inria.ilda.gestures.Utils.cardinalDirection(scaleSubMenuX, scaleSubMenuY, x, y);
			System.out.println("update PIE MENU to direction "+direction);
			switch(direction) {
			case NORTH : 
				//				if(!app.getScene().getCurrentScale(null).equals(Config.SCALE_HISTEQ)) {
				app.getScene().setScale(null, Config.SCALE_HISTEQ);
				System.out.println("\t --> "+Config.SCALE_HISTEQ);
				app.getMenuEventHandler().unhighlightAllScalePieMenuItems();
				app.getMenuEventHandler().getScalePieMenuGlyphByScaleType(Config.SCALE_HISTEQ).highlight(true, null);
				//				}
				break;
			case SOUTH : 
				//				if(!app.getScene().getCurrentScale(null).equals(Config.SCALE_SQRT)) {
				app.getScene().setScale(null, Config.SCALE_SQRT);
				System.out.println("\t --> "+Config.SCALE_SQRT);
				app.getMenuEventHandler().unhighlightAllScalePieMenuItems();
				app.getMenuEventHandler().getScalePieMenuGlyphByScaleType(Config.SCALE_SQRT).highlight(true, null);
				//				}
				break;
			case WEST :
				//				if(!app.getScene().getCurrentScale(null).equals(Config.SCALE_LINEAR)) {
				app.getScene().setScale(null, Config.SCALE_LINEAR);
				System.out.println("\t --> "+Config.SCALE_LINEAR);
				app.getMenuEventHandler().unhighlightAllScalePieMenuItems();
				app.getMenuEventHandler().getScalePieMenuGlyphByScaleType(Config.SCALE_LINEAR).highlight(true, null);
				//				}
				break;
			case EAST : 
				//				if(!app.getScene().getCurrentScale(null).equals(Config.SCALE_LOG)) {
				app.getScene().setScale(null, Config.SCALE_LOG);
				System.out.println("\t --> "+Config.SCALE_LOG);
				app.getMenuEventHandler().unhighlightAllScalePieMenuItems();
				app.getMenuEventHandler().getScalePieMenuGlyphByScaleType(Config.SCALE_LOG).highlight(true, null);
				//				}
				break;
			default :
				break;
			}
		}

		public void hide() { 
			wc.setVisible(false); 
		}

		public void show() {
			wc.setVisible(true);
		}
		public void dispose() { wc.dispose(); }

		public ZcsCursor(double x, double y, Color c) {
			this.x = x;
			this.y = y;
			this.color = c;
			this.dwellTimer.setRepeats(false);

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
