/*
 *   AUTHOR:    Olivier Chapuis <chapuis@lri.fr>
 *   Copyright (c) CNRS, 2015. All Rights Reserved
 *   Licensed under the GNU GPL.
 *
 */
package fr.inria.ilda.fitsow;

import java.awt.Color;

import java.util.Observable;
import java.util.Observer; 

import fr.inria.ilda.fitsow.walltouch.TuioTouch;
import fr.inria.ilda.fitsow.walltouch.IldaEvent;

class WallTouchManager implements Observer
{

double _displayWidth, _displayHeight;
TuioTouch _tuioTouch;

FITSOW application;
CursorManager cursorManager;

WallTouchManager(FITSOW app, CursorManager cm)
{
	application = app;
	cursorManager = cm;
	_displayWidth = app.getDisplayWidth();
	_displayHeight = app.getDisplayHeight();

	_tuioTouch = new TuioTouch(
		(double)_displayWidth/(double)_displayHeight,
		0.003,  // dragT
		0.003, // pinchT
		0.0   // clusterT
		);
	_tuioTouch.addObserver(this);

	cursorManager.registerDevice(this,"WallTouch");
}

double prevMoveX = 0, prevMoveY = 0;
double prevPinchD = 0;
int cursorCount = 0;
int contacts_lim = 4;

public void update(Observable obj, Object arg)
{
	//System.out.println("TuioTouchManager");
    if (!(arg instanceof IldaEvent.Base)) return;

	IldaEvent.Base e = (IldaEvent.Base)arg;
 
	switch(e.type)
	{
		case IldaEvent.START_MOVE:
		{
			IldaEvent.StartMove ee = (IldaEvent.StartMove)e;
			System.out.println("StartMove " + ee.x +" "+ ee.y);
			// cursorManager.createTouchCursor(this, 0, ee.x, ee.y, Color.YELLOW);
			cursorManager.createCursor(this, 0, ee.x, ee.y, Color.YELLOW);
			//int mm = InputManager.IM_DRAG_DM_MODE_DRAG_VIEW;
			//if (ee.contacts >= contacts_lim) { mm = InputManager.IM_DRAG_DM_MODE_MOVE_DM; }
			cursorManager.startDrag(this, 0); // mm);
			prevMoveX = ee.x; prevMoveY = ee.y;
			break;
		}
		case IldaEvent.MOVE:
		{
			IldaEvent.Move ee = (IldaEvent.Move)e;
			double dx = (ee.x - prevMoveX);
			double dy = (ee.y - prevMoveY);
			//int mm = InputManager.IM_DRAG_DM_MODE_DRAG_VIEW;
			//if (ee.contacts >= contacts_lim) { mm = InputManager.IM_DRAG_DM_MODE_MOVE_DM; }
			//cursorManager.moveCursor(this, 0, dx, dy);
			cursorManager.moveCursorTo(this, 0, ee.x, ee.y);
			cursorManager.drag(this, 0, dx, dy, ee.contacts); //, mm);
			prevMoveX = ee.x; prevMoveY = ee.y;
			break;
		}
		case IldaEvent.END_MOVE:
		{
			IldaEvent.EndMove ee = (IldaEvent.EndMove)e;
			//int mm = InputManager.IM_DRAG_DM_MODE_DRAG_VIEW;
			//if (ee.contacts >= contacts_lim) { mm = InputManager.IM_DRAG_DM_MODE_MOVE_DM; }
			cursorManager.endDrag(this, 0); // mm);
			cursorManager.removeCursor(this, 0);
			System.out.println("EndMove " + ee.x +" "+ ee.y);
			break;
		}
		case IldaEvent.START_PINCH:
		{
			IldaEvent.StartPinch ee = (IldaEvent.StartPinch)e;
			prevPinchD = ee.d; // prevPinchA = ee.a;
			//cursorManager.createTouchCursor(this, 0, ee.cx, ee.cy, Color.ORANGE);
			cursorManager.createCursor(this, 0, ee.cx, ee.cy, Color.ORANGE);
			cursorManager.startZoom(this, 0);
			System.out.println("StartPinch " + ee.d);
			break;
		}
		case IldaEvent.PINCH:
		{
			IldaEvent.Pinch ee = (IldaEvent.Pinch)e;
			//System.out.println("SPinch " + ee.d);
			if (ee.d != 0)
			{
				double f = prevPinchD/ee.d;  // FIXME aspect ratio ???
				cursorManager.zoom(this, 0, f, ee.cx, ee.cy);
			}
			prevPinchD = ee.d;
			break;
		}
		case IldaEvent.END_PINCH:
		{
			IldaEvent.EndPinch ee = (IldaEvent.EndPinch)e;
			cursorManager.endZoom(this, 0);
			cursorManager.removeCursor(this, 0);
			System.out.println("EndPinch ");
			break;
		}
		case IldaEvent.SIMPLE_TAP:
		{
			IldaEvent.SimpleTap ee = (IldaEvent.SimpleTap)e;
			System.out.println("SimpleTap");
			//cursorManager.createTouchCursor(this, 0, ee.x, ee.y, Color.BLUE);
			cursorManager.createCursor(this, 0, ee.x, ee.y, Color.BLUE);
			//cursorManager.tap(this, 0, ee.x, ee.y, ee.contacts);
			cursorManager.removeCursor(this, 0);
			break;
		}
	}
}

}