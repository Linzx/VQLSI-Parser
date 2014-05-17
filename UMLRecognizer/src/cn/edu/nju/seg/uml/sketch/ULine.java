package cn.edu.nju.seg.uml.sketch;

import cn.edu.nju.seg.uml.recognize.CornerDetect;
import android.gesture.GesturePoint;
import android.graphics.PointF;
import android.text.format.Time;

/**
 * @author Zhang Xuelin
 * 
 *         2014-2-20 ÏÂÎç2:57:41
 */

@SuppressWarnings("unused")
public class ULine {
	private GesturePoint startPoint = null;
	private GesturePoint endPoint = null;
	private float length;

	public ULine() {
	}

	public ULine(PointF sp, PointF ep) {
		this.startPoint = new GesturePoint(sp.x, sp.y, 0l);
		this.endPoint = new GesturePoint(ep.x, ep.y, 0l);
	}

	public ULine(GesturePoint sp, GesturePoint ep) {
		this.startPoint = sp;
		this.endPoint = ep;
	}

	public GesturePoint getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(GesturePoint startPoint) {
		this.startPoint = startPoint;
	}

	public GesturePoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(GesturePoint endPoint) {
		this.endPoint = endPoint;
	}

	public float getLength() {
		return CornerDetect.getDis(this.startPoint, this.endPoint);
	}

	public float getK() {
		return (this.startPoint.y - this.endPoint.y)
				/ (this.startPoint.x - this.endPoint.x);
	}

	public float getAngle() {
		return CornerDetect.getAngle(this.startPoint.x - this.endPoint.x,
				this.startPoint.y - this.endPoint.y);
	}

	@Override
	public String toString() {
		return "ULine [startPoint=(" + startPoint.x + "," + startPoint.y + ")"
				+ ", endPoint=(" + endPoint.x + "," + endPoint.y + ")" + "]";
	}
	
	public PointF getStartPointF()
	{
		return new PointF(this.startPoint.x,this.startPoint.y);
	}
	
	public PointF getEndPointF()
	{
		return new PointF(this.endPoint.x,this.endPoint.y);
	}
}
