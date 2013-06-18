package model;

/**
 * User: hamdan
 * Date: 2013-06-18
 * Time: 5:26 PM
 */
public class Face {

	public int[] vertexIndex = new int[3];
	public int[] normalIndex = new int[3];

	public Face(int[] vertexIndex, int[] normalIndex) {
		this.vertexIndex = vertexIndex;
		this.normalIndex = normalIndex;
	}

}
