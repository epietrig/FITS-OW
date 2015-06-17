package fr.inria.ilda.gestures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.Timer;

import fr.inria.ilda.fitsow.Config;
import fr.inria.ilda.fitsow.FITSOW;
import fr.inria.ilda.gesture.AbstractGestureEvent;
import fr.inria.ilda.gesture.AbstractInputDevice;
import fr.inria.ilda.gesture.IGestureEventListener;
import fr.inria.ilda.gesture.InputSource;
import fr.inria.ilda.gestures.events.MTAnchoredCircularGesture;
import fr.inria.ilda.gestures.events.MTCircularGesture;
import fr.inria.ilda.gestures.events.MTFreeCircularGesture;
import fr.inria.ilda.gestures.events.MTFreeExternalLinearGesture;
import fr.inria.ilda.gestures.events.MTGestureEvent;
import fr.inria.ilda.gestures.events.MTStartGestureEvent;
import fr.inria.ilda.gestures.events.MTStopGestureEvent;
import fr.inria.zvtm.engine.Java2DPainter;

public class GestureLayer implements IGestureEventListener, Java2DPainter, ActionListener {

	public static int CM_STEP = 40; 
	private static Font FONT_DEBUG = new Font("Verdana", Font.PLAIN, 30);

	protected FITSOW app;
	protected double traceLength = 0;
	protected double traceLengthIncrement = 0;
	protected double traceLengthLastCMSetting = 0;
	protected Point deltaMove = new Point();
	protected GestureControl gestureControl = GestureControl.NONE;

	protected Timer dwellTimer5Fingers;
	protected Timer dwellTimer3Fingers;

	public GestureLayer(FITSOW app) {
		this.app = app;
		this.dwellTimer5Fingers = new Timer(1000, this);
		this.dwellTimer5Fingers.setRepeats(false);
		this.dwellTimer3Fingers = new Timer(500, this);
		this.dwellTimer3Fingers.setRepeats(false);
	}

	public void actionPerformed(ActionEvent e) {
		// Dwell
		if(e.getSource() == dwellTimer5Fingers) {
			app.getNavigation().getGlobalView(null);
		} else if(e.getSource() == dwellTimer3Fingers) {
			if(gestureControl == GestureControl.NONE) {
				gestureControl = GestureControl.SCALE_SELECTION;
				app.getMenuEventHandler().displayScaleSubMenu(new Point2D.Double(0, 0));
			}
		}
	}

	public void gestureOccured(AbstractGestureEvent event) {
		if(!(event instanceof MTGestureEvent)) {
			return;
		}
		dwellTimer5Fingers.stop();
		dwellTimer3Fingers.stop();
		MTGestureEvent mtEvent = (MTGestureEvent)event;
		if(mtEvent.getFingers() == 5) {
			dwellTimer5Fingers.restart();
		} else if(mtEvent.getFingers() == 3) {
			dwellTimer3Fingers.restart();
		}
		if((event instanceof MTStopGestureEvent) || (event instanceof MTStartGestureEvent)) {
			traceLength = 0;
			traceLengthIncrement = 0;
			traceLengthLastCMSetting = 0;
			deltaMove.setLocation(0, 0);
			gestureControl = GestureControl.NONE;
			app.getMenuEventHandler().hideColorSubMenu();
			app.getMenuEventHandler().hideSubPieMenu();
			app.getView().repaint();
		} else {
			ArrayList<Finger> freeFingers = ((MTRecognitionEngine)(mtEvent.getRecognizerSource())).getFreeFingersWithoutId();
			double previousTrace = traceLength;
			traceLength = 0;
			deltaMove.setLocation(0, 0);
			traceLengthIncrement = 0;
			if(freeFingers.size() > 0) {
				traceLength = freeFingers.get(0).getTraceLength();
				deltaMove = freeFingers.get(0).getLastMove();
				traceLengthIncrement = traceLength - previousTrace;
			}
			if(mtEvent.getFingers() == 2 && gestureControl == GestureControl.NONE) {
				app.getNavigation().pan(app.getZFCamera(), -deltaMove.x, deltaMove.y, 4.0);
			} else if(mtEvent.getFingers() == 3) {
				if(gestureControl == GestureControl.ZOOM_IN || gestureControl == GestureControl.ZOOM_OUT) {
					if(mtEvent instanceof MTCircularGesture) {
						gestureControl = ((MTCircularGesture)mtEvent).isClockwise() ? GestureControl.ZOOM_IN : GestureControl.ZOOM_OUT;
					}
				} else if(gestureControl == GestureControl.NEXT_COLOR_MAPPING || gestureControl == GestureControl.PREV_COLOR_MAPPING) {
					if(mtEvent instanceof MTCircularGesture) {
						gestureControl = ((MTCircularGesture)mtEvent).isClockwise() ? GestureControl.NEXT_COLOR_MAPPING : GestureControl.PREV_COLOR_MAPPING;
					}
				} else if(
						gestureControl == GestureControl.SCALE_SELECTION ||
						gestureControl == GestureControl.SCALE_SELECTION_NORTH ||
						gestureControl == GestureControl.SCALE_SELECTION_SOUTH ||
						gestureControl == GestureControl.SCALE_SELECTION_WEST ||
						gestureControl == GestureControl.SCALE_SELECTION_EAST
						) {
					if(mtEvent instanceof MTFreeExternalLinearGesture) {
						updateScale(((MTFreeExternalLinearGesture)mtEvent).getCardinalDirection());
					}
				} else {
					if(mtEvent instanceof MTAnchoredCircularGesture && gestureControl == GestureControl.NONE) {
						MTAnchoredCircularGesture mtAnchoredCircularEvent = (MTAnchoredCircularGesture)event;
						gestureControl = mtAnchoredCircularEvent.isClockwise() ? GestureControl.ZOOM_IN : GestureControl.ZOOM_OUT;
					} else if(mtEvent instanceof MTFreeCircularGesture && gestureControl == GestureControl.NONE) {
						MTFreeCircularGesture mtFreeCircularEvent = (MTFreeCircularGesture)event;
						gestureControl = mtFreeCircularEvent.isClockwise() ? GestureControl.NEXT_COLOR_MAPPING : GestureControl.PREV_COLOR_MAPPING;
						app.getMenuEventHandler().displayColorSubMenu();
					} 
				}
				if(gestureControl == GestureControl.ZOOM_IN) {
					// for Olivier
					InputSource inputSource = mtEvent.getInputSource();
					String inputSourceID = inputSource.getID();
					int cursorID = Integer.parseInt(inputSourceID.split("_")[0]);
					AbstractInputDevice device = inputSource.getDevice();
//					System.out.println("cursorID="+cursorID+" - device="+device.getID());
					// adapt the method call below 
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
				else {
					app.getView().repaint();
				}
			}
		}
	}

	protected void updateScale(CardinalDirection direction) {
		switch(direction) {
		case NORTH : 
			if(!(gestureControl == GestureControl.SCALE_SELECTION_NORTH)) {
				app.getScene().setScale(null, Config.SCALE_HISTEQ);
				app.getMenuEventHandler().unhighlightAllScalePieMenuItems();
				app.getMenuEventHandler().getScalePieMenuGlyphByScaleType(Config.SCALE_HISTEQ).highlight(true, null);
				gestureControl = GestureControl.SCALE_SELECTION_NORTH;
			}
			break;
		case SOUTH : 
			if(!(gestureControl == GestureControl.SCALE_SELECTION_SOUTH)) {
				app.getScene().setScale(null, Config.SCALE_SQRT);
				app.getMenuEventHandler().unhighlightAllScalePieMenuItems();
				app.getMenuEventHandler().getScalePieMenuGlyphByScaleType(Config.SCALE_SQRT).highlight(true, null);
				gestureControl = GestureControl.SCALE_SELECTION_SOUTH;
			}
			break;
		case WEST :
			if(!(gestureControl == GestureControl.SCALE_SELECTION_WEST)) {
				app.getScene().setScale(null, Config.SCALE_LINEAR);
				app.getMenuEventHandler().unhighlightAllScalePieMenuItems();
				app.getMenuEventHandler().getScalePieMenuGlyphByScaleType(Config.SCALE_LINEAR).highlight(true, null);
				gestureControl = GestureControl.SCALE_SELECTION_WEST;
			}
			break;
		case EAST : 
			if(!(gestureControl == GestureControl.SCALE_SELECTION_EAST)) {
				app.getScene().setScale(null, Config.SCALE_LOG);
				app.getMenuEventHandler().unhighlightAllScalePieMenuItems();
				app.getMenuEventHandler().getScalePieMenuGlyphByScaleType(Config.SCALE_LOG).highlight(true, null);
				gestureControl = GestureControl.SCALE_SELECTION_EAST;
			}
			break;
		default :
			break;
		}
	}

	@Override
	public void paint(Graphics2D g2d, int viewWidth, int viewHeight) {
		if(gestureControl != GestureControl.NONE) {
			g2d.setFont(FONT_DEBUG);
			g2d.setColor(Color.WHITE);
			g2d.drawString(""+gestureControl, viewWidth/2, 40);
			g2d.drawString("("+deltaMove.x+", "+deltaMove.y+") / "+traceLengthIncrement, viewWidth/2, 80);
		}
	}

}
