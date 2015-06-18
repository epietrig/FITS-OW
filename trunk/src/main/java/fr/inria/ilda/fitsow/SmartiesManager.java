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
	boolean gmConnected = true;

	SmartiesManager(FITSOW app, GestureManager gestureManager, int app_width, int app_height, int row, int col){

		this.application = app;
		this.nav = application.getNavigation();
	    this.smarties = new Smarties(app_width, app_height, col, row);
	    this.inputManager = app.cm;

		System.out.println("new Smarties "+app_width+" "+app_height+" "+col+" "+row);
	    
	    smarties.initWidgets(12,6);
		SmartiesWidget sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON, "Global View", 3, 4, 2, 3);
	    sw.handler = new EventGlobalView();
	        
	    countWidget++;
	    smarties.addObserver(this);

	    smarties.setRawTouchEventsConf(true);
		
	    smarties.Run();
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
				if (gmConnected) { sdg.connect(); }
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
				SmartiesDeviceGestures sdg =  devGesturesMap.get(se.device);
				if (sdg != null){ // should be always true
					sdg.down(se);
				}
				break;
			}
			case SmartiesEvent.SMARTIES_EVENTS_TYPE_RAW_MOVE:
			{
				SmartiesDeviceGestures sdg =  devGesturesMap.get(se.device);
				if (sdg != null){ // should be always true
					sdg.move(se);
				}
				break;
			}
			
			case SmartiesEvent.SMARTIES_EVENTS_TYPE_RAW_UP:
			{
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
            default:{
                 //System.out.println("OTHER: " + se.type);
                 break;
            }
        }
	}

    class EventGlobalView implements SmartiesWidgetHandler{
        public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
            System.out.println("GlobalView");
            nav.getGlobalView(null);
            return true;
        }
    }
}
