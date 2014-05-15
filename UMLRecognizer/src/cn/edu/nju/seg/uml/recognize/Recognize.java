package cn.edu.nju.seg.uml.recognize;

import java.util.ArrayList;
import java.util.List;

import android.gesture.GesturePoint;
import android.graphics.PointF;
import cn.edu.nju.seg.uml.draw.constant.SystemConstant;
import cn.edu.nju.seg.uml.sketch.Rectangle;
import cn.edu.nju.seg.uml.sketch.Triangle;
import cn.edu.nju.seg.uml.sketch.ULine;
import cn.edu.nju.seg.uml.sketch.UMLClass;
import cn.edu.nju.seg.uml.sketch.UMLInterface;

/**
 * @author Zhang Xuelin
 * 
 * @date 2014-3-7 下午4:13:43
 */
public class Recognize {

	public static List<ULine> lines = LineContainer.lines;

	public static List<Rectangle> rectangles = new ArrayList<Rectangle>();

	public static List<Triangle> triangles = new ArrayList<Triangle>();

	public static List<UMLInterface> umlInterfaces = new ArrayList<UMLInterface>();
	
	public static List<UMLClass> umlClasses = new ArrayList<UMLClass>();

	public static void recognize() {
		if (lines.size() >= 3) {
			for (int i = 0; i < lines.size(); i++) {
				// 是否完成匹配标志
				boolean isFinished = false;

				// 已经匹配的线条
				List<ULine> recognizedLines = new ArrayList<ULine>();

				// 重置标志
				isFinished = false;

				// 获取第一条边及其两个顶点
				ULine firstLine = lines.get(i);
				GesturePoint firstPoint = firstLine.getStartPoint();
				GesturePoint endPoint = firstLine.getEndPoint();

				// 加入第一条边
				recognizedLines.add(firstLine);

				// 线段计数
				int index = 0;

				while (!isFinished && index < lines.size()) {
					// 若此线段已经匹配过，则不再进行比较
					while (index < lines.size()
							&& recognizedLines.contains(lines.get(index))) {
						index++;
					}

					if (index >= lines.size()) {
						isFinished = true;
						break;
					}
					// 获取线条
					ULine line = lines.get(index);
					GesturePoint start = line.getStartPoint();
					GesturePoint end = line.getEndPoint();
					// 判断是否能够连接
					boolean se = isEqual(firstPoint, start), ee = isEqual(
							firstPoint, end);
					if (se || ee) {
						if (se) {
							firstPoint = end;
							recognizedLines.add(line);
							index = 0;
						} else if (ee) {
							firstPoint = start;
							recognizedLines.add(line);
							index = 0;
						}

						int recoSize = recognizedLines.size();

						// 若能首尾相连，则判断是否是三角形和矩形
						if (isEqual(firstPoint, endPoint)) {
							if (recoSize == 3 || recoSize == 4) {
								List<PointF> points = new ArrayList<PointF>();

								for (ULine ul : recognizedLines) {
									GesturePoint gp = ul.getStartPoint();
									PointF pf = new PointF(gp.x, gp.y);
									for (PointF p : points) {
										if (isEqual(pf, p)) {
											gp = ul.getEndPoint();
											pf = new PointF(gp.x, gp.y);
											break;
										}
									}
									points.add(pf);
								}

								// 若是三条边 则是三角形
								if (recoSize == 3) {
									triangles.add(new Triangle(points));
									lines.removeAll(recognizedLines);
								} else if (recoSize == 4) {
									// 判断是否为矩形，还需要判断两两平行，互相垂直等条件
									if (isRectangle(recognizedLines.get(0),
											recognizedLines.get(1),
											recognizedLines.get(2),
											recognizedLines.get(3))) {
										rectangles.add(new Rectangle(points));
										lines.removeAll(recognizedLines);
									}
								}
								isFinished = true;
							}
						}
					} else
						index++;
				}
			}
		}
		
		/**
		 * 查看是否有 UML Interface 图形
		 */
		if (!rectangles.isEmpty() && !lines.isEmpty()) {
			for (int m = 0; m < lines.size(); m++) {
				ULine line = lines.get(m);
				float angle = (float) (line.getAngle()*180/Math.PI);
				float th = SystemConstant.PARALLEL_VERTICAL_ANGLE;
				if ((angle < th && angle > th * -1)
						|| (angle > 180 - th && angle < 180 + th) && !rectangles.isEmpty()) {
					for (int n = 0; n < rectangles.size(); n++) {
						Rectangle rectangle = rectangles.get(n);
						if (isInRect(rectangle, line)) {
							umlInterfaces.add(new UMLInterface(rectangle));
							rectangles.remove(rectangle);
							lines.remove(line);
							break;
						}
					}
				}
				if(rectangles.isEmpty())
					break;
			}
		}
		/**
		 * 查看是否有 UML Class 图形
		 */
		if(!umlInterfaces.isEmpty() && !lines.isEmpty())
		{
			for (int m = 0; m < lines.size(); m++) {
				ULine line = lines.get(m);
				float angle = (float) (line.getAngle()*180/Math.PI);
				
				float th = SystemConstant.PARALLEL_VERTICAL_ANGLE;
				if ((angle < th && angle > th * -1)
						|| (angle > 180 - th && angle < 180 + th)&&!umlInterfaces.isEmpty()) {
					for (int n = 0; n < umlInterfaces.size(); n++) {
						Rectangle rectangle = umlInterfaces.get(n);
						if (isInRect(rectangle, line)) {
							umlClasses.add(new UMLClass(rectangle));
							umlInterfaces.remove(rectangle);
							lines.remove(line);
							break;
						}
					}
				}
				if(umlInterfaces.isEmpty())
					break;
			}
		}

	}
	
	public static boolean isPointInRect(float x, float y)
	{
		boolean isIn = false;
		for (UMLClass umlClass : umlClasses) {
			if(umlClass.contains(x,y))
			{
				isIn = true;
				break;
			}
		}

		for (UMLInterface umlInterface : umlInterfaces) {
			if(umlInterface.contains(x, y))
			{
				isIn = true;
				break;
			}
		}
		
		return isIn;
	}

	public static boolean isInRect(Rectangle rectangle, ULine line) {
		float x1 = line.getStartPoint().x;
		float y1 = line.getStartPoint().y;
		float x2 = line.getEndPoint().x;
		float y2 = line.getEndPoint().y;

		if (rectangle.contains(x1, y1) && rectangle.contains(x2, y2))
			return true;
		else {
			float left = rectangle.left;
			float right = rectangle.right;
			float top = rectangle.top;
			float bottom = rectangle.bottom;
			if (left > right) {
				left = left + right;
				right = left - right;
				left = left - right;
			}
			if (top > bottom) {
				top = top + bottom;
				bottom = top - bottom;
				top = top - bottom;
			}
			float crossLength = 0f, combineLength = 0f;
			if ((y1 > top && y1 < bottom) && (y2 > top && y2 < bottom)) {
				if (x2 < left || right < x1)
					return false;
				else {
					if (x1 < left) {
						if (x2 < right) {
							crossLength = x2 - left;
							combineLength = right - x1;
						} else {
							crossLength = right - left;
							combineLength = x2 - x1;
						}
					} else {
						if (right < x2) {
							crossLength = right - x1;
							combineLength = x2 - left;
						} else {
							crossLength = x2 - x1;
							combineLength = right - left;
						}
					}
					float crossRatio = crossLength / combineLength;
					if (crossRatio > SystemConstant.CROSS_LENGTH_RATIO)
						return true;
					else
						return false;
				}
			} else
				return false;
		}
	}

	public static boolean isEqual(GesturePoint gp1, GesturePoint gp2) {
		return CornerDetect.getDis(gp1, gp2) == 0;
	}

	public static boolean isEqual(PointF pf1, PointF pf2) {
		return CornerDetect.getDis(pf1, pf2) == 0;
	}

	public static float getAngle(ULine u1, ULine u2) {
		float len1 = u1.getLength();
		float len2 = u2.getLength();

		float x1 = u1.getEndPoint().x - u1.getStartPoint().x;
		float y1 = u1.getEndPoint().y - u1.getStartPoint().y;
		float x2 = u2.getEndPoint().x - u2.getStartPoint().x;
		float y2 = u2.getEndPoint().y - u2.getStartPoint().y;

		return (float) (Math.acos((x1 * x2 + y1 * y2) / (len1 * len2)) * 180 / Math.PI);
	}

	public static boolean isParallel(ULine u1, ULine u2) {
		boolean isParallel = false;

		float angle = getAngle(u1, u2);
		angle = angle > 90 ? 180 - angle : angle;

		if (angle <= SystemConstant.PARALLEL_VERTICAL_ANGLE)
			isParallel = true;

		return isParallel;
	}

	public static boolean isVertical(ULine u1, ULine u2) {
		boolean isVertical = false;

		float angle = getAngle(u1, u2);

		if (Math.abs(angle - 90) <= SystemConstant.PARALLEL_VERTICAL_ANGLE)
			isVertical = true;

		return isVertical;
	}

	public static boolean isRectangle(ULine u1, ULine u2, ULine u3, ULine u4) {
		return isParallel(u1, u3) && isParallel(u4, u2) && isVertical(u1, u2)
				&& isVertical(u3, u2) && isVertical(u3, u4)
				&& isVertical(u1, u4);

	}

}
