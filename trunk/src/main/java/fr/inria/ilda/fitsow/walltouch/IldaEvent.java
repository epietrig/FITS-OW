package fr.inria.ilda.fitsow.walltouch;


public class IldaEvent {

public static final int SIMPLE_TAP=1;
public static final int LONG_PRESS=1;

public static final int START_MOVE=2;
public static final int MOVE=3;
public static final int END_MOVE=4;

public static final int START_PINCH=5;
public static final int PINCH=6;
public static final int END_PINCH=7;

public IldaEvent() {}

public class Base {
	public int type = 0;
	public int id = -1;
	public Base() {}
	public Base(int id) {this.id = id;}
}

public class XY extends Base {
	public double x,y;
	public XY(int id, double xx, double yy) { 
		super(id);
		x = xx; y = yy;
	}
	public XY(double xx, double yy) { 
		super();
		x = xx; y = yy;
	}
}
public class SimpleTap extends XY {
	public int contacts = 1;
	public SimpleTap(int id, double xx, double yy, int c) {
		super(id, xx, yy);
		contacts = c;
		type = SIMPLE_TAP;
	}
	public SimpleTap(double xx, double yy, int c) {
		this(-1, xx, yy, c);
	}
	public SimpleTap(int id, double xx, double yy) {
		this(id, xx, yy, 1);
	}
	public SimpleTap(double xx, double yy) {
		this(-1, xx, yy, 1);
	}
}
public class LongPress extends SimpleTap {
	public LongPress(int id, double xx, double yy, int c) {
		super(id, xx, yy, c);
		type = LONG_PRESS;
	}
	public LongPress(double xx, double yy, int c) {
		this(-1, xx, yy, c);
	}
	public LongPress(int id, double xx, double yy) {
		this(id, xx, yy, 1);
	}
	public LongPress(double xx, double yy) {
		this(-1, xx, yy, 1);
	}
}

public class StartMove extends SimpleTap {
	public StartMove(int id, double xx, double yy, int c) {
		super(id, xx, yy, c);
		type = START_MOVE;
	}
	public StartMove(double xx, double yy, int c) {
		this(-1, xx, yy, c);
	}
	public StartMove(int id, double xx, double yy) {
		this(id, xx, yy, 1);
	}
	public StartMove(double xx, double yy) {
		this(-1, xx, yy, 1);
	}
}
public class Move extends SimpleTap {
	public Move(int id, double xx, double yy, int c) {
		super(id, xx, yy, c);
		type = MOVE;
	}
	public Move(double xx, double yy, int c) {
		this(-1, xx, yy, c);
	}
	public Move(int id, double xx, double yy) {
		this(id, xx, yy, 1);
	}
	public Move(double xx, double yy) {
		this(-1, xx, yy, 1);
	}
}
public class EndMove extends SimpleTap {
	public EndMove(int id, double xx, double yy, int c) {
		super(id, xx, yy, c);
		type = END_MOVE;
	}
	public EndMove(double xx, double yy, int c) {
		this(-1, xx, yy, c);
	}
	public EndMove(int id, double xx, double yy) {
		super(id, xx, yy, 1);
	}
	public EndMove(double xx, double yy) {
		super(-1, xx, yy, 1);
	}
}

public class Pinch extends Base {
	public double cx,cy,d,a;
	public Pinch(int id, double cxx, double cyy, double dd, double aa) {
		this.id = id; cx = cxx; cy = cyy; d = dd; a = aa;
		type = PINCH;
	}
	public Pinch(double cxx, double cyy, double dd, double aa) {
		cx = cxx; cy = cyy; d = dd; a = aa;
		type = PINCH;
	}
}
public class StartPinch extends Pinch {
	public StartPinch(int id, double cxx, double cyy, double dd, double aa) {
		super(id, cxx,cyy,dd,aa);
		type = START_PINCH;
	}
	public StartPinch(double cxx, double cyy, double dd, double aa) {
		super(cxx,cyy,dd,aa);
		type = START_PINCH;
	}
}
public class EndPinch extends Pinch {
	public EndPinch(int id, double cxx, double cyy, double dd, double aa) {
		super(id, cxx,cyy,dd,aa);
		type = END_PINCH;
	}
	public EndPinch(double cxx, double cyy, double dd, double aa) {
		super(cxx,cyy,dd,aa);
		type = END_PINCH;
	}
}



}