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

import java.io.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
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

	// shader
	private int shaderProgram;
	private int vertexShaderHandle;
	private int fragmentShaderHandle;

	public Main() {
		initializeProgram();
		run();
	}

	// -------------------------------------------------- INIT -------------------------------------------------- //

	private void initializeProgram() {
		initializeDisplay();
		initializeGL();
		initializeVariables();
	}

	private void initializeDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			Display.setTitle("VBO Models");
			Display.setVSyncEnabled(true);
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

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		initializeLighting();
		initializeShaders();
	}

	private void initializeLighting() {
		glShadeModel(GL_SMOOTH);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glLightModel(GL_LIGHT_MODEL_AMBIENT, asFloatBuffer(0.05f, 0.05f, 0.05f, 1.0f));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
		glLight(GL_LIGHT0, GL_POSITION, asFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_DIFFUSE);
		glMaterialf(GL_FRONT, GL_SHININESS, 120);
		glMaterial(GL_FRONT, GL_DIFFUSE, asFloatBuffer(0.4f, 0.27f, 0.17f, 0));
	}

	private void initializeShaders() {
		shaderProgram = glCreateProgram();
		vertexShaderHandle = glCreateShader(GL_VERTEX_SHADER);
		fragmentShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);

		StringBuilder vertexShaderSource = loadShaderSourceFromPath("src/shader/shader.vertex");
		StringBuilder fragmentShaderSource = loadShaderSourceFromPath("src/shader/shader.fragment");

		compileShader(vertexShaderHandle, vertexShaderSource);
		compileShader(fragmentShaderHandle, fragmentShaderSource);

		glAttachShader(shaderProgram, vertexShaderHandle);
		glAttachShader(shaderProgram, fragmentShaderHandle);

		glLinkProgram(shaderProgram);
		glValidateProgram(shaderProgram);
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


	// -------------------------------------------------- LOOP -------------------------------------------------- //

	private void run() {
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			glUseProgram(shaderProgram);
			update();
			render();
			glUseProgram(0);
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

		glColor3f(0.44313726f, 0.44313726f, 0.7764706f);
		glDrawArrays(GL_TRIANGLES, 0, torusModel.faces.size() * 3);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private void update() {
		int delta = getDelta();
		camera.update(delta);
		glLight(GL_LIGHT0, GL_POSITION, asFloatBuffer(camera.position.x, camera.position.y, camera.position.z, 1.0f));
	}

	// -------------------------------------------------- EXIT -------------------------------------------------- //

	private void tearDownGL() {
		glDeleteBuffers(torusVertexHandle);
		glDeleteBuffers(torusNormalHandle);

		glDeleteProgram(shaderProgram);
		glDeleteShader(vertexShaderHandle);
		glDeleteShader(fragmentShaderHandle);

		Display.destroy();
	}

	// -------------------------------------------------- UTILITY -------------------------------------------------- //

	private long getSystemTime() {
		return System.nanoTime() / 1000000;
	}

	private int getDelta() {
		long time = getSystemTime();
		int delta = (int) (time - timeOfLastFrame);
		timeOfLastFrame = time;
		return delta;
	}

	private float[] vectorAsFloats(Vector3f vector) {
		return new float[] { vector.x, vector.y, vector.z };
	}

	private FloatBuffer reserveData(int size) {
		FloatBuffer data = BufferUtils.createFloatBuffer(size);
		return data;
	}

	private FloatBuffer asFloatBuffer(float a, float b, float c, float d) {
		return asFloatBuffer(new float[] { a, b, c, d });
	}

	private FloatBuffer asFloatBuffer(float[] data) {
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
		floatBuffer.put(data);
		floatBuffer.flip();
		return floatBuffer;
	}

	private StringBuilder loadShaderSourceFromPath(String path) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			tearDownGL();
			System.exit(1);
		}
		return shaderSource;
	}

	private void compileShader(int shaderHandle, StringBuilder shaderSource) {
		glShaderSource(shaderHandle, shaderSource);
		glCompileShader(shaderHandle);
		if (glGetShaderi(shaderHandle, GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println("Not able to compile shader " + shaderHandle);
	}

	public static void main(String[] args) {
		new Main();
	}

}
