package camera;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;

/**
 * User: hamdan
 * Date: 2013-06-16
 * Time: 11:53 PM
 */
public class Camera {

	private static final float MOVE_SPEED = 0.01f;
	private static final float MOUSE_SPEED = 0.01f;

	public Vector3f position;
	private Vector3f rotation;

	public Camera() {
		position = new Vector3f(0.0f, 0.0f, 0.0f);
		rotation = new Vector3f(0.0f, 0.0f, 0.0f);
	}

	private void applyTransformations() {
		glLoadIdentity();
		glRotatef(rotation.x, 1.0f, 0.0f, 0.0f);
		glRotatef(rotation.y, 0.0f, 1.0f, 0.0f);
		glRotatef(rotation.z, 0.0f, 0.0f, 1.0f);
		glTranslatef(-position.x, -position.y, -position.z);
	}

	public void update(int delta) {
		// keyboard
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			moveForward(MOVE_SPEED * delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			moveLeft(MOVE_SPEED * delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			moveBackward(MOVE_SPEED * delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			moveRight(MOVE_SPEED * delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			moveUp(MOVE_SPEED * delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			moveDown(MOVE_SPEED * delta);
		}

		// mouse
		rotation.y += Mouse.getDX() * MOUSE_SPEED * delta;
		rotation.x -= Mouse.getDY() * MOUSE_SPEED * delta;
		applyTransformations();
	}

	private void moveForward(float distance) {
		position.z -= distance * Math.cos(Math.toRadians(rotation.y));
		position.x += distance * Math.sin(Math.toRadians(rotation.y));
		//position.y -= distance * Math.sin(Math.toRadians(rotation.x));
	}

	private void moveBackward(float distance) {
		position.z += distance * Math.cos(Math.toRadians(rotation.y));
		position.x -= distance * Math.sin(Math.toRadians(rotation.y));
		//position.y += distance * Math.sin(Math.toRadians(rotation.x));
	}

	private void moveRight(float distance) {
		position.x += distance * Math.cos(Math.toRadians(rotation.y));
		position.z += distance * Math.sin(Math.toRadians(rotation.y));
	}

	private void moveLeft(float distance) {
		position.x -= distance * Math.cos(Math.toRadians(rotation.y));
		position.z -= distance * Math.sin(Math.toRadians(rotation.y));
	}

	private void moveUp(float distance) {
		position.y += distance;
	}

	private void moveDown(float distance) {
		position.y -= distance;
	}
}
