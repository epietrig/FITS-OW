package fr.inria.ilda.gestures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import fr.inria.ilda.fitsow.FITSOW;
import fr.inria.ilda.gesture.AbstractGestureEvent;
import fr.inria.ilda.gesture.IGestureEventListener;
import fr.inria.ilda.gestures.events.MTAnchoredCircularGesture;
import fr.inria.ilda.gestures.events.MTCircularGesture;
import fr.inria.ilda.gestures.events.MTFreeCircularGesture;
import fr.inria.ilda.gestures.events.MTFreeExternalLinearGesture;
import fr.inria.ilda.gestures.events.MTGestureEvent;
import fr.inria.ilda.gestures.events.MTStopGestureEvent;
import fr.inria.zvtm.engine.Java2DPainter;

public class RecognitionLayer implements IGestureEventListener, Java2DPainter {

	protected boolean debug = false;

	protected FITSOW app;
	protected double traceLength = 0;
	protected double traceLengthIncrement = 0;

	protected double traceLengthLastCMSetting = 0;

	protected Point deltaMove = new Point();

	protected GestureControl gestureControl = GestureControl.NONE;

	private static Font FONT_DEBUG = new Font("Verdana", Font.PLAIN, 30);


	public static int CM_STEP = 40; 
	public static int TRACE_LENGTH_START_PAN = 50; 

	public RecognitionLayer(FITSOW app) {
		this.app = app;
	}

	//	public void gestureOccured(AbstractGestureEvent event) {
	//		//		System.out.println(""+event.getClass());
	//		if(event instanceof MTStopGestureEvent) {
	//			System.out.println("STOP");
	//			traceLength = 0;
	//			traceLengthIncrement = 0;
	//			traceLengthLastCMSetting = 0;
	//			deltaMove.setLocation(0, 0);
	//			gestureControl = GestureControl.NONE;
	//			app.getMenuEventHandler().hideColorSubMenu();
	//			app.getView().repaint();
	//		} else if(event instanceof MTGestureEvent) {
	//			MTGestureEvent mtEvent = (MTGestureEvent)event;
	//			if(mtEvent.getFingers() == 2) {
	//				ArrayList<Finger> freeFingers = ((MTRecognitionEngine)(mtEvent.getRecognizerSource())).getFreeFingersWithoutId();
	//				double previousTrace = traceLength;
	//				traceLength = 0;
	//				deltaMove.setLocation(0, 0);
	//				traceLengthIncrement = 0;
	//				if(freeFingers.size() > 0) {
	//					traceLength = freeFingers.get(0).getTraceLength();
	//					deltaMove = freeFingers.get(0).getLastMove();
	//					traceLengthIncrement = traceLength - previousTrace;
	//				}
	//				//				System.out.println("\t--> "+(int)traceLength);
	//				if(gestureControl == GestureControl.ZOOM_IN || gestureControl == GestureControl.ZOOM_OUT) {
	//					// zoom is going on
	//					// check if there is a change in direction
	//					if(mtEvent instanceof MTFreeCircularGesture) {
	//						MTFreeCircularGesture mtFreeCircularEvent = (MTFreeCircularGesture)event;
	//						gestureControl = mtFreeCircularEvent.isClockwise() ? GestureControl.ZOOM_IN : GestureControl.ZOOM_OUT;
	//					}
	//				} else if(gestureControl == GestureControl.PAN) {
	//				} else if(mtEvent instanceof MTFreeCircularGesture) {
	//					// zoom starts
	//					MTFreeCircularGesture mtFreeCircularEvent = (MTFreeCircularGesture)event;
	//					gestureControl = mtFreeCircularEvent.isClockwise() ? GestureControl.ZOOM_IN : GestureControl.ZOOM_OUT;
	//				} else if(traceLength > TRACE_LENGTH_START_PAN) {
	//					// pan starts
	//					gestureControl = GestureControl.PAN;
	//				}
	//				if(gestureControl == GestureControl.ZOOM_IN) {
	//					app.getNavigation().czoomIn(app.getZFCamera(), 2.5f, app.getZFCamera().vx, app.getZFCamera().vy);
	//				} else if(gestureControl == GestureControl.ZOOM_OUT) {
	//					app.getNavigation().czoomOut(app.getZFCamera(), 2.5f, app.getZFCamera().vx, app.getZFCamera().vy);
	//				} else if(gestureControl == GestureControl.PAN) {
	//					app.getNavigation().pan(app.getZFCamera(), deltaMove.x, deltaMove.y, 1.0);
	//				} else {
	//					app.getView().repaint();
	//				}
	//			} else if(mtEvent.getFingers() == 3) {
	//				ArrayList<Finger> freeFingers = ((MTRecognitionEngine)(mtEvent.getRecognizerSource())).getFreeFingersWithoutId();
	//				double previousTrace = traceLength;
	//				traceLength = 0;
	//				deltaMove.setLocation(0, 0);
	//				traceLengthIncrement = 0;
	//				if(freeFingers.size() > 0) {
	//					traceLength = freeFingers.get(0).getTraceLength();
	//					deltaMove = freeFingers.get(0).getLastMove();
	//					traceLengthIncrement = traceLength - previousTrace;
	//				}
	//				if(gestureControl == GestureControl.NEXT_COLOR_MAPPING || gestureControl == GestureControl.PREV_COLOR_MAPPING) {
	//					// color mapping setting is going on
	//					// check if there is a change in direction
	//					if(mtEvent instanceof MTFreeCircularGesture) {
	//						MTFreeCircularGesture mtFreeCircularEvent = (MTFreeCircularGesture)event;
	//						gestureControl = mtFreeCircularEvent.isClockwise() ? GestureControl.NEXT_COLOR_MAPPING : GestureControl.PREV_COLOR_MAPPING;
	//					}
	//				} else if(mtEvent instanceof MTFreeCircularGesture) {
	//					// color mapping setting starts
	//					MTFreeCircularGesture mtFreeCircularEvent = (MTFreeCircularGesture)event;
	//					gestureControl = mtFreeCircularEvent.isClockwise() ? GestureControl.NEXT_COLOR_MAPPING : GestureControl.PREV_COLOR_MAPPING;
	//					app.getMenuEventHandler().displayColorSubMenu();
	//				}
	//				if(gestureControl == GestureControl.NEXT_COLOR_MAPPING) {
	//					if((traceLength - traceLengthLastCMSetting) > CM_STEP) {
	//						String newCLT = app.getScene().selectNextColorMapping(null);
	//						app.getMenuEventHandler().updateHighlightedCLT(newCLT);
	//						traceLengthLastCMSetting = traceLength;
	//					}
	//				} else if(gestureControl == GestureControl.PREV_COLOR_MAPPING) {
	//					if((traceLength - traceLengthLastCMSetting) > CM_STEP) {
	//						String newCLT = app.getScene().selectPrevColorMapping(null);
	//						app.getMenuEventHandler().updateHighlightedCLT(newCLT);
	//						traceLengthLastCMSetting = traceLength;
	//					}
	//				} else {
	//					app.getView().repaint();
	//				}
	//			}
	//
	//		}
	//	}

	public void gestureOccured(AbstractGestureEvent event) {
		if(debug) { System.out.println(event); }
		if(event instanceof MTStopGestureEvent) {
			if(debug) { System.out.println("STOP"); }
			traceLength = 0;
			traceLengthIncrement = 0;
			traceLengthLastCMSetting = 0;
			deltaMove.setLocation(0, 0);
			gestureControl = GestureControl.NONE;
			app.getMenuEventHandler().hideColorSubMenu();
			app.getView().repaint();
		} else if(event instanceof MTGestureEvent) {
			MTGestureEvent mtEvent = (MTGestureEvent)event;
			ArrayList<Finger> freeFingers = ((MTRecognitionEngine)(mtEvent.getRecognizerSource())).getFreeFingersWithoutId();
			double previousTrace = traceLength;
			traceLength = 0;
			deltaMove.setLocation(0, 0);
			traceLengthIncrement = 0;
			if(freeFingers.size() > 0) {
				traceLength = freeFingers.get(0).getTraceLength();
				deltaMove = freeFingers.get(0).getLastMove();
				if(debug) { System.out.println("--> ("+deltaMove.x+", "+deltaMove.y+")"); }
				traceLengthIncrement = traceLength - previousTrace;
			}
			if(debug) { System.out.println("PAN "+mtEvent.getFingers()); }
			if(mtEvent.getFingers() == 2) {
				gestureControl = GestureControl.PAN;
				app.getNavigation().pan(app.getZFCamera(), deltaMove.x, deltaMove.y, 4.0);
			} else if(mtEvent.getFingers() == 3) {
				if(gestureControl == GestureControl.ZOOM_IN || gestureControl == GestureControl.ZOOM_OUT) {
					if(mtEvent instanceof MTCircularGesture) {
						gestureControl = ((MTCircularGesture)mtEvent).isClockwise() ? GestureControl.ZOOM_IN : GestureControl.ZOOM_OUT;
					}
				} else if(gestureControl == GestureControl.NEXT_COLOR_MAPPING || gestureControl == GestureControl.PREV_COLOR_MAPPING) {
					if(mtEvent instanceof MTCircularGesture) {
						gestureControl = ((MTCircularGesture)mtEvent).isClockwise() ? GestureControl.NEXT_COLOR_MAPPING : GestureControl.PREV_COLOR_MAPPING;
					}
				} else {
					if(mtEvent instanceof MTAnchoredCircularGesture) {
						MTAnchoredCircularGesture mtAnchoredCircularEvent = (MTAnchoredCircularGesture)event;
						gestureControl = mtAnchoredCircularEvent.isClockwise() ? GestureControl.ZOOM_IN : GestureControl.ZOOM_OUT;
					} else if(mtEvent instanceof MTFreeCircularGesture) {
						MTFreeCircularGesture mtFreeCircularEvent = (MTFreeCircularGesture)event;
						gestureControl = mtFreeCircularEvent.isClockwise() ? GestureControl.NEXT_COLOR_MAPPING : GestureControl.PREV_COLOR_MAPPING;
						app.getMenuEventHandler().displayColorSubMenu();
					} else if(mtEvent instanceof MTFreeExternalLinearGesture) {
						MTFreeExternalLinearGesture mtFreeExtLinearEvent = (MTFreeExternalLinearGesture)event;
						System.out.println("\t\t"+mtFreeExtLinearEvent.getCardinalDirection());
					}
				}
				if(gestureControl == GestureControl.ZOOM_IN) {
					app.getNavigation().czoomIn(app.getZFCamera(), 1f, app.getZFCamera().vx, app.getZFCamera().vy);
				} else if(gestureControl == GestureControl.ZOOM_OUT) {
					app.getNavigation().czoomOut(app.getZFCamera(), 1f, app.getZFCamera().vx, app.getZFCamera().vy);
				} else if(gestureControl == GestureControl.NEXT_COLOR_MAPPING) {
					if((traceLength - traceLengthLastCMSetting) > CM_STEP) {
						String newCLT = app.getScene().selectNextColorMapping(null);
						app.getMenuEventHandler().updateHighlightedCLT(newCLT);
						traceLengthLastCMSetting = traceLength;
					}
				} else if(gestureControl == GestureControl.PREV_COLOR_MAPPING) {
					if((traceLength - traceLengthLastCMSetting) > CM_STEP) {
						String newCLT = app.getScene().selectPrevColorMapping(null);
						app.getMenuEventHandler().updateHighlightedCLT(newCLT);
						traceLengthLastCMSetting = traceLength;
					}
				}
				//				else {
				//					app.getView().repaint();
				//				}
			}
		}
	}

	@Override
	public void paint(Graphics2D g2d, int viewWidth, int viewHeight) {
		if(gestureControl != GestureControl.NONE) {
			g2d.setFont(FONT_DEBUG);
			g2d.setColor(Color.WHITE);
			g2d.drawString(""+gestureControl, viewWidth/2, viewHeight/2);
			g2d.drawString("("+deltaMove.x+", "+deltaMove.y+") / "+traceLengthIncrement, viewWidth/2, viewHeight/2 + 40);
		}
	}

}
