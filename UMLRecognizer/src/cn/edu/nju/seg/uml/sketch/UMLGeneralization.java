package cn.edu.nju.seg.uml.sketch;

/**
 * @author Zhang Xuelin
 * 
 * @date 2014-5-17 ионГ10:08:03
 */
public class UMLGeneralization {
	
	private Triangle triangle;
	
	private ULine line;

	
	public UMLGeneralization(Triangle triangle, ULine line) {
		super();
		this.triangle = triangle;
		this.line = line;
	}

	public Triangle getTriangle() {
		return triangle;
	}

	public void setTriangle(Triangle triangle) {
		this.triangle = triangle;
	}

	public ULine getLine() {
		return line;
	}

	public void setLine(ULine line) {
		this.line = line;
	}

}
