package cn.edu.nju.seg.uml.sketch;

import java.util.List;

import cn.edu.nju.seg.uml.recognize.CornerDetect;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @author Zhang Xuelin
 * 
 *         2014-3-7 ÏÂÎç4:26:04
 */
public class Rectangle extends RectF {

	private PointF leftTopCorner = null;
	private PointF rightTopCorner = null;
	private PointF rightBottomCorner = null;
	private PointF leftBottomCorner = null;

	public Rectangle() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Rectangle(float left, float top, float right, float bottom) {
		super(left, top, right, bottom);
		this.setCorner();
	}

	public Rectangle(Rect r) {
		super(r);
		// TODO Auto-generated constructor stub
	}

	public Rectangle(RectF r) {
		super(r);
		// TODO Auto-generated constructor stub
	}
	
	public void set()
	{
		if(this.leftTopCorner != null)
		{
			this.left = this.leftTopCorner.x;
			this.top = this.leftTopCorner.y;
		}
		if(this.rightBottomCorner !=null)
		{
			this.right = this.rightBottomCorner.x;
			this.bottom = this.rightBottomCorner.y;
		}
	}
	
	public void setCorner()
	{
		this.leftTopCorner = new PointF(left,top);
		this.rightBottomCorner = new PointF(right,bottom);
		this.rightTopCorner = new PointF(right,top);
		this.leftBottomCorner = new PointF(left,bottom);
		
	}
	
	public float getWidth()
	{
		return CornerDetect.getDis(leftTopCorner, rightTopCorner);
	}

	public Rectangle(PointF leftTopCorner, PointF rightTopCorner,
			PointF rightBottomCorner, PointF leftBottomCorner) {
		super();
		this.leftTopCorner = leftTopCorner;
		this.rightTopCorner = rightTopCorner;
		this.rightBottomCorner = rightBottomCorner;
		this.leftBottomCorner = leftBottomCorner;
		this.set();
	}
	
	public Rectangle(List<PointF> points)
	{
		PointF p = points.get(0);
		float l=p.x,r=p.x,t=p.y,b=p.y;
		for(PointF pf:points)
		{
			float x = pf.x;
			float y = pf.y;
			
			if(l>x)
				l = x;
			if(r<x)
				r = x;
			if(t>y)
				t = y;
			if(b<y)
				b = y;
			
		}
		this.leftTopCorner = new PointF(l,t);
		this.rightTopCorner = new PointF(r,t);
		this.rightBottomCorner = new PointF(r,b);
		this.leftBottomCorner = new PointF(l,b);
		this.set();
	}

	public PointF getLeftTopCorner() {
		return leftTopCorner;
	}

	public void setLeftTopCorner(PointF leftTopCorner) {
		this.leftTopCorner = leftTopCorner;
	}

	public PointF getRightTopCorner() {
		return rightTopCorner;
	}

	public void setRightTopCorner(PointF rightTopCorner) {
		this.rightTopCorner = rightTopCorner;
	}

	public PointF getRightBottomCorner() {
		return rightBottomCorner;
	}

	public void setRightBottomCorner(PointF rightBottomCorner) {
		this.rightBottomCorner = rightBottomCorner;
	}

	public PointF getLeftBottomCorner() {
		return leftBottomCorner;
	}

	public void setLeftBottomCorner(PointF leftBottomCorner) {
		this.leftBottomCorner = leftBottomCorner;
	}
	
	

}
