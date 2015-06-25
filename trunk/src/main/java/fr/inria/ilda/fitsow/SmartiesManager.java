package fr.inria.ilda.fitsow;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import java.awt.Color;

import fr.inria.ilda.fitsow.cursors.CursorDwellEvent;
import fr.inria.ilda.fitsow.cursors.CursorDwellListener;
import fr.inria.ilda.gesture.GestureManager;
import fr.inria.ilda.gestures.MTRecognitionEngine;
import fr.inria.ilda.smarties.SmartiesDeviceGestures;
import fr.lri.smarties.libserver.Smarties;
import fr.lri.smarties.libserver.SmartiesColors;
import fr.lri.smarties.libserver.SmartiesDevice;
import fr.lri.smarties.libserver.SmartiesEvent;
import fr.lri.smarties.libserver.SmartiesPuck;
import fr.lri.smarties.libserver.SmartiesWidget;
import fr.lri.smarties.libserver.SmartiesWidgetHandler;

class SmartiesManager implements Observer {

	FITSOW application;
	Navigation nav;
	Smarties smarties;
	CursorManager inputManager;

	int countWidget;
	// gesture
	HashMap<SmartiesDevice, SmartiesDeviceGestures> devGesturesMap = new HashMap<SmartiesDevice, SmartiesDeviceGestures>();
	boolean useGM = true;
	myCursor nullCur;

	SmartiesPuck activePuck = null;
	int fingerCount = 0;
	long lastPuckSelectionTime = -1;

	MTRecognitionEngine mtRecognizer = null;

	SimbadQuery sq;

	SmartiesManager(FITSOW app, GestureManager gestureManager, int app_width, int app_height, int row, int col){

		this.application = app;
		this.nav = application.getNavigation();
		this.smarties = new Smarties(app_width, app_height, col, row);
		this.inputManager = app.cm;

		nullCur = new myCursor();

		System.out.println("new Smarties "+app_width+" "+app_height+" "+col+" "+row);

	    smarties.initWidgets(16,2);

		SmartiesWidget sw1 = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Global View", 1, 1, 4, 1);
		sw1.handler = new EventGlobalView();
		SmartiesWidget sw2 = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Reset Recognizer", 5, 1, 4, 1);
		sw2.handler = new EventResetRecognizer();

        sw2 = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Color Menu", 9, 1, 4, 1);
        sw2.handler = new ColorMenuHandler();

        sw2 = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_TOGGLE_BUTTON, "Wall Touch: On", 13, 1, 4, 1);
        sw2.labelOn = "Wall Touch: On";
        sw2.on = true;
        sw2.handler = new WallTouchHandler();

        //sw2 = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Scale Menu", 9, 1, 4, 1);
        //sw.handler = new ColorMenuHandler();

	    smarties.addObserver(this);
	    smarties.setRawTouchEventsConf(true);

	    smarties.Run();
	}

	class myCursor{
		double prevMFPinchD;
		double prevMFMoveX, prevMFMoveY;
		myCursor(){
			prevMFPinchD = prevMFMoveX = prevMFMoveY = 0;
		}
	}

	public void setGestureRecognizer(MTRecognitionEngine mtRecognizer) {
		this.mtRecognizer = mtRecognizer;
	}

	public void update(Observable obj, Object arg)
	{
		if (!(arg instanceof SmartiesEvent)) { return; }

		final SmartiesEvent se = (SmartiesEvent)arg;
		switch (se.type){
		case SmartiesEvent.SMARTIES_EVENTS_TYPE_NEW_DEVICE:
		{
			SmartiesDeviceGestures sdg = new SmartiesDeviceGestures(se.device, this);
			GestureManager.getInstance().registerDevice(sdg);
			if (useGM) { sdg.connect(); }
			devGesturesMap.put(se.device, sdg);
			break;
		}
		case SmartiesEvent.SMARTIES_EVENTS_TYPE_DEVICE_SIZES:
		{
			//
			SmartiesDeviceGestures sdg =  devGesturesMap.get(se.device);
			if (sdg != null){ // should be always true
				sdg.setSmartiesTouchWidthInMm(se.device.getTouchpadWidth()/se.device.getXPixelsByMM());
				sdg.setSmartiesTouchHeightInMm(se.device.getTouchpadHeight()/se.device.getYPixelsByMM());
			}
			break;
		}
		case SmartiesEvent.SMARTIES_EVENTS_TYPE_RAW_DOWN:
		{
			if (!useGM) break;
			SmartiesDeviceGestures sdg =  devGesturesMap.get(se.device);
			if (sdg != null){ // should be always true
				sdg.down(se);
			}
			fingerCount++;
			System.out.println("SMARTIES_EVENTS_TYPE_RAW_DOWN "+fingerCount);
			if(activePuck != null) {
//				double distanceInMm = Math.sqrt(
//						((se.x - activePuck.x)*sdg.getWidthInMm()) * ((se.x - activePuck.x)*sdg.getWidthInMm()) +
//						((se.y - activePuck.y)*sdg.getHeightInMm()) * ((se.y - activePuck.y)*sdg.getHeightInMm()));
//				if(distanceInMm < 10 && (System.currentTimeMillis() - lastPuckSelectionTime) >= 200) {
//					inputManager.addCursorDwellListener(this, se.id, cursorDwellListener);
//					inputManager.down(this, se.id);
//				}
				if((System.currentTimeMillis() - lastPuckSelectionTime) >= 200 && fingerCount == 1) {
					inputManager.down(this, se.id);
				} else if(fingerCount > 1) {
					inputManager.up(this, activePuck.id);
				}
			}
			break;
		}
		case SmartiesEvent.SMARTIES_EVENTS_TYPE_RAW_MOVE:
		{
			if (!useGM) { break; }
			SmartiesDeviceGestures sdg =  devGesturesMap.get(se.device);
			if (sdg != null){ // should be always true
				sdg.move(se);
			}
			break;
		}

		case SmartiesEvent.SMARTIES_EVENTS_TYPE_RAW_UP:
		{
			if (!useGM) { break; }
			SmartiesDeviceGestures sdg =  devGesturesMap.get(se.device);
			if (sdg != null){ // should be always true
				sdg.up(se);
			}
			if(fingerCount > 0) {
				fingerCount--;
			}
			System.out.println("SMARTIES_EVENTS_TYPE_RAW_UP "+fingerCount);
			if(activePuck != null) {
				inputManager.up(this, se.id);
			}
			break;
		}
		case SmartiesEvent.SMARTIE_EVENTS_TYPE_CREATE:{
			//System.out.println("Create Puck: " + se.id);
			inputManager.createCursor(
					this, se.id, se.p.x, se.p.y, SmartiesColors.getPuckColorById(se.id));
			se.p.app_data = new myCursor();
			break;
		}
		case SmartiesEvent.SMARTIE_EVENTS_TYPE_SELECT:{
			//System.out.println("Select Puck: " + se.id);
			//_checkWidgetState(e.device, e.p);
			lastPuckSelectionTime = System.currentTimeMillis();
			activePuck = se.p;
			break;
		}
		case SmartiesEvent.SMARTIE_EVENTS_TYPE_STORE:{
			//repaint();
			if (se.p != null){
				inputManager.hideCursor(this, se.id);
				if(activePuck == se.p) {
					activePuck = null;
				}
			}
			break;
		}
		case SmartiesEvent.SMARTIE_EVENTS_TYPE_UNSTORE:{
			if (se.p != null){
				inputManager.showCursor(this, se.id);
				activePuck = se.p;
			}
			break;
		}
		case SmartiesEvent.SMARTIE_EVENTS_TYPE_DELETE:{
			if (se.p != null){
				inputManager.removeCursor(this, se.id);
				smarties.deletePuck(se.p.id);
				if(activePuck == se.p) {
					activePuck = null;
				}
			}
			break;
		}
		case SmartiesEvent.SMARTIE_EVENTS_TYPE_START_MOVE:{
			//System.out.println("SMARTIE_EVENTS_TYPE_START_MOVE");
			if (se.p != null){
				if (se.mode == SmartiesEvent.SMARTIE_GESTUREMOD_DRAG){
					inputManager.startCircularSelection(this, se.id);
				}
			}
			break;
		}
		case SmartiesEvent.SMARTIE_EVENTS_TYPE_MOVE:{
			//System.out.println("SMARTIE_EVENTS_TYPE_MOVE");
			if (se.p != null){
				inputManager.moveCursorTo(this, se.id, se.p.x, se.p.y);
				if (se.mode == SmartiesEvent.SMARTIE_GESTUREMOD_DRAG){
					inputManager.resizeCircularSelection(this, se.id);
				}
			}
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_END_MOVE:{
            //System.out.println("SMARTIE_EVENTS_TYPE_END_MOVE");
			if (se.p != null){
				if (se.mode == SmartiesEvent.SMARTIE_GESTUREMOD_DRAG){
					inputManager.endCircularSelection(this, se.id);
				}
                break;
            }
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_TAP:
        {
            if (false){  // for emulating wall touch
                inputManager.createCursor(this, 1000, se.x, se.y, Color.BLUE);
                inputManager.tap(this, 1000, se.x, se.y, se.num_fingers);
                inputManager.removeCursor(this, 1000);
            }
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_WIDGET:{
            if (se.widget.handler != null){
                se.widget.handler.callback(se.widget, se, this);
            }
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_START_MFMOVE:{
            if (useGM) { break;}
            System.out.println("SMARTIE_EVENTS_TYPE_START_MFMOVE");
            if (se.p != null){
            	myCursor cur = (myCursor)se.p.app_data;
            	cur.prevMFMoveX = se.x;
                cur.prevMFMoveY = se.y;
            }
            else{
            	nullCur.prevMFMoveX = se.x;
                nullCur.prevMFMoveY = se.y;
            }
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_MFMOVE:{
            if (useGM) { break;}
            myCursor cur;
            double x,y;
            if (se.p != null){
            	cur = (myCursor)se.p.app_data;
            }
            else{
            	cur = nullCur;
            }
            double dx = (se.x - cur.prevMFMoveX);
            double dy = (se.y - cur.prevMFMoveY);
            inputManager.drag(this, se.id, -dx, dy, se.num_fingers);
            cur.prevMFMoveX = se.x;
            cur.prevMFMoveY = se.y;
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_END_MFMOVE:{
        	if (useGM) { break;}
            System.out.println("SMARTIE_EVENTS_TYPE_END_MFMOVE");
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_START_MFPINCH:
        {
        	if (useGM) { break;}
            System.out.println("SMARTIE_EVENTS_TYPE_START_MFPINCH");
            if (se.p != null){
            	myCursor cur = (myCursor)se.p.app_data;
            	cur.prevMFPinchD = se.d;
            }
            else{
            	nullCur.prevMFPinchD = se.d;
            }
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_MFPINCH:{
        	if (useGM) { break;}
            //System.out.println("SMARTIE_EVENTS_TYPE_MFPINCH");
            myCursor cur;
            double x,y;
            if (se.p != null){
            	cur = (myCursor)se.p.app_data;
            	x = se.p.x; y = se.p.y;
            }
            else{
            	cur = nullCur;
            	//x = se.x; y = se.y;
            	x = 0.5; y = 0.5;
            }
            if (se.d>0){
            	double f = cur.prevMFPinchD/se.d;
            	//System.out.println("zoom: "+ se.id+" "+x+" "+y+" "+cur.prevMFPinchD+" "+se.d + " "+f);
            	inputManager.zoom(this, se.id, f, x,y);
            }
            cur.prevMFPinchD = se.d;
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_END_MFPINCH:{
        	if (useGM) { break;}
            System.out.println("SMARTIE_EVENTS_TYPE_END_MFPINCH");
            break;
        }
        
        case SmartiesEvent.SMARTIES_EVENTS_TYPE_LONGPRESS:{
            System.out.println("SMARTIES_EVENTS_TYPE_LONGPRESS");
            if (se.p != null){
				inputManager.longPress(this, se.id, se.p.x, se.p.y);
			}
            break;
        }
        
        default:{
             //System.out.println("OTHER: " + se.type);
             break;
        }
        }
    }

    class WallTouchHandler implements SmartiesWidgetHandler{
        public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
            System.out.println("Wall Touch "+sw.on);
            application.wtm.setActive(sw.on);
            return true;
        }
    }

    class ColorMenuHandler implements SmartiesWidgetHandler{
        public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
            System.out.println("ColorMenu");
            application.meh.displayColorSubMenu();
            return true;
        }
    }
	class EventGlobalView implements SmartiesWidgetHandler{
		public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
			System.out.println("GlobalView");
			nav.getGlobalView(null);
			return true;
		}
	}

	class EventResetRecognizer implements SmartiesWidgetHandler{
		public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
			System.out.println("ResetRecognizer");
			if(mtRecognizer != null) {
				mtRecognizer.forceReset();
			}
			return true;
		}
	}
}
