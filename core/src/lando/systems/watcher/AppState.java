package lando.systems.watcher;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Brian Ploeckelman created on 8/9/2014.
 */
public class AppState {

	static BitmapFont font = new BitmapFont();

	UserInterface ui;
	Cameras cameras;
	WorkingDirectory workingDirectory;
	WorkingAnimation workingAnimation;


	public AppState() {
		ui = new UserInterface(this);
		cameras = new Cameras();
		workingAnimation = new WorkingAnimation();
		workingDirectory = new WorkingDirectory();
	}


	/**
	 * Handle a window resize event
	 */
	public void resize(int width, int height) {
		cameras.resize(width, height);
		ui.resize(width, height);
	}

	/**
	 * Update all the app state
	 */
	public void update(float delta) {
		workingAnimation.update(delta);
		cameras.update(delta);
		ui.update(delta);
	}

	/**
	 * Dispose of assets
	 */
	public void dispose() {
		ui.dispose();
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
