package main;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
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

	}

	private void run() {
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			render();
			update();
			Display.update();
			Display.sync(60);
		}
		tearDownGL();
		System.exit(0);
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glBegin(GL_TRIANGLES); {
			glColor3f(1, 1, 1);
			glVertex3f(-1, 0, -10);
			glVertex3f(-1, 1, -10);
			glVertex3f(1, 1, -10);
		} glEnd();
	}

	private void update() {

	}

	private void tearDownGL() {
		Display.destroy();
	}

	public static void main(String[] args) {
		new Main();
	}

}
