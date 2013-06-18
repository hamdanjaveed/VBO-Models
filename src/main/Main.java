package main;

import camera.Camera;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
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
		gluPerspective(65.0f, (float)(DISPLAY_WIDTH / DISPLAY_HEIGHT), 0.001f, 500.0f);
		glMatrixMode(GL_MODELVIEW);
	}

	private void initializeVariables() {
		camera = new Camera();
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
	}

	private void update() {
		int delta = getDelta();
		camera.update(delta);
	}

	private void tearDownGL() {
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
