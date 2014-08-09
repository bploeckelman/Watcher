package lando.systems.watcher;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class WatcherMain extends ApplicationAdapter {

	static Map<String, Texture> textures;
	static Animation animation;
	static Path watchPath;
	static float frame_rate;
	static final String chosenDir = "";

	SpriteBatch batch;
	OrthographicCamera camera;
	WatchDir watchDir;
	Texture default_texture;
	TextureRegion keyframe;
	BitmapFont font;
	float accum;

	final float frame_rate_min   = 0.0077f;
	final float frame_step_small = 0.00025f;
	final float frame_step_big   = 0.0025f;
	final float default_frame_rate = 0.15f;
	final String default_watch_dir = "C:\\";

	@Override
	public void create () {
		batch = new SpriteBatch();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 512, 512);

		Gdx.input.setInputProcessor(new InputAdapter() {
			final float shift_scroll_modifier = 0.5f;
			final float scroll_modifier = 0.01f;
			boolean shiftDown = false;
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
					shiftDown = true;
				} else if (keycode == Keys.TAB) {
					camera.zoom = 1;
				}
				return false;
			}
			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Keys.ESCAPE) {
					Gdx.app.exit();
				}
				if (keycode == Keys.SPACE) {
					updateWatchDirectory();
				}
				if (keycode == Keys.BACKSPACE) {
					clearAnimation();
				}
				if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
					shiftDown = false;
				}
				return false;
			}
			@Override
			public boolean scrolled(int amount) {
				camera.zoom += amount * (shiftDown ? shift_scroll_modifier : scroll_modifier);
				return false;
			}
		});

		textures = new HashMap<String, Texture>();
		default_texture = new Texture(Gdx.files.internal("badlogic.jpg"));
		animation = new Animation(1f, new TextureRegion(default_texture));
		font = new BitmapFont();

		frame_rate = default_frame_rate;

		registerWatchDirectory(default_watch_dir);
	}

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());

//		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float whw = Gdx.graphics.getWidth() / 2f;
		float whh = Gdx.graphics.getHeight() / 2f;
		float thw = keyframe.getRegionWidth() / 2f;
		float thh = keyframe.getRegionHeight() / 2f;

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		{
			batch.draw(keyframe, whw - thw, whh - thh);

			font.setColor(Color.WHITE);
			font.draw(batch, "working directory: " + watchPath.toString(), 5, Gdx.graphics.getHeight() - 5);
			font.draw(batch, "anim  duration: " + String.format("%02.4f sec", animation.getAnimationDuration()), 5, Gdx.graphics.getHeight() - 25);
			font.draw(batch, "frame duration: " + String.format("%02.4f sec", frame_rate), 5, Gdx.graphics.getHeight() - 45);

			font.draw(batch, "[space] change directory", 5, 45 + font.getLineHeight());
			font.draw(batch, "[backspace] clear current animation", 5, 25 + font.getLineHeight());
			font.draw(batch, "[left/right] [up/down] change frame duration", 5, 5 + font.getLineHeight());
		}
		batch.end();
	}

	/**
	 * Register the specified path as the current watched directory
	 *
	 * @param path
	 */
	private void registerWatchDirectory(String path) {
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

	/**
	 * Clear the current animation frames, reverting to the default animation
	 */
	private void clearAnimation() {
		textures.clear();
		animation = new Animation(1f, new TextureRegion(default_texture));
		animation.setPlayMode(Animation.PlayMode.LOOP);
	}

	/**
	 * Refresh animation frames based on current watch path
	 */
	private void refreshAnimation() {
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
				animation = new Animation(frame_rate, regions);
				animation.setPlayMode(Animation.PlayMode.LOOP);
			}
		}
	}

	/**
	 * Update the application, process input, etc...
	 *
	 * @param delta
	 */
	public void update(float delta) {
		// Adjust frame rate in small steps
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			frame_rate += frame_step_small;
			refreshAnimation();
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			frame_rate -= frame_step_small;
			if (frame_rate <= frame_rate_min) frame_rate = frame_rate_min;
			refreshAnimation();
		}

		// Adjust frame rate in big steps
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			frame_rate += frame_step_big;
			refreshAnimation();
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			frame_rate -= frame_step_big;
			if (frame_rate <= frame_rate_min) frame_rate = frame_rate_min;
			refreshAnimation();
		}

		// Update animation timer
		if ((accum += delta) > animation.getAnimationDuration()) {
			accum = 0f;
		}

		// Get current keyframe (assuming a valid animation)
		if (animation.getAnimationDuration() != 0) {
			keyframe = animation.getKeyFrame(accum);
		}

		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
	}

	/**
	 * Prompt user to pick a new watched directory with a file chooser dialog
	 */
	private void updateWatchDirectory() {
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
			registerWatchDirectory(chosenFile.getAbsolutePath());
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
		animation = new Animation(frame_rate, regions);
	}

}
