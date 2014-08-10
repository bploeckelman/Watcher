package lando.systems.watcher;

import com.badlogic.gdx.Gdx;

import java.nio.file.Path;

/**
 * Brian Ploeckelman created on 8/9/2014.
 */
public class AppState {

	Cameras cameras;
	WorkingDirectory workingDirectory;
	WorkingAnimation workingAnimation;


	public AppState() {
		cameras = new Cameras();
		workingAnimation = new WorkingAnimation();
		workingDirectory = new WorkingDirectory();
	}


	/**
	 * Handle a window resize event
	 */
	public void resize(int width, int height) {
		cameras.resize(width, height);
	}

	/**
	 * Update all the app state
	 */
	public void update(float delta) {
		workingAnimation.update(delta);
		cameras.update(delta);
	}

	/**
	 * Delegate update working directory request
	 */
	public void updateWatchDirectory() {
		workingDirectory.updateWatchDirectory();
	}

	/**
	 * Clear the current animation frames, reverting to the default animation
	 */
	public void clearAnimation() {
		workingAnimation.clear();
		cameras.sceneCamera.zoom = 1;
	}

}
