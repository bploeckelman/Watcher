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
public class WorkingDirectory {

	static final String default_watch_dir = "C:\\";

	static final String chosenDir = "";
	static Path watchPath;

	WatchDir watchDir;


	public WorkingDirectory() {
		initializeWatchDirectory(default_watch_dir);
	}


	/**
	 * Prompt user to pick a new watched directory with a file chooser dialog
	 */
	public void updateWatchDirectory() {
		final String cwd = watchPath.toAbsolutePath().toString();
		final JFileChooser fileChooser = new JFileChooser(cwd);
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
	 * Register the specified path as the current watched directory
	 *
	 * @param path the filesystem path to start watching
	 */
	private void initializeWatchDirectory(String path) {
		// register directory and process its events
		try {
			watchPath = Paths.get(path);

			WorkingAnimation.refresh();

			watchDir = new WatchDir(watchPath, false);
			watchDir.processEvents();
		} catch (IOException e) {
			Gdx.app.log("EXCEPTION", e.getMessage());
		}
	}

}
