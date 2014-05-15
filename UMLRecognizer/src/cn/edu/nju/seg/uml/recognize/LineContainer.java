package cn.edu.nju.seg.uml.recognize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import android.gesture.GesturePoint;

import cn.edu.nju.seg.uml.draw.constant.SystemConstant;
import cn.edu.nju.seg.uml.sketch.ULine;

/**
 * @author Zhang Xuelin
 * 
 *         2014-3-3 下午2:08:16
 */
public class LineContainer {

	public static List<ULine> lines = new ArrayList<ULine>();

	public static List<ULine> getNewLines(
			TreeMap<Long, GesturePoint> cornerPoints) {
		List<ULine> lines = new ArrayList<ULine>();

		Iterator<Entry<Long, GesturePoint>> cornerIterator = cornerPoints
				.entrySet().iterator();

		GesturePoint gp1 = null, gp2 = null;

		if (cornerIterator.hasNext())
			gp1 = cornerIterator.next().getValue();

		while (cornerIterator.hasNext()) {
			Entry<Long, GesturePoint> next = cornerIterator.next();

			gp2 = next.getValue();

			ULine ul = new ULine(gp1, gp2);

			lines.add(ul);

			gp1 = gp2;
		}

		return lines;
	}

	/***
	 * 拐点处理，处理情况有：1、缺口 2、交叉
	 * 
	 * @return 处理后的线段集合
	 */
	public static List<ULine> cornerProcess() {
		
		int size = lines.size();
		
		if (size >= 2) {

			// 两条线段
			ULine ul1 = null, ul2 = null;
			float len1 = 0f,len2 = 0f;

			// 两条线段上的四个顶点
			GesturePoint gp11 = null, gp12 = null, gp21 = null, gp22 = null;

			// 比较顶点之间的距离，若小于设定的阈值这合并处理
			for (int i = 0; i < size - 1; i++) {
				ul1 = lines.get(i);
				len1 = ul1.getLength()*SystemConstant.LENGTH_RATIO;
				gp11 = ul1.getStartPoint();
				gp12 = ul1.getEndPoint();
				
				for (int j = i + 1; j < size; j++) {
					if (j != i) {
						ul2 = lines.get(j);
						len2 = ul2.getLength()*SystemConstant.LENGTH_RATIO;
						gp21 = ul2.getStartPoint();
						gp22 = ul2.getEndPoint();
						
						// 设定的阈值
						float minLen = len1<len2?len1:len2;
						
						float dis11 = CornerDetect.getDis(gp11, gp21);
						float dis12 = CornerDetect.getDis(gp11, gp22);
						float dis21 = CornerDetect.getDis(gp12, gp21);
						float dis22 = CornerDetect.getDis(gp12, gp22);
						
						if (dis11 < minLen && dis11 != 0) {
							float x = (gp11.x + gp21.x) / 2f;
							float y = (gp11.y + gp21.y) / 2f;
							GesturePoint gp = new GesturePoint(x, y,
									gp11.timestamp);
							ul1.setStartPoint(gp);
							ul2.setStartPoint(gp);
							gp11 = gp;
							j = -1;
							continue;

						} else if (dis12 < minLen && dis12 != 0) {
							float x = (gp11.x + gp22.x) / 2f;
							float y = (gp11.y + gp22.y) / 2f;
							GesturePoint gp = new GesturePoint(x, y,
									gp11.timestamp + 1);
							ul1.setStartPoint(gp);
							ul2.setEndPoint(gp);
							gp11 = gp;
							j = -1;
							continue;
						} else if (dis21 < minLen && dis21 != 0) {
							float x = (gp12.x + gp21.x) / 2f;
							float y = (gp12.y + gp21.y) / 2f;
							GesturePoint gp = new GesturePoint(x, y,
									gp12.timestamp);
							ul1.setEndPoint(gp);
							ul2.setStartPoint(gp);
							gp12 = gp;
							j = -1;
							continue;
						} else if (dis22 < minLen && dis22 != 0) {
							float x = (gp12.x + gp22.x) / 2f;
							float y = (gp12.y + gp22.y) / 2f;
							GesturePoint gp = new GesturePoint(x, y,
									gp12.timestamp);
							ul1.setEndPoint(gp);
							ul2.setEndPoint(gp);
							gp12 = gp;
							j = -1;
							continue;
						} else
							continue;

					} else
						continue;
				}
			}
		}

		return lines;
	}
	

	public static float getMaxLineLength(List<ULine> lines) {
		float maxLength = 0f;

		for (ULine ul : lines) {
			float length = ul.getLength();
			if (length > maxLength) {
				maxLength = length;
			}
		}
		return maxLength;
	}
}
