package lando.systems.watcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Brian Ploeckelman created on 8/9/2014.
 *
 * TODO : address the use of static fields/methods in this class
 */
public class WorkingAnimation {

	static final String default_texture_filename = "badlogic.jpg";
	static final float frame_rate_min   = 0.0077f;
	static final float frame_step_small = 0.00025f;
	static final float frame_step_big   = 0.0025f;
	static final float default_frame_rate = 0.15f;

	static Map<String, Texture> textures;
	static Animation animation;
	static float framerate;
	float animTimer;

	Texture default_texture;
	TextureRegion keyframe;


	public WorkingAnimation() {
		initializeDefaultAnimation();
	}


	/**
	 * Update the animation state
	 *
	 * @param delta the time between this frame and the last
	 */
	public void update(float delta) {
		// Adjust frame rate in small steps
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			framerate += frame_step_small;
			refresh();
		}
		else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			framerate -= frame_step_small;
			if (framerate <= frame_rate_min) framerate = frame_rate_min;
			refresh();
		}

		// Adjust frame rate in big steps
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			framerate += frame_step_big;
			refresh();
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			framerate -= frame_step_big;
			if (framerate <= frame_rate_min) framerate = frame_rate_min;
			refresh();
		}

		// Update animation timer
		if ((animTimer += delta) > animation.getAnimationDuration()) {
			animTimer = 0f;
		}

		// Get current keyframe (assuming a valid animation)
		if (animation.getAnimationDuration() != 0) {
			keyframe = animation.getKeyFrame(animTimer);
		}
	}

	/**
	 * Clear the current animation frames, reverting to the default animation
	 */
	public void clear() {
		textures.clear();
		animation = new Animation(1f, new TextureRegion(default_texture));
		animation.setPlayMode(PlayMode.LOOP);
	}

	/**
	 * Refresh animation frames based on current watch path
	 */
	public void refresh() {
		// Scan directory for pngs
		File watchPathFile = AppState.watchPath.toFile();
		if (watchPathFile != null) {
			File[] files = AppState.watchPath.toFile().listFiles();
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

	/**
	 * Handle a 'filesystem modified' event by updating the animation
	 *
	 * @param filename the name of the modified file
	 * @param filepath the path of the modified file
	 */
	public static void modify(Path filename, Path filepath) {
		try {
			FileHandle fileHandle = Gdx.files.absolute(filepath.toString());
			textures.put(filename.toString(), new Texture(fileHandle));
		} catch (Exception e) {
			// TODO : if '8-bit only' message, display to user
			Gdx.app.log("TEXTURE LOAD FAILURE", "Failed to load '" + filename.toString() + "', exception: " + e.getMessage());
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
	 * Load the default single frame animation
	 */
	private void initializeDefaultAnimation() {
		if (textures == null) {
			textures = new HashMap<String, Texture>();
		}

		if (default_texture == null) {
			default_texture = new Texture(Gdx.files.internal(default_texture_filename));
		}

		animation = new Animation(1, new TextureRegion(default_texture));
		animation.setPlayMode(PlayMode.LOOP);

		keyframe = animation.getKeyFrame(0);

		framerate = default_frame_rate;
		animTimer = 0;
	}

}