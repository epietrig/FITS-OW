package fr.inria.ilda.fitsow;

import java.awt.geom.Point2D;
import java.awt.Color;

import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.inria.ilda.gesture.GestureManager;
import fr.inria.ilda.smarties.SmartiesDeviceGestures;

import fr.lri.smarties.libserver.Smarties;
import fr.lri.smarties.libserver.SmartiesColors;
import fr.lri.smarties.libserver.SmartiesEvent;
import fr.lri.smarties.libserver.SmartiesPuck;
import fr.lri.smarties.libserver.SmartiesDevice;
import fr.lri.smarties.libserver.SmartiesWidget;
import fr.lri.smarties.libserver.SmartiesWidgetHandler;

import java.util.Observer;
import java.util.Observable;


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

	SmartiesManager(FITSOW app, GestureManager gestureManager, int app_width, int app_height, int row, int col){

		this.application = app;
		this.nav = application.getNavigation();
	    this.smarties = new Smarties(app_width, app_height, col, row);
	    this.inputManager = app.cm;

	    nullCur = new myCursor();

		System.out.println("new Smarties "+app_width+" "+app_height+" "+col+" "+row);
	    
	    smarties.initWidgets(12,2);

		SmartiesWidget sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Global View", 1, 1, 4, 1);
	    sw.handler = new GlobalViewHandler();

        sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Color Menu", 5, 1, 4, 1);
        sw.handler = new ColorMenuHandler();

        sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Scale Menu", 9, 1, 4, 1);
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
                    break;
            }
            case SmartiesEvent.SMARTIE_EVENTS_TYPE_STORE:{
                //repaint();
                if (se.p != null){
                	inputManager.hideCursor(this, se.id);
                }
                break;  
            }
            case SmartiesEvent.SMARTIE_EVENTS_TYPE_UNSTORE:{
                if (se.p != null){
                	inputManager.showCursor(this, se.id);
                }
                break;
            }
            case SmartiesEvent.SMARTIE_EVENTS_TYPE_DELETE:{
                if (se.p != null){
                	inputManager.removeCursor(this, se.id);
                	smarties.deletePuck(se.p.id);
                }
                break;
            }
        	case SmartiesEvent.SMARTIE_EVENTS_TYPE_START_MOVE:{
                //System.out.println("SMARTIE_EVENTS_TYPE_START_MOVE");
                if (se.p != null){
                    if (se.mode == SmartiesEvent.SMARTIE_GESTUREMOD_DRAG){
                    }
                }
                break;
            }
            case SmartiesEvent.SMARTIE_EVENTS_TYPE_MOVE:{
                //System.out.println("SMARTIE_EVENTS_TYPE_MOVE");
                if (se.p != null){
                    inputManager.moveCursorTo(this, se.id, se.p.x, se.p.y);
                    if (se.mode == SmartiesEvent.SMARTIE_GESTUREMOD_DRAG){
                    }
                }
                break;
            }
            case SmartiesEvent.SMARTIE_EVENTS_TYPE_END_MOVE:{
                //System.out.println("SMARTIE_EVENTS_TYPE_END_MOVE");
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
            default:{
                 //System.out.println("OTHER: " + se.type);
                 break;
            }
        }
	}

    class GlobalViewHandler implements SmartiesWidgetHandler{
        public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
            System.out.println("GlobalView");
            nav.getGlobalView(null);
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
}
