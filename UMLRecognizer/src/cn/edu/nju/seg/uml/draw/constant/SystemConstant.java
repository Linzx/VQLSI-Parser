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
	
	// 平行角度阈值
	public final static int PARALLEL_VERTICAL_ANGLE = 30;
	
	// 交叉比例因子，用来判断线段是否在矩形框内
	public final static float CROSS_LENGTH_RATIO = 3/5F;
	
	// 识别模式
	public static enum MODE{delay,current};
}
