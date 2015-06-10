package fr.inria.ilda.gestures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import fr.inria.ilda.gesture.AbstractGestureEvent;
import fr.inria.ilda.gesture.IGestureEventListener;
import fr.inria.ilda.gestures.events.MTFreeCircularGesture;
import fr.inria.ilda.gestures.events.MTGestureEvent;
import fr.inria.ilda.gestures.events.MTStopGestureEvent;
import fr.inria.zvtm.engine.Java2DPainter;
import fr.inria.zvtm.engine.View;

public class RecognitionLayer implements IGestureEventListener, Java2DPainter {

	protected View mView;
	protected String gestureRecognized = "";
	protected double traceLength = 0;
	protected double traceLengthIncrement = 0;
	
	protected Point deltaMove = new Point();

	protected GestureControl gestureControl = GestureControl.NONE;

	private static Font FONT_DEBUG = new Font("Verdana", Font.PLAIN, 30);



	public RecognitionLayer(View mView) {
		this.mView = mView;
	}

	public void gestureOccured(AbstractGestureEvent event) {
//		System.out.println(""+event.getClass());
		if(event instanceof MTStopGestureEvent) {
			gestureRecognized = "";
			traceLength = 0;
			traceLengthIncrement = 0;
			deltaMove.setLocation(0, 0);
			gestureControl = GestureControl.NONE;
			mView.repaint();
		} else if(event instanceof MTGestureEvent) {
			MTGestureEvent mtEvent = (MTGestureEvent)event;
			if(mtEvent.getFingers() == 2) {
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
//				System.out.println("\t--> "+(int)traceLength);
				if(gestureControl == GestureControl.ZOOM) {
					// zoom is going on
					// check if there is a change in direction
					if(mtEvent instanceof MTFreeCircularGesture) {
						MTFreeCircularGesture mtFreeCircularEvent = (MTFreeCircularGesture)event;
						gestureRecognized = mtFreeCircularEvent.isClockwise() ? "ZOOM + " : "ZOOM - ";
					}
				} else if(gestureControl == GestureControl.PAN) {
				} else if(mtEvent instanceof MTFreeCircularGesture) {
					// zoom starts
					gestureControl = GestureControl.ZOOM;
					if(mtEvent instanceof MTFreeCircularGesture) {
						MTFreeCircularGesture mtFreeCircularEvent = (MTFreeCircularGesture)event;
						gestureRecognized = mtFreeCircularEvent.isClockwise() ? "ZOOM + " : "ZOOM - ";
					}
				} else if(traceLength > 50) {
					// pan starts
					gestureControl = GestureControl.PAN;
					gestureRecognized ="PAN ";
				}
			}
			mView.repaint();
		}
	}
	
	protected void updateView() {
		
	}

	@Override
	public void paint(Graphics2D g2d, int viewWidth, int viewHeight) {
		g2d.setFont(FONT_DEBUG);
		g2d.setColor(Color.WHITE);
		g2d.drawString(gestureRecognized, viewWidth/2, viewHeight/2);
		g2d.drawString("("+deltaMove.x+", "+deltaMove.y+") / "+traceLengthIncrement, viewWidth/2, viewHeight/2 + 40);
	}

}
