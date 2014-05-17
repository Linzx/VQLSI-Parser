package cn.edu.nju.seg.uml.sketch;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.seg.uml.recognize.Recognize;

import android.graphics.PointF;

/**
 * @author Zhang Xuelin
 * 
 * @date 2014-3-11 ÏÂÎç1:58:39
 */
public class Triangle {

	private PointF topCorner = null;
	private PointF leftBottomCorner = null;
	private PointF rightBottomCorner = null;

	public Triangle(PointF topCorner, PointF leftBottomCorner,
			PointF rightBottomCorner) {
		super();
		this.topCorner = topCorner;
		this.leftBottomCorner = leftBottomCorner;
		this.rightBottomCorner = rightBottomCorner;
	}

	public Triangle(List<PointF> points) {
		this.topCorner = points.get(0);
		this.leftBottomCorner = points.get(1);
		this.rightBottomCorner = points.get(2);
	}

	public List<PointF> getCorners() {
		List<PointF> points = new ArrayList<PointF>();
		points.add(leftBottomCorner);
		points.add(rightBottomCorner);
		points.add(topCorner);
		return points;
	}

	public PointF getTopCorner() {
		return topCorner;
	}

	public void setTopCorner(PointF topCorner) {
		this.topCorner = topCorner;
	}

	public PointF getLeftBottomCorner() {
		return leftBottomCorner;
	}

	public void setLeftBottomCorner(PointF leftBottomCorner) {
		this.leftBottomCorner = leftBottomCorner;
	}

	public PointF getRightBottomCorner() {
		return rightBottomCorner;
	}

	public void setRightBottomCorner(PointF rightBottomCorner) {
		this.rightBottomCorner = rightBottomCorner;
	}

	public List<ULine> getLines() {
		List<ULine> lines = new ArrayList<ULine>();
		lines.add(new ULine(topCorner, leftBottomCorner));
		lines.add(new ULine(topCorner, rightBottomCorner));
		lines.add(new ULine(leftBottomCorner, rightBottomCorner));
		return lines;
	}

	public PointF getThirdPoint(ULine ul) {
		PointF pf1 = ul.getStartPointF();
		PointF pf2 = ul.getEndPointF();

		if (Recognize.isEqual(pf1, topCorner)) {
			if(Recognize.isEqual(pf2, leftBottomCorner))
			{
				return rightBottomCorner;
			}
			else if(Recognize.isEqual(pf2, rightBottomCorner))
			{
				return leftBottomCorner;
			}
			else
				return null;
		} else if (Recognize.isEqual(pf1, leftBottomCorner)) {
			if(Recognize.isEqual(pf2, topCorner))
			{
				return rightBottomCorner;
			}
			else if(Recognize.isEqual(pf2, rightBottomCorner))
			{
				return topCorner;
			}
			else
				return null;
		} else if(Recognize.isEqual(pf1, rightBottomCorner))
		{
			if(Recognize.isEqual(pf2, topCorner))
			{
				return leftBottomCorner;
			}
			else if(Recognize.isEqual(pf2, leftBottomCorner))
			{
				return topCorner;
			}
			else
				return null;
		}
		else 
			return null;
	}

}
