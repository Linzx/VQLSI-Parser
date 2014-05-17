package cn.edu.nju.seg.uml.draw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import cn.edu.nju.seg.uml.draw.constant.SystemConstant.MODE;
import cn.edu.nju.seg.uml.recognize.CornerDetect;
import cn.edu.nju.seg.uml.recognize.LineContainer;
import cn.edu.nju.seg.uml.recognize.Recognize;
import cn.edu.nju.seg.uml.sketch.Rectangle;
import cn.edu.nju.seg.uml.sketch.Triangle;
import cn.edu.nju.seg.uml.sketch.ULine;
import cn.edu.nju.seg.uml.sketch.UMLClass;
import cn.edu.nju.seg.uml.sketch.UMLGeneralization;
import cn.edu.nju.seg.uml.sketch.UMLInterface;
import cn.edu.nju.seg.uml.util.StringValue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.gesture.GesturePoint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class MyView extends View {

	private Paint paint = new Paint();
	private Paint np = new Paint();
	private Paint cornerPaint = new Paint();
	private Paint cornerPaint1 = new Paint();

	// Track the velocity of each points
	private VelocityTracker mVelocityTracker = null;

	// the path of stroke, consist of several points.
	public Path path = new Path();

	// the path store corners
	public Path cornerPath = new Path();
	public Path cornerPath1 = new Path();

	public static boolean parse = false;
	public static MODE mode = MODE.current;
	// the velocity map
	public static TreeMap<Long, Float> velocityMap = new TreeMap<Long, Float>();

	// the curvature map
	public static TreeMap<Long, Float> curvatureMap = new TreeMap<Long, Float>();

	// all points
	public static TreeMap<Long, GesturePoint> gPoints = new TreeMap<Long, GesturePoint>();

	// corner points
	public static TreeMap<Long, GesturePoint> cornerPoints = new TreeMap<Long, GesturePoint>();

	// user string input includes x, y, string value
	public static List<StringValue> stringValues = new ArrayList<StringValue>();

	public boolean flag = false;
	public boolean firstVelocity = true;
	public long lastTimeIndex = 0l;

	public static float downX = 0f;
	public static float downY = 0f;
	public static boolean isLongClick = false;

	// start point and end point of each sketch
	public GesturePoint startPoint;
	public GesturePoint endPoint;

	@SuppressLint("NewApi")
	public MyView(Context context) {
		super(context);

		// Set the paint, such as color, style...
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(2f);

		// Set the paint, such as color, style...
		cornerPaint.setAntiAlias(true);
		cornerPaint.setColor(Color.RED);
		cornerPaint.setStyle(Paint.Style.FILL);

		cornerPaint1.setAntiAlias(true);
		cornerPaint1.setColor(Color.BLUE);
		cornerPaint1.setStrokeWidth(1f);

		np.setAntiAlias(true);
		np.setColor(Color.BLUE);
		np.setStyle(Paint.Style.STROKE);
		np.setStrokeJoin(Paint.Join.ROUND);
		np.setStrokeWidth(2f);

		setX(0f);
		setY(0f);

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		float pointX = event.getX();
		float pointY = event.getY();

		long timeIndex = event.getEventTime();

		// Checks for the event that occurs
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			isLongClick = true; // 先将长按标识设为true， 然后再根据按的时间由系统判断是否是长按
			downX = pointX;
			downY = pointY;
			flag = false;

			firstVelocity = true;

			lastTimeIndex = timeIndex;

			path.moveTo(pointX, pointY);

			if (mVelocityTracker == null) {
				// Retrieve a new VelocityTracker object to watch the velocity
				// of a motion.
				mVelocityTracker = VelocityTracker.obtain();
			} else {
				// Reset the velocity tracker back to its initial state.
				mVelocityTracker.clear();
			}
			// Log.v("Velocity", "Start point->" + timeIndex);
			mVelocityTracker.addMovement(event);

			startPoint = new GesturePoint(pointX, pointY, timeIndex);

			gPoints.put(timeIndex, startPoint);
			return super.onTouchEvent(event);
		case MotionEvent.ACTION_MOVE:
			isLongClick = false; // 如果有移动，则判断不是长按。

			path.lineTo(pointX, pointY);

			mVelocityTracker.addMovement(event);
			// When you want to determine the velocity, call
			// computeCurrentVelocity(). Then call getXVelocity()
			// and getYVelocity() to retrieve the velocity for each pointer ID.
			mVelocityTracker.computeCurrentVelocity(1);
			// Log velocity of pixels per second
			// Best practice to use VelocityTrackerCompat where possible.

			GesturePoint gp = gPoints.get(lastTimeIndex);
			float arc = CornerDetect.getAngle(pointX - gp.x, pointY - gp.y);
			curvatureMap.put(timeIndex, arc);

			float Vx = mVelocityTracker.getXVelocity();
			float Vy = mVelocityTracker.getYVelocity();
			float V = (float) Math.sqrt(Vx * Vx + Vy * Vy);

			if (!firstVelocity)
				velocityMap.put(timeIndex, V);
			else {
				curvatureMap.put(lastTimeIndex, arc);
				firstVelocity = false;
				if (V != 0)
					velocityMap.put(timeIndex, V);
			}

			lastTimeIndex = timeIndex;

			gPoints.put(timeIndex, new GesturePoint(pointX, pointY, timeIndex));
			break;
		case MotionEvent.ACTION_UP:

			endPoint = new GesturePoint(pointX, pointY, timeIndex);

			GesturePoint lastSecondPoint = gPoints.get(lastTimeIndex);
			float lastArc = CornerDetect.getAngle(pointX - lastSecondPoint.x,
					pointY - lastSecondPoint.y);
			curvatureMap.put(timeIndex, lastArc);

			gPoints.put(timeIndex, endPoint);

			// 使用 速度 参数进行拐点检测
			// List<Long> corners = CornerDetect.detectSpeedCorner(velocityMap);

			// 使用 曲率 即 方向 为参数进行拐点检测
			List<Long> corners = CornerDetect
					.detectCurvatureCorner(curvatureMap);

			// 使用 速度 和 曲率 混合参数作为拐点检测
			// List<Long> corners =CornerDetect.detectHybridCorner(curvatureMap,
			// velocityMap);

			// the start and end point are regarded as corners
			corners.add(startPoint.timestamp);
			corners.add(endPoint.timestamp);

			// 拐点过滤
			TreeMap<Long, GesturePoint> cleanCorners = CornerDetect
					.cornerFilter(corners);

			// 获取分割线段
			List<ULine> lines = LineContainer.getNewLines(cleanCorners);

			// 将新的线段加入线段集合
			LineContainer.lines.addAll(lines);

			// 处理线段中顶点分离 和 交叉的情况
			LineContainer.cornerProcess();

			Recognize.recognize();
			// cornerPoints.putAll(cleanCorners);

			// 显示拐点
			Iterator<Entry<Long, GesturePoint>> cornerIterator = cleanCorners
					.entrySet().iterator();

			// 将之前的拐点清除
			// if(!cornerPath.isEmpty())
			// cornerPath.reset();

			// 将新的拐点集合加入路径显示
			while (cornerIterator.hasNext()) {
				Entry<Long, GesturePoint> next = cornerIterator.next();
				GesturePoint point = next.getValue();

				float _x = point.x;
				float _y = point.y;

				cornerPath.addCircle(_x, _y, 3, Path.Direction.CW);
			}

			if (mode.equals(MODE.current))
				path.reset();

			/*
			 * // 将轨迹中每点速度写入 SD Card 文件中 Iterator<Entry<Long, Float>> ite =
			 * velocityMap.entrySet().iterator(); String content = ""; while
			 * (ite.hasNext()) { Entry<Long, Float> next = ite.next(); float
			 * velocity = next.getValue(); long t = next.getKey(); content += t
			 * + "," + velocity + "\n"; }
			 * 
			 * saveContentToSDCard("velocity.txt", content);
			 * 
			 * // 将轨迹中每点曲率即方向写入 SD card 文件中 Iterator<Entry<Long, Float>> itarc =
			 * curvatureMap.entrySet().iterator(); String arcString = ""; while
			 * (itarc.hasNext()) { Entry<Long, Float> next = itarc.next(); float
			 * arcValue = next.getValue(); long t = next.getKey(); arcString +=
			 * t + "," + arcValue + "\n"; } saveContentToSDCard("arc.txt",
			 * arcString);
			 */

			// clear the velocity tracker and map
			mVelocityTracker.clear();
			velocityMap.clear();
			curvatureMap.clear();
			flag = true;
			break;

		case MotionEvent.ACTION_CANCEL:
			mVelocityTracker.recycle();
			break;
		default:
			return false;
		}

		// Force a view to draw.
		postInvalidate();
		return false;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawPath(path, paint);
		if (mode.equals(MODE.current)) {

			// canvas.drawPath(cornerPath, cornerPaint);

			for (ULine ul : LineContainer.lines) {
				canvas.drawLine(ul.getStartPoint().x, ul.getStartPoint().y,
						ul.getEndPoint().x, ul.getEndPoint().y, np);
			}

			for (Rectangle rect : Recognize.rectangles) {
				canvas.drawRect(rect, np);
			}

			for (UMLClass umlClass : Recognize.umlClasses) {
				// canvas.drawText("UML Class", umlClass.left, umlClass.top -
				// 2,cornerPaint1);

				canvas.drawRect(umlClass, np);
				ULine line = umlClass.getInnerLine();
				ULine secondLine = umlClass.getSecondLine();
				canvas.drawLine(line.getStartPoint().x, line.getStartPoint().y,
						line.getEndPoint().x, line.getEndPoint().y, np);
				canvas.drawLine(secondLine.getStartPoint().x,
						secondLine.getStartPoint().y,
						secondLine.getEndPoint().x, secondLine.getEndPoint().y,
						np);
			}

			for (UMLInterface umlInterface : Recognize.umlInterfaces) {
				// canvas.drawText("UML Interface",
				// umlInterface.left,umlInterface.top - 2, cornerPaint1);
				canvas.drawRect(umlInterface, np);
				ULine line = umlInterface.getInnerLine();
				canvas.drawLine(line.getStartPoint().x, line.getStartPoint().y,
						line.getEndPoint().x, line.getEndPoint().y, np);
			}

			for (Triangle tri : Recognize.triangles) {
				List<PointF> corners = tri.getCorners();
				PointF pf = corners.get(0);
				for (int i = 1; i < corners.size(); i++) {
					PointF pf2 = corners.get(i);
					canvas.drawLine(pf.x, pf.y, pf2.x, pf2.y, np);
					pf = pf2;
				}

				canvas.drawLine(pf.x, pf.y, corners.get(0).x, corners.get(0).y,
						np);
			}

			for (UMLGeneralization ug : Recognize.umlGeneralizations) {
				
				List<PointF> corners = ug.getTriangle().getCorners();
				PointF pf = corners.get(0);
				for (int i = 1; i < corners.size(); i++) {
					PointF pf2 = corners.get(i);
					canvas.drawLine(pf.x, pf.y, pf2.x, pf2.y, np);
					pf = pf2;
				}

				canvas.drawLine(pf.x, pf.y, corners.get(0).x, corners.get(0).y,
						np);

				canvas.drawLine(ug.getLine().getStartPointF().x, ug.getLine()
						.getStartPointF().y, ug.getLine().getEndPointF().x, ug
						.getLine().getEndPointF().y, np);
			}
			for (StringValue sv : stringValues) {
				canvas.drawText(sv.getValue(), sv.getX(), sv.getY(),
						cornerPaint1);
			}
		} else if (mode.equals(MODE.delay)) {
			if (parse) {

				// canvas.drawPath(cornerPath, cornerPaint);

				for (ULine ul : LineContainer.lines) {
					canvas.drawLine(ul.getStartPoint().x, ul.getStartPoint().y,
							ul.getEndPoint().x, ul.getEndPoint().y, np);
				}

				for (Rectangle rect : Recognize.rectangles) {
					canvas.drawRect(rect, np);
				}

				for (UMLClass umlClass : Recognize.umlClasses) {
					// canvas.drawText("UML Class", umlClass.left,umlClass.top -
					// 2, cornerPaint1);

					canvas.drawRect(umlClass, np);
					ULine line = umlClass.getInnerLine();
					ULine secondLine = umlClass.getSecondLine();
					canvas.drawLine(line.getStartPoint().x,
							line.getStartPoint().y, line.getEndPoint().x,
							line.getEndPoint().y, np);
					canvas.drawLine(secondLine.getStartPoint().x,
							secondLine.getStartPoint().y,
							secondLine.getEndPoint().x,
							secondLine.getEndPoint().y, np);
				}

				for (UMLInterface umlInterface : Recognize.umlInterfaces) {
					// canvas.drawText("UML Interface",
					// umlInterface.left,umlInterface.top - 2, cornerPaint1);
					canvas.drawRect(umlInterface, np);
					ULine line = umlInterface.getInnerLine();
					canvas.drawLine(line.getStartPoint().x,
							line.getStartPoint().y, line.getEndPoint().x,
							line.getEndPoint().y, np);
				}

				for (Triangle tri : Recognize.triangles) {
					List<PointF> corners = tri.getCorners();
					PointF pf = corners.get(0);
					for (int i = 1; i < corners.size(); i++) {
						PointF pf2 = corners.get(i);
						canvas.drawLine(pf.x, pf.y, pf2.x, pf2.y, np);
						pf = pf2;
					}

					canvas.drawLine(pf.x, pf.y, corners.get(0).x,
							corners.get(0).y, np);
				}

				for (StringValue sv : stringValues) {
					canvas.drawText(sv.getValue(), sv.getX(), sv.getY(),
							cornerPaint1);
				}
				
				for (UMLGeneralization ug : Recognize.umlGeneralizations) {
					
					List<PointF> corners = ug.getTriangle().getCorners();
					PointF pf = corners.get(0);
					for (int i = 1; i < corners.size(); i++) {
						PointF pf2 = corners.get(i);
						canvas.drawLine(pf.x, pf.y, pf2.x, pf2.y, np);
						pf = pf2;
					}

					canvas.drawLine(pf.x, pf.y, corners.get(0).x, corners.get(0).y,
							np);

					canvas.drawLine(ug.getLine().getStartPointF().x, ug.getLine()
							.getStartPointF().y, ug.getLine().getEndPointF().x, ug
							.getLine().getEndPointF().y, np);
				}

			}
		}

	}

	/**
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            保存内容
	 * @return
	 * @throws IOException
	 */
	public static boolean saveContentToSDCard(String fileName, String content) {
		boolean tag = false;

		/*---------------
		 * 文件输入输出流
		 * --------------
		 */
		FileOutputStream fileOutputStream = null;

		File file = new File(Environment.getExternalStorageDirectory(),
				fileName);

		/*---------------
		 * 判断SD卡是否可用
		 * --------------
		 */
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			try {
				fileOutputStream = new FileOutputStream(file);

				/*---------------
				 * 写入内容
				 * --------------
				 */
				fileOutputStream.write(content.getBytes());
				tag = true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			Log.i("MyTest", "--> SDCard not mounted!");
		}
		return tag;
	}

}
