package lando.systems.watcher;

import com.badlogic.gdx.Gdx;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Brian Ploeckelman created on 8/9/2014.
 */
public class AppState {

	Cameras cameras;

	// TODO : encapsulate watch directory functionality
	final String default_watch_dir = "C:\\";
	static final String chosenDir = "";
	static Path watchPath;
	WatchDir watchDir;

	WorkingAnimation workingAnimation;


	public AppState() {
		cameras = new Cameras();
		workingAnimation = new WorkingAnimation();
		initializeWatchDirectory(default_watch_dir);
	}

	// Public Interface -------------------------------------------------------

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
	 * Prompt user to pick a new watched directory with a file chooser dialog
	 */
	public void updateWatchDirectory() {
		final JFileChooser fileChooser = new JFileChooser(watchPath.toAbsolutePath().toString());
		try {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					fileChooser.showOpenDialog(null);
				}
			});
		} catch(Exception e) {
			Gdx.app.log("FILE CHOOSER", "File chooser error: " + e.getMessage());
		}

		final File chosenFile = fileChooser.getSelectedFile();
		if (chosenFile != null) {
			Gdx.app.log("FILE CHOOSER", "Directory: " + chosenFile.getAbsolutePath());
			initializeWatchDirectory(chosenFile.getAbsolutePath());
		}
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


	// Private Implementation -------------------------------------------------


	/**
	 * Register the specified path as the current watched directory
	 *
	 * @param path the filesystem path to start watching
	 */
	private void initializeWatchDirectory(String path) {
		// register directory and process its events
		try {
			watchPath = Paths.get(path);

			workingAnimation.refresh();

			watchDir = new WatchDir(watchPath, false);
			watchDir.processEvents();
		} catch (IOException e) {
			Gdx.app.log("EXCEPTION", e.getMessage());
		}
	}

}
