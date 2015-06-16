package fr.inria.ilda.gestures.events;

import fr.inria.ilda.gesture.AbstractGestureEvent;
import fr.inria.ilda.gesture.InputSource;

/**
 * Created by appert on 08/03/15.
 */
public class MTGestureEvent extends AbstractGestureEvent{

	protected InputSource inputSource;
	
	protected boolean unknown = false;

	protected boolean dwell = false;
	protected boolean anchored;
	protected boolean external;

	protected int fingers = -1;

	public MTGestureEvent(InputSource inputSource, int fingers) {
		this.inputSource = inputSource;
		this.unknown = true;
		this.fingers = fingers;
		this.anchored = false;
		this.external = true;
	}

	public MTGestureEvent(InputSource inputSource, boolean dwell, int fingers) {
		this.inputSource = inputSource;
		this.unknown = false;
		this.fingers = fingers;
		this.dwell = dwell;
		this.anchored = true;
		this.external = true;
	}

	public MTGestureEvent(InputSource inputSource, boolean anchored, boolean external, int fingers) {
		this.inputSource = inputSource;
		this.unknown = false;
		this.anchored = anchored;
		this.external = external;
		this.fingers = fingers;
		// TODO what is that?
		if (this.fingers > 5)
			System.out.println("Break");
	}

	public boolean equals(Object o) {
		MTGestureEvent gc = (MTGestureEvent)o;
		boolean mainDimensions = anchored == gc.anchored &&
				external == gc.external;
		return mainDimensions;
	}

	public String toString() {
		if(unknown) {
			return "UNKNOWN";
		}
		if(dwell) {
			return "DWELL";
		}
		return "Not a known gesture";
	}

	public boolean isAnchored() {
		return anchored;
	}

	public void setAnchored(boolean anchored) {
		this.anchored = anchored;
	}

	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	public boolean isUnknown() {
		return unknown;
	}

	public void setUnknown(boolean unknown) {
		this.unknown = unknown;
	}

	public boolean isDwell() {
		return dwell;
	}

	public void setDwell(boolean dwell) {
		this.dwell = dwell;
	}

	public int getFingers() {
		return fingers;
	}

	public void setFingers(int fingers) {
		this.fingers = fingers;
	}

	public InputSource getInputSource() {
		return inputSource;
	}

	public void setInputSource(InputSource inputSource) {
		this.inputSource = inputSource;
	}


}

