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
import fr.inria.zvtm.engine.PickerVS;
import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.event.PickerListener;
import fr.inria.zvtm.glyphs.Glyph;

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

	//	public void moveCursor(Object obj, int id, double dx, double dy){
	//		ZcsDevice dev = getDevice(obj);
	//		if (dev == null){ return; }
	//		ZcsCursor cur = dev.cursors.get(id);
	//		if (cur == null){ return; }
	//		cur.move(dx, dy);
	//	}

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

	public void displayMainMenu(Object obj, int id){ 
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		cur.displayMainPieMenu();
	}

	public void hideMainMenu(Object obj, int id){ 
		ZcsDevice dev = getDevice(obj);
		if (dev == null){ return; }
		ZcsCursor cur = dev.cursors.get(id);
		cur.hideMainPieMenu();
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
	public class ZcsCursor implements PickerListener {
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
		protected double mainMenuX = -1;
		protected double mainMenuY = -1;

		protected PickerVS pickerVS;
		protected Point2D.Double vsCoords = new Point2D.Double();

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

			pickerVS = new PickerVS();
			app.mnSpace.registerPicker(pickerVS);
			pickerVS.setListener(this);
		}

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
			if(app.getMenuEventHandler().mainPieMenu != null) {
				Glyph g = pickerVS.lastGlyphEntered();
				short layerToActivate = FITSOW.DATA_LAYER;
				if (g != null){
					if (g.getType() == Config.T_MPMI){
						layerToActivate = app.getMenuEventHandler().mainPieMenuEvent(g);
					}
					else if (g.getType() == Config.T_SPMISc){
						// nothing to do, command triggered when cursor entered menu item
					}
				}
				hideSubPieMenu();
				hideMainPieMenu();
				app.mView.setActiveLayer(layerToActivate);
			}
		}

		public void displayMainPieMenu() {
			if(app.getMenuEventHandler().mainPieMenu == null) {
				System.out.println("DISPLAY MAIN PIE MENU");
				app.getMenuEventHandler().displayMainPieMenu(new Point2D.Double(x*app.getDisplayWidth() - app.getDisplayWidth()/2, -y*app.getDisplayHeight() + app.getDisplayHeight()/2));
				mainMenuX = x;
				mainMenuY = y;
			}
		}

		public void hideMainPieMenu() {
			if(app.getMenuEventHandler().mainPieMenu != null) {
				app.getMenuEventHandler().hideMainPieMenu();
				System.out.println("HIDE SUB PIE MENU");
				mainMenuX = -1;
				mainMenuY = -1;
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
			if(pickerVS != null) {
				vsCoords.x = x*w - w/2.0;
				vsCoords.y = h/2.0 - y*h;
				pickerVS.setVSCoordinates(vsCoords.x, vsCoords.y);
			}

			if(app.getMenuEventHandler().subPieMenu != null) {
				pickerVS.computePickedGlyphList(app.mnCamera);
			} else if(app.getMenuEventHandler().mainPieMenu != null) {
				pickerVS.computePickedGlyphList(app.mnCamera);
			}
		}

		//		public void move(double dx, double dy) {
		//			x = x+dx; y = y+dy;
		//			wc.moveTo(x, y);
		//		}

		public void hide() { 
			wc.setVisible(false); 
		}

		public void show() {
			wc.setVisible(true);
		}
		public void dispose() { wc.dispose(); }

		@Override
		public void enterGlyph(Glyph g) {
			System.out.println("enter glyph "+g);
			if (g.getType() != null){
				if (g.getType().equals(Config.T_MPMI)){
					g.highlight(true, null);
				}
				else if (g.getType().startsWith(Config.T_SPMI)){
					g.highlight(true, null);
					if (g.getType() == Config.T_SPMISc){
						subPieMenuEvent(g);
					}
				}
				//	            else if (g.getType().equals(Config.T_CLT_BTN)){ // TODO
					//	                selectCLT((String)g.getOwner());
				//	            }
			}
			else {
				if (app.getMenuEventHandler().mainPieMenu != null && g == app.getMenuEventHandler().mainPieMenu.getBoundary()){
					app.getMenuEventHandler().mainPieMenu.setSensitivity(true);
				}
			}
		}

		@Override
		public void exitGlyph(Glyph g) {
			System.out.println("exit glyph "+g);
			if (g.getType() != null){
				if (g.getType().equals(Config.T_MPMI) || g.getType().startsWith(Config.T_SPMI)){
					// exiting a pie menu item
					g.highlight(false, null);
				}
				else if (g.getType().equals(Config.T_CLT_BTN)){
					g.highlight(false, null);
				}
			}
			else {
				if (app.getMenuEventHandler().mainPieMenu != null && g == app.getMenuEventHandler().mainPieMenu.getBoundary()){
					// crossing the main pie menu's trigger
					Glyph lge = pickerVS.lastGlyphEntered();
					if (lge != null && lge.getType() == Config.T_MPMI){
						if (app.getMenuEventHandler().displaySubPieMenu(lge, new Point2D.Double(vsCoords.x, vsCoords.y))){
							app.getMenuEventHandler().mainPieMenu.setSensitivity(false);
						}
					}
				}
				else if (app.getMenuEventHandler().subPieMenu != null && g == app.getMenuEventHandler().subPieMenu.getBoundary()){
					// crossing a sub pie menu's trigger
					// (takes back to main pie menu)
					hideSubPieMenu();
					app.getMenuEventHandler().mainPieMenu.setSensitivity(true);
				}
			}
		}

		void subPieMenuEvent(Glyph menuItem){
			int index = app.getMenuEventHandler().subPieMenu.getItemIndex(menuItem);
			if (index != -1){
				String label = app.getMenuEventHandler().subPieMenu.getLabels()[index].getText();
				if (label == app.getMenuEventHandler().SCALEPM_LOG){
					//	                app.scene.setScale(app.getMenuEventHandler().selectedFITSImage, Config.SCALE_LOG);
					app.scene.setScale(null, Config.SCALE_LOG);
				}
				else if (label == app.getMenuEventHandler().SCALEPM_LINEAR){
					app.scene.setScale(null, Config.SCALE_LINEAR);
				}
				else if (label == app.getMenuEventHandler().SCALEPM_SQRT){
					app.scene.setScale(null, Config.SCALE_SQRT);
				}
				else if (label == app.getMenuEventHandler().SCALEPM_HISTEQ){
					app.scene.setScale(null, Config.SCALE_HISTEQ);
				}
			}
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
