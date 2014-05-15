package cn.edu.nju.seg.uml.sketch;

import java.util.List;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @author Zhang Xuelin
 * 
 * @date 2014-3-12 ÏÂÎç1:59:38
 */
public class UMLInterface extends Rectangle {

	private ULine innerLine = null;

	public UMLInterface() {
		super();
	}

	public UMLInterface(float left, float top, float right, float bottom) {
		super(left, top, right, bottom);
		adjustInnerLine();
	}

	public UMLInterface(List<PointF> points) {
		super(points);
		adjustInnerLine();
	}

	public UMLInterface(PointF leftTopCorner, PointF rightTopCorner,
			PointF rightBottomCorner, PointF leftBottomCorner) {
		super(leftTopCorner, rightTopCorner, rightBottomCorner,
				leftBottomCorner);
		adjustInnerLine();
	}

	public UMLInterface(Rect r) {
		super(r);
		adjustInnerLine();
	}

	public UMLInterface(RectF r) {
		super(r);
		adjustInnerLine();
	}

	public ULine getInnerLine() {
		return this.innerLine;
	}

	public ULine adjustInnerLine() {
		float y = (bottom - top) / 3f + top;
		PointF leftPF = new PointF(left, y);
		PointF rightPF = new PointF(right, y);
		this.innerLine = new ULine(leftPF, rightPF);
		return this.innerLine;
	}
}
