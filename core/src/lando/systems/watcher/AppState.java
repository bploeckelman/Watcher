package lando.systems.watcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Brian Ploeckelman created on 8/9/2014.
 */
public class AppState {

	public static final int viewport_width = 512;
	public static final int viewport_height = 512;

	// TODO : encapsulate sceneCamera functionality
	Viewport sceneViewport;
	Viewport hudViewport;

	// TODO : encapsulate watch directory functionality
	final String default_watch_dir = "C:\\";
	static final String chosenDir = "";
	static Path watchPath;
	WatchDir watchDir;

	// TODO : encapsulate animation functionality
	final float frame_rate_min   = 0.0077f;
	final float frame_step_small = 0.00025f;
	final float frame_step_big   = 0.0025f;
	final float default_frame_rate = 0.15f;
	static Map<String, Texture> textures;
	static Animation animation;
	static float framerate;
	Texture default_texture;
	TextureRegion keyframe;
	float animTimer;


	public AppState() {
		initializeCameras();
		initializeDefaultAnimation();
		initializeWatchDirectory(default_watch_dir);
	}

	// Public Interface -------------------------------------------------------

	/**
	 * Handle a window resize event
	 */
	public void resize(int width, int height) {
		sceneViewport.update(width, height);
		hudViewport.update(width, height);
	}

	/**
	 * Update all the app state
	 */
	public void update(float delta) {
		// Adjust frame rate in small steps
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			framerate += frame_step_small;
			refreshAnimation();
		}
		else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			framerate -= frame_step_small;
			if (framerate <= frame_rate_min) framerate = frame_rate_min;
			refreshAnimation();
		}

		// Adjust frame rate in big steps
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			framerate += frame_step_big;
			refreshAnimation();
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			framerate -= frame_step_big;
			if (framerate <= frame_rate_min) framerate = frame_rate_min;
			refreshAnimation();
		}

		// Update animation timer
		if ((animTimer += delta) > animation.getAnimationDuration()) {
			animTimer = 0f;
		}

		// Get current keyframe (assuming a valid animation)
		if (animation.getAnimationDuration() != 0) {
			keyframe = animation.getKeyFrame(animTimer);
		}

		sceneViewport.update();
		hudViewport.update();
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
	 * @param name
	 * @param path
	 */
	public static void handleModifyEvent(Path name, Path path) {
		if (!name.toString().endsWith(".png")) {
			return;
		}
		Gdx.app.log("MODIFY EVENT", "received modify event for " + path.toString());

		try {
			FileHandle fileHandle = Gdx.files.absolute(path.toString());
			textures.put(name.toString(), new Texture(fileHandle));
		} catch (Exception e) {
			// TODO : if '8-bit only' message, display to user
			Gdx.app.log("TEXTURE LOAD FAILURE", "Failed to load '" + name.toString() + "', exception: " + e.getMessage());
		}

		String[] keys = textures.keySet().toArray(new String[textures.keySet().size()]);
		Arrays.sort(keys);

		Array<TextureRegion> regions = new Array<TextureRegion>();
		for (String key : keys) {
			regions.add(new TextureRegion(textures.get(key)));
		}
		animation = new Animation(framerate, regions);
	}

	/**
	 * Clear the current animation frames, reverting to the default animation
	 */
	public void clearAnimation() {
		textures.clear();
		animation = new Animation(1f, new TextureRegion(default_texture));
		animation.setPlayMode(Animation.PlayMode.LOOP);
		((OrthographicCamera) sceneViewport.getCamera()).zoom = 1;
	}

	/**
	 * Refresh animation frames based on current watch path
	 */
	public void refreshAnimation() {
		// Scan directory for pngs
		File watchPathFile = watchPath.toFile();
		if (watchPathFile != null) {
			File[] files = watchPath.toFile().listFiles();
			if (files == null) return;

			// Update filename->texture map from the current watch path
			for (File file : files) {
				if (file.isFile() && file.toString().endsWith(".png")) {
					String filename = file.getName();
					try {
						Texture texture = new Texture(Gdx.files.absolute(file.getAbsolutePath()));
						textures.put(filename, texture);
					} catch (Exception e) {
						// TODO : if '8-bit only' message, display to user
						Gdx.app.log("TEXTURE LOAD FAILURE", "Failed to load '" + filename + "', exception: " + e.getMessage());
					}
				}
			}

			// Sort filenames alphabetically
			String[] keys = textures.keySet().toArray(new String[textures.keySet().size()]);
			Arrays.sort(keys);

			// Get animation frames
			Array<TextureRegion> regions = new Array<TextureRegion>();
			for (String key : keys) {
				regions.add(new TextureRegion(textures.get(key)));
			}

			// Update the animation with the new frames (and possibly frame_rate)
			if (regions.size > 0) {
				animation = new Animation(framerate, regions);
				animation.setPlayMode(Animation.PlayMode.LOOP);
			}
		}
	}


	// Private Implementation -------------------------------------------------


	/**
	 * Initialize the scene sceneCamera and ui sceneCamera
	 */
	private void initializeCameras() {
//		sceneCamera = new OrthographicCamera();
//		sceneCamera.setToOrtho(false, viewport_width, viewport_height);//Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		sceneCamera.update();
//
//		hudCamera = new OrthographicCamera();
//		hudCamera.setToOrtho(false, viewport_width, viewport_height);
//		hudCamera.update();

		sceneViewport = new ScreenViewport();
		sceneViewport.getCamera().translate(viewport_width / 2f, viewport_height / 2f, 0);

		hudViewport = new ScreenViewport();
		hudViewport.getCamera().translate(viewport_width / 2f, viewport_height / 2f, 0);
	}

	/**
	 * Load the default single frame animation
	 */
	private void initializeDefaultAnimation() {
		if (textures == null) {
			textures = new HashMap<String, Texture>();
		}

		if (default_texture == null) {
			default_texture = new Texture(Gdx.files.internal("badlogic.jpg"));
		}

		animation = new Animation(1, new TextureRegion(default_texture));
		animation.setPlayMode(PlayMode.LOOP);

		keyframe = animation.getKeyFrame(0);

		framerate = default_frame_rate;
		animTimer = 0;
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

			refreshAnimation();

			watchDir = new WatchDir(watchPath, false);
			watchDir.processEvents();
		} catch (IOException e) {
			Gdx.app.log("EXCEPTION", e.getMessage());
		}
	}

}
