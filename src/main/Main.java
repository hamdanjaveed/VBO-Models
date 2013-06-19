package main;

import camera.Camera;
import model.Face;
import model.Model;
import model.OBJLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * User: hamdan
 * Date: 2013-06-16
 * Time: 9:38 PM
 */
public class Main {

	private static final int DISPLAY_WIDTH = 1280;
	private static final int DISPLAY_HEIGHT = 720;

	private Camera camera;

	private long timeOfLastFrame;

	// torus
	private Model torusModel;

	private int torusVertexHandle;
	private int torusNormalHandle;

	public Main() {
		initializeProgram();
		run();
	}

	private void initializeProgram() {
		initializeDisplay();
		initializeGL();
		initializeVariables();
	}

	private void initializeDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			Display.setTitle("VBO Models");
			Display.create();

			Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
			tearDownGL();
			System.err.println("Could not initialize display");
			System.exit(1);
		}
	}

	private void initializeGL() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(65.0f, (float) DISPLAY_WIDTH / DISPLAY_HEIGHT, 0.001f, 500.0f);
		glMatrixMode(GL_MODELVIEW);
	}

	private void initializeVariables() {
		camera = new Camera();
		setupTorus();
	}

	private void setupTorus() {
		setupModel();
		setupVertexBufferObjects();
	}

	private void setupModel() {
		try {
			torusModel = OBJLoader.loadModelFromFile("src/model/torus.obj");
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
			tearDownGL();
			System.exit(1);
		} catch (IOException ioException) {
			ioException.printStackTrace();
			tearDownGL();
			System.exit(2);
		}
	}

	private void setupVertexBufferObjects() {
		torusVertexHandle = glGenBuffers();
		torusNormalHandle = glGenBuffers();

		FloatBuffer vertices = reserveData(torusModel.faces.size() * 3 * 3);
		FloatBuffer normals = reserveData(torusModel.faces.size() * 3 * 3);
		for (Face face : torusModel.faces) {
			vertices.put(vectorAsFloats(torusModel.vertices.get(face.vertexIndex[0] - 1)));
			vertices.put(vectorAsFloats(torusModel.vertices.get(face.vertexIndex[1] - 1)));
			vertices.put(vectorAsFloats(torusModel.vertices.get(face.vertexIndex[2] - 1)));

			normals.put(vectorAsFloats(torusModel.normals.get(face.normalIndex[0] - 1)));
			normals.put(vectorAsFloats(torusModel.normals.get(face.normalIndex[1] - 1)));
			normals.put(vectorAsFloats(torusModel.normals.get(face.normalIndex[2] - 1)));
		}
		vertices.flip();
		normals.flip();

		glBindBuffer(GL_ARRAY_BUFFER, torusVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		glBindBuffer(GL_ARRAY_BUFFER, torusNormalHandle);
		glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private float[] vectorAsFloats(Vector3f vector) {
		return new float[] { vector.x, vector.y, vector.z };
	}

	private FloatBuffer reserveData(int size) {
		FloatBuffer data = BufferUtils.createFloatBuffer(size);
		return data;
	}

	private void run() {
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			update();
			render();
			Display.update();
			Display.sync(60);
		}
		tearDownGL();
		System.exit(0);
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glBindBuffer(GL_ARRAY_BUFFER, torusVertexHandle);
		glVertexPointer(3, GL_FLOAT, 0, 0L);

		glBindBuffer(GL_ARRAY_BUFFER, torusNormalHandle);
		glNormalPointer(GL_FLOAT, 0, 0L);

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);

		glColor3f(0.8f, 0.60f, 0.6f);
		glDrawArrays(GL_TRIANGLES, 0, torusModel.faces.size() * 3);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private void update() {
		int delta = getDelta();
		camera.update(delta);
	}

	private void tearDownGL() {
		glDeleteBuffers(torusVertexHandle);
		glDeleteBuffers(torusNormalHandle);

		Display.destroy();
	}

	// UTILITY METHODS

	private long getSystemTime() {
		return System.nanoTime() / 1000000;
	}

	private int getDelta() {
		long time = getSystemTime();
		int delta = (int) (time - timeOfLastFrame);
		timeOfLastFrame = time;
		return delta;
	}

	public static void main(String[] args) {
		new Main();
	}

}
