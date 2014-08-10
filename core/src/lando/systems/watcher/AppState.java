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
	 * Handle an EVENT_MODIFY event from the file system
	 *
	 * @param name the name of the modified file
	 * @param path the path of the modified file
	 */
	public static void handleModifyEvent(Path name, Path path) {
		if (!name.toString().endsWith(".png")) {
			return;
		}
		Gdx.app.log("MODIFY EVENT", "received modify event for " + path.toString());

		WorkingAnimation.modify(name, path);
	}

	/**
	 * Clear the current animation frames, reverting to the default animation
	 */
	public void clearAnimation() {
		workingAnimation.clear();
		cameras.sceneCamera.zoom = 1;
	}

}
