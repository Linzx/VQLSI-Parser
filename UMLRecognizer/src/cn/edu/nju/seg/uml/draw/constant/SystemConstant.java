/**
 * 
 */
package cn.edu.nju.seg.uml.draw.constant;

/**
 * @author X.L.Zhang
 *
 */
public class SystemConstant {
	// 长度比例因子，用来设定阈值
	public final static float LENGTH_RATIO = 1/4f;
	
	// 判断三角形和直线距离
	public final static float DIS_RATIO = (float) (Math.sqrt(2)/2);
	
	// 平行角度阈值
	public final static int PARALLEL_VERTICAL_ANGLE = 20;
	
	// 交叉比例因子，用来判断线段是否在矩形框内
	public final static float CROSS_LENGTH_RATIO = 4/5F;
	
	// 识别模式
	public static enum MODE{delay,current};
}
