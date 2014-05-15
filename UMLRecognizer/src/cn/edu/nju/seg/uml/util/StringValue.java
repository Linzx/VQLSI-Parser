package cn.edu.nju.seg.uml.util;

/**
 * @author Zhang Xuelin
 *
 * @date 2014-5-14 обнГ12:24:49
 */
public class StringValue {
	
	private String value = null;
	
	private float x;
	
	private float y;

	public StringValue(String value, float x, float y) {
		super();
		this.value = value;
		this.x = x;
		this.y = y;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	
}
