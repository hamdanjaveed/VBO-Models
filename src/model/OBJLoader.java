package model;

import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: hamdan
 * Date: 2013-06-18
 * Time: 5:30 PM
 */
public class OBJLoader {

	public static Model loadModelFromFile(String path) throws IOException {
		Model model = new Model();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] splitLine = line.split(" ");
			if (line.startsWith("v ")) {
				float x = Float.valueOf(splitLine[1]);
				float y = Float.valueOf(splitLine[2]);
				float z = Float.valueOf(splitLine[3]);
				model.vertices.add(new Vector3f(x, y, z));
			} else if (line.startsWith("vn ")) {
				float x = Float.valueOf(splitLine[1]);
				float y = Float.valueOf(splitLine[2]);
				float z = Float.valueOf(splitLine[3]);
				model.normals.add(new Vector3f(x, y, z));
			} else if (line.startsWith("f ")) {
				String[] splitLine1Split = splitLine[1].split("//");
				String[] splitLine2Split = splitLine[2].split("//");
				String[] splitLine3Split = splitLine[3].split("//");
				int[] vertexIndices = new int[] { Integer.valueOf(splitLine1Split[0]),
												  Integer.valueOf(splitLine2Split[0]),
												  Integer.valueOf(splitLine3Split[0]) };
				int[] normalIndices = new int[] { Integer.valueOf(splitLine1Split[1]),
												  Integer.valueOf(splitLine2Split[1]),
												  Integer.valueOf(splitLine3Split[1]) };
				model.faces.add(new Face(vertexIndices, normalIndices));
			}
		}

		bufferedReader.close();
		return model;
	}

}
