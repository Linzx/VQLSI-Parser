package cn.edu.nju.seg.uml.sketch;

import java.util.List;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @author Zhang Xuelin
 * 
 * @date 2014-3-12 ÏÂÎç4:51:53
 */
public class UMLClass extends UMLInterface {

	private ULine secondLine = null;

	public UMLClass(float left, float top, float right, float bottom) {
		super(left, top, right, bottom);
		adjustSecondLine();
	}

	public UMLClass(List<PointF> points) {
		super(points);
		adjustSecondLine();
	}

	public UMLClass(PointF leftTopCorner, PointF rightTopCorner,
			PointF rightBottomCorner, PointF leftBottomCorner) {
		super(leftTopCorner, rightTopCorner, rightBottomCorner,
				leftBottomCorner);
		adjustSecondLine();
	}

	public UMLClass(Rect r) {
		super(r);
		adjustSecondLine();
	}

	public UMLClass(RectF r) {
		super(r);
		adjustSecondLine();
	}

	public ULine getSecondLine() {
		return secondLine;
	}

	public ULine adjustSecondLine() {
		super.adjustInnerLine();
		float y = (bottom - top) * 2 / 3f + top;
		PointF leftPF = new PointF(left, y);
		PointF rightPF = new PointF(right, y);
		this.secondLine = new ULine(leftPF, rightPF);
		return this.secondLine;
	}

}
