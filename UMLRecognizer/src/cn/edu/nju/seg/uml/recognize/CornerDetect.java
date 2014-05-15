package cn.edu.nju.seg.uml.recognize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.gesture.GesturePoint;
import android.graphics.PointF;

import cn.edu.nju.seg.uml.draw.MyView;

/**
 * @author X.L.Zhang
 * 
 */
public class CornerDetect {

	/***
	 * 
	 * @param curvatureMap
	 * @param velocityMap
	 * @return
	 */
	public static List<Long> detectHybridCorner(
			TreeMap<Long, Float> curvatureMap, TreeMap<Long, Float> velocityMap) {
		List<Long> speedCorners = detectSpeedCorner(velocityMap);
		List<Long> curvatureCorners = detectCurvatureCorner(curvatureMap);
		List<Long> commonCorners = new ArrayList<Long>();

		for (long sc : speedCorners) {
			for (long cc : curvatureCorners) {
				if (sc == cc) {
					commonCorners.add(sc);
				}
			}
		}

		return commonCorners;
	}

	/***
	 * 
	 * @param curvatureMap
	 * @return
	 */
	public static List<Long> detectCurvatureCorner(
			TreeMap<Long, Float> curvatureMap) {
		List<Long> corners = new ArrayList<Long>();
		List<Long> candidates = new ArrayList<Long>();

		TreeMap<Long, Float> curvatures = getCurvatures(curvatureMap);

		float mean = getMean(curvatures);
		if (mean == 0)
			return corners;

		Iterator<Entry<Long, Float>> it = curvatures.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, Float> entry = it.next();
			long index = entry.getKey();
			float curvature = entry.getValue();

			if (curvature > mean) {
				candidates.add(index);
			} else {
				if (!candidates.isEmpty()) {
					corners.add(getMax(candidates));
					candidates.clear();
				}
			}
		}

		return corners;
	}

	/***
	 * 
	 * @param velocityMap
	 * @return
	 */
	public static List<Long> detectSpeedCorner(TreeMap<Long, Float> velocityMap) {
		List<Long> corners = new ArrayList<Long>();
		List<Long> candidates = new ArrayList<Long>();
		float threshold = getMean(velocityMap) * 0.9f;

		if (threshold == 0)
			return corners;

		// detect corners of stroke
		Iterator<Entry<Long, Float>> it = velocityMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, Float> entry = it.next();
			long index = entry.getKey();
			float velocity = entry.getValue();

			if (velocity < threshold) {
				candidates.add(index);
			} else {
				if (!candidates.isEmpty()) {
					corners.add(getMin(candidates));
					candidates.clear();
				}
			}

		}
		return corners;
	}

	/***
	 * 
	 * @param candidateList
	 * @return
	 */
	public static long getMin(List<Long> candidateList) {
		long min = Long.MAX_VALUE;
		for (long c : candidateList) {
			if (min >= c)
				min = c;
		}
		return min;
	}

	/***
	 * 
	 * @param candidateList
	 * @return
	 */
	public static long getMax(List<Long> candidateList) {
		long max = Long.MIN_VALUE;
		for (long c : candidateList) {
			if (max <= c)
				max = c;
		}
		return max;
	}

	/***
	 * 
	 * @param map
	 * @return
	 */
	protected static float getMean(TreeMap<Long, Float> map) {
		// the average and total of velocity
		float mean = 0f, total = 0f;

		// counter of pointers
		int count = 0;

		// compute the total and mean of velocity
		Iterator<Entry<Long, Float>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, Float> entry = it.next();
			total += entry.getValue();
			count++;

			// Log.v("LINZX", "vv-->"+entry.getValue());
		}

		if (count != 0)
			mean = total / count;

		// Log.v("LINZX", "mean-->"+mean*0.9);
		return mean;
	}

	/***
	 * 
	 * @param A1
	 * @param A2
	 * @return
	 */
	protected static float getDeltaAngle(float A1, float A2) {
		// Log.v("Delta Angle-->", "A2-->"+A2+"       A1-->"+A1);
		float delta = Math.abs(A2 - A1);
		if (delta > Math.PI) {
			delta = (float) (Math.PI * 2 - delta);
		}
		return delta;
	}

	/***
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	protected static float getDeltaArc(Long t1, Long t2) {
		float deltaArc = 0f;

		float x1 = MyView.gPoints.get(t1).x;
		float y1 = MyView.gPoints.get(t1).y;

		float x2 = MyView.gPoints.get(t2).x;
		float y2 = MyView.gPoints.get(t2).y;

		float dis = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		deltaArc = (float) Math.sqrt(dis);

		return deltaArc;
	}

	/***
	 * 
	 * @param curvatureMap
	 * @return
	 */
	protected static TreeMap<Long, Float> getCurvatures(
			TreeMap<Long, Float> curvatureMap) {
		TreeMap<Long, Float> curvatures = new TreeMap<Long, Float>();

		Entry<Long, Float> A1 = null, A2 = null;

		Iterator<Entry<Long, Float>> it = curvatureMap.entrySet().iterator();

		if (it.hasNext()) {
			A1 = it.next();
			/*
			 * if(it.hasNext()) A1 = it.next();
			 */
		}

		//String cur = "";

		while (it.hasNext()) {
			A2 = it.next();

			float deltaArc = getDeltaArc(A1.getKey(), A2.getKey());

			float deltaAngle = getDeltaAngle(A1.getValue(), A2.getValue());

			// 变化角度
			//cur += A1.getKey() + "," + deltaAngle + "\n";

			float curvature = 0f;
			if (deltaArc != 0)
				curvature = deltaAngle;

			// if(curvature > Math.PI/4)
			// Log.v("LINZX",
			// "PI/4--time-->"+A1.getKey()+" A1-->"+A1.getValue()/Math.PI*180+"  A2-->"+A2.getValue()/Math.PI*180);

			// Log.v("LINZX",
			// "delta angle-->"+Math.abs(deltaAngle)/Math.PI*180+"     delta arc-->"+deltaArc);

			curvatures.put(A1.getKey(), curvature);

			A1 = A2;
		}

		//MyView.saveContentToSDCard("curvature.txt", cur.toString());

		return curvatures;
	}

	/***
	 * 
	 * @param corners
	 * @return
	 */
	public static TreeMap<Long, GesturePoint> cornerFilter(List<Long> corners) {
		TreeMap<Long, GesturePoint> cleanCorners = new TreeMap<Long, GesturePoint>();

		Collections.sort(corners);

		GesturePoint gp1 = MyView.gPoints.get(corners.get(0)), gp2 = null, gp3 = null;

		if (corners.size() >= 2)
			gp2 = MyView.gPoints.get(corners.get(1));

		// 将第一个顶点放入过滤顶点集合中
		cleanCorners.put(gp1.timestamp, gp1);

		if (corners.size() >= 3) {
			for (int i = 1; i < corners.size() - 1; i++) {
				gp3 = MyView.gPoints.get(corners.get(i + 1));

				// 计算相邻3个顶点是否共线，若角度差值小于设定的阈值，则认定为共线
				// 若共线，则删除中间一个顶点
				int arc1 = (int) (getAngle(gp2.x - gp1.x, gp2.y - gp1.y) * 180 / Math.PI);
				int arc2 = (int) (getAngle(gp3.x - gp2.x, gp3.y - gp2.y) * 180 / Math.PI);

				// 不共线，加入第二个顶点
				int arcDiff = Math.abs(arc1 - arc2);
				int diff = arcDiff > 180 ? 360 - arcDiff : arcDiff;
				if (diff >= 20) {
					cleanCorners.put(gp2.timestamp, gp2);
					gp1 = gp2;
					gp2 = gp3;
				} else {
					gp2 = gp3;
				}
			}
		}

		cleanCorners.put(gp2.timestamp, gp2);

		return cleanCorners;
	}

	/***
	 * 
	 * @param cleanCorners
	 * @return
	 */
	public static float getMaxDistance(TreeMap<Long, GesturePoint> cleanCorners) {
		float maxDis = 0f;

		Iterator<Entry<Long, GesturePoint>> it = cleanCorners.entrySet()
				.iterator();

		GesturePoint gp1 = null, gp2 = null;
		if (it.hasNext())
			gp1 = it.next().getValue();
		while (it.hasNext()) {
			gp2 = it.next().getValue();
			float dis = getDis(gp1, gp2);

			if (maxDis < dis)
				maxDis = dis;

			gp1 = gp2;
		}

		return maxDis;
	}

	/***
	 * 
	 * @param gp1
	 * @param gp2
	 * @return
	 */
	public static float getDis(GesturePoint gp1, GesturePoint gp2) {
		return (float) Math.sqrt((gp1.x - gp2.x) * (gp1.x - gp2.x)
				+ (gp1.y - gp2.y) * (gp1.y - gp2.y));
	}

	public static float getDis(PointF pf1, PointF pf2) {
		return (float) Math.sqrt((pf1.x - pf2.x) * (pf1.x - pf2.x)
				+ (pf1.y - pf2.y) * (pf1.y - pf2.y));
	}

	/***
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static float getAngle(float x, float y) {
		float arc = 0f;
		float tan = 0f;

		if (x == 0 && y == 0) {
			tan = 0;
		} else
			tan = y / x;
		arc = (float) Math.atan(tan);
		if (tan >= 0) {
			if (x < 0 && y <= 0) {
				arc += Math.PI;
			}
		} else if (tan < 0) {
			if (x >= 0 && y < 0)
				arc += Math.PI * 2;
			else if (x < 0 && y > 0)
				arc += Math.PI;
		}
		return arc;
	}
}
