package fr.inria.ilda.fitsow;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

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
import fr.inria.ilda.simbad.SimbadResults;
import fr.inria.ilda.simbad.SimbadInfo;
import fr.inria.ilda.simbad.SimbadCriteria;
import fr.inria.ilda.simbad.SimbadQueryTypeSelector;
import fr.inria.ilda.simbad.SimbadQueryGlyph;
import fr.inria.ilda.simbad.SimbadClearQuery;
import fr.inria.ilda.simbad.Tabs;
import fr.inria.ilda.fitsow.cursors.OlivierCursor;

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

	boolean dragging = false;
	boolean circularSelection = false;
	// boolean draggingFITS = false;
	// boolean draggingSimbadResults = false;
	// boolean draggingSimbadInfo = false;
	// boolean draggingSimbadCriteria = false;
	// boolean draggingSimbadQTS = false;
	// boolean draggingSimbadCQ = false;
	int lastJPX, lastJPY;

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
    SmartiesWidget sw3 = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Query Menu", 9,2,4,1);
		sw3.handler = new QueryMenuHandler();
		SmartiesWidget text = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_INCTEXT_BUTTON , "Input Text", 13,2,4,1);
		text.handler = new TextMenuHandler();
		SmartiesWidget dragg = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_TOGGLE_BUTTON , "Dragg", 1,2,4,1);
		dragg.handler = new DraggHandler();
		SmartiesWidget circularSelection = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_TOGGLE_BUTTON , "Draw circle", 5,2,4,1);
		circularSelection.handler = new CircularSelectionHandler();
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

	public int[] jCoords(SmartiesEvent se){
		OlivierCursor oc = inputManager.getCursor(this, se.id).wc;
		Point res = new Point();
		double[] bounds = oc.getBounds();
		double x = bounds[2] - bounds[0];
		double y = bounds[1] - bounds[3];
		application.mView.fromVSToPanelCoordinates(oc.getLocation().getX(),oc.getLocation().getY(),application.crCamera,res);
		int[] jcoords = {(int) res.getX(),(int) res.getY()};
		return jcoords;
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
					this, se.p.id, se.p.x, se.p.y, SmartiesColors.getPuckColorById(se.p.id));
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
			if (se.p != null){
				if (se.mode == SmartiesEvent.SMARTIE_GESTUREMOD_DRAG){
					inputManager.startCircularSelection(this, se.id);
				}
				int[] jCoords = jCoords(se);
				int jpx = jCoords[0];
				int jpy = jCoords[1];
				if(dragging){
					application.eh.lastJPX=jpx;
					application.eh.lastJPY=jpy;
					application.eh.startDragging(jpx, jpy);
				}
				else if(circularSelection && application.eh.querying &&
				!application.eh.insideSimbadCriteria(jpx, jpy) &&
				!application.eh.insideSimbadQueryTypeSelector(jpx, jpy)){
					Point2D.Double res = new Point2D.Double();
					application.mView.fromPanelToVSCoordinates(jpx, jpy, application.dCamera,res);
					System.out.println("start circle x: "+res.getX()+" ,y : "+res.getY());
					application.eh.startCircularSelection(res);
				}

			}
			break;
		}
		case SmartiesEvent.SMARTIE_EVENTS_TYPE_MOVE:{
			// System.out.println("SMARTIE_EVENTS_TYPE_MOVE");
			if (se.p != null){
				inputManager.moveCursorTo(this, se.id, se.p.x, se.p.y);
				if (se.mode == SmartiesEvent.SMARTIE_GESTUREMOD_DRAG){
					inputManager.resizeCircularSelection(this, se.id);
				}
				int[] jCoords = jCoords(se);
				int jpx = jCoords[0];
				int jpy = jCoords[1];
				if(dragging){
					application.eh.dragg(jpx, jpy);
				}
				else if(circularSelection && application.eh.querying && application.eh.sq != null){
					Point2D.Double res = new Point2D.Double();
					application.mView.fromPanelToVSCoordinates(jpx, jpy, application.dCamera,res);
					System.out.println("move circle x: "+res.getX()+" ,y : "+res.getY());
					application.eh.resizeCircularSelection(res, jpx, jpy);
				}
			}
            break;
        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_END_MOVE:{

						if (se.p != null){
							int[] jCoords = jCoords(se);
							int jpx = jCoords[0];
							int jpy = jCoords[1];
							if(dragging){
								System.out.println("enddragging");
								application.eh.endDragging();
							}
							else if(circularSelection && application.eh.querying &&
							!application.eh.insideSimbadQueryTypeSelector(jpx, jpy)
							&&!application.eh.insideSimbadCriteria(jpx, jpy)){
								Point2D.Double res = new Point2D.Double();
								application.mView.fromPanelToVSCoordinates(jpx, jpy, application.dCamera,res);
								application.eh.endCircularSelection(res);
							}

							if (se.mode == SmartiesEvent.SMARTIE_GESTUREMOD_DRAG){
								inputManager.endCircularSelection(this, se.id);
							}
              break;

            }

        }
        case SmartiesEvent.SMARTIE_EVENTS_TYPE_TAP:
        {		System.out.println("tap!");
						// smarties.getWidget(5).handler.callback(smarties.getWidget(5), se, this);
						// smarties.showKeyboard(se.p.id,se.device);
						// OlivierCursor oc = inputManager.getCursor(this, se.id).wc;
						// Point res = new Point();
						// application.mView.fromVSToPanelCoordinates(oc.getLocation().getX(),oc.getLocation().getY(),application.crCamera,res);
						int[] jCoords = jCoords(se);
						int jpx = jCoords[0];
						int jpy = jCoords[1];
						// (int) res.getX();
						// int jpy = (int) res.getY();
						SimbadQueryTypeSelector sqts = (SimbadQueryTypeSelector) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SQTS);
						SimbadCriteria criteria = (SimbadCriteria)SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SC);
						SimbadResults list = (SimbadResults) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SR);
						SimbadInfo info = (SimbadInfo) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_BINFO);
						SimbadClearQuery cq = (SimbadClearQuery) SimbadQueryGlyph.getCurrent(Config.T_ASTRO_OBJ_SCQ);
						if(criteria != null && criteria.coordInsideItem(jpx,jpy)){
							criteria.updateSimbadCriteriaTabs(jpx, jpy);
							criteria.updateSimbadCriteria(jpx, jpy, application);
						}
						else if(sqts != null && sqts.coordInsideItem(jpx, jpy)){
							application.eh.updateSimbadQueryTypeSelector(jpx, jpy, sqts);
						}
						else if(info != null && info.coordInsideItem(jpx, jpy)){
							application.eh.updateSimbadInfoTabs(jpx, jpy, info);
						}
						else if(list!=null && list.coordInsideItem(jpx, jpy)){
							application.eh.updateSimbadResults(jpx, jpy, list);
						}
						else if(cq!= null && cq.getClearButton().coordInsideP(jpx, jpy, application.sqCamera)){
							application.eh.updateSimbadClearQuery(jpx, jpy);
						}

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
      //   case SmartiesEvent.SMARTIE_EVENTS_TYPE_START_MFPINCH:
      //   {
      //   	if (useGM) { break;}
      //       System.out.println("SMARTIE_EVENTS_TYPE_START_MFPINCH");
      //       if (se.p != null){
      //       	myCursor cur = (myCursor)se.p.app_data;
      //       	cur.prevMFPinchD = se.d;
      //       }
      //       else{
      //       	nullCur.prevMFPinchD = se.d;
      //       }
      //       break;
      //   }
      //   case SmartiesEvent.SMARTIE_EVENTS_TYPE_MFPINCH:{
      //   	if (useGM) { break;}
      //       //System.out.println("SMARTIE_EVENTS_TYPE_MFPINCH");
      //       myCursor cur;
      //       double x,y;
      //       if (se.p != null){
      //       	cur = (myCursor)se.p.app_data;
      //       	x = se.p.x; y = se.p.y;
      //       }
      //       else{
      //       	cur = nullCur;
      //       	//x = se.x; y = se.y;
      //       	x = 0.5; y = 0.5;
      //       }
      //       if (se.d>0){
      //       	double f = cur.prevMFPinchD/se.d;
      //       	//System.out.println("zoom: "+ se.id+" "+x+" "+y+" "+cur.prevMFPinchD+" "+se.d + " "+f);
      //       	inputManager.zoom(this, se.id, f, x,y);
      //       }
      //       cur.prevMFPinchD = se.d;
      //       break;
      //   }
      //   case SmartiesEvent.SMARTIE_EVENTS_TYPE_END_MFPINCH:{
      //   	if (useGM) { break;}
      //       System.out.println("SMARTIE_EVENTS_TYPE_END_MFPINCH");
      //       break;
      //   }
			// 	case SmartiesEvent.SMARTIE_EVENTS_TYPE_MULTI_TAPS:{
      //   }
      //   case SmartiesEvent.SMARTIES_EVENTS_TYPE_LONGPRESS:{
      //       System.out.println("SMARTIES_EVENTS_TYPE_LONGPRESS");
      //       if (se.p != null && se.num_fingers == 1){
			// 	inputManager.longPress(this, se.id, se.p.x, se.p.y);
			// }
        //     break;
        // }

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

		class QueryMenuHandler implements SmartiesWidgetHandler{
				public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
					System.out.println("Query Menu");
					application.eh.enterQueryMode();
					return true;
				}
		}

		class TextMenuHandler implements SmartiesWidgetHandler{
				public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
					System.out.println("Text");
					// application.eh.enterQueryMode();
					return true;
				}
		}

		class DraggHandler implements SmartiesWidgetHandler{
				public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
					if(dragging == false) dragging = true;
					else dragging = false;

					// application.eh.enterQueryMode();
					return true;
				}
		}
		class CircularSelectionHandler implements SmartiesWidgetHandler{
				public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
					if(circularSelection == false) circularSelection = true;
					else circularSelection = false;
					return true;
				}
		}

	class EventGlobalView implements SmartiesWidgetHandler{
		public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
			System.out.println("GlobalView");
			application.getGlobalView(null);
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
