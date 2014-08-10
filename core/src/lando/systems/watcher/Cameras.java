package lando.systems.watcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Brian Ploeckelman created on 8/9/2014.
 */
public class Cameras {

	OrthographicCamera sceneCamera;
	OrthographicCamera hudCamera;


	public Cameras() {
		sceneCamera = new OrthographicCamera();
		sceneCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sceneCamera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0);
		sceneCamera.update();

		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hudCamera.update();
	}


	/**
	 * Update the camera states
	 *
	 * @param delta the time between this frame and the last
	 */
	public void update(float delta) {
		sceneCamera.update();
		hudCamera.update();
	}

	/**
	 * Handle a window resize event
	 */
	public void resize(int width, int height) {
		sceneCamera.setToOrtho(false, width, height);
		sceneCamera.position.set(width / 2f, height / 2f, 0);
		sceneCamera.update();

		hudCamera.setToOrtho(false, width, height);
		hudCamera.update();
	}

}
