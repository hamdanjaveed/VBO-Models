package model;

/**
 * User: hamdan
 * Date: 2013-06-18
 * Time: 5:26 PM
 */
public class Face {

	public float[] vertexIndex = new float[3];
	public float[] normalIndex = new float[3];

	public Face(float[] vertexIndex, float[] normalIndex) {
		this.vertexIndex = vertexIndex;
		this.normalIndex = normalIndex;
	}

}
