package lando.systems.watcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

/**
 * Brian Ploeckelman created on 8/9/2014.
 */
public class InputHandler extends InputAdapter {
	static final float shift_scroll_modifier = 0.1f;
	static final float scroll_modifier = 0.01f;
	static final float minimum_zoom = 0.015f;
	static final int minimum_thumbnail = 4;

	AppState appState;
	boolean shiftDown;
	boolean ctrlDown;

	public InputHandler(AppState appState) {
		this.appState = appState;
		this.shiftDown = false;
		this.ctrlDown = false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftDown = true;
		} else if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrlDown = true;
		} else if (keycode == Keys.TAB) {
			appState.cameras.sceneCamera.zoom = 1;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.ESCAPE) {
			Gdx.app.exit();
		}
		if (keycode == Keys.SPACE) {
			appState.updateWatchDirectory();
		}
		if (keycode == Keys.BACKSPACE) {
			appState.clearAnimation();
		}
		if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftDown = false;
		}
		if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrlDown = false;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (ctrlDown) {
			appState.workingAnimation.thumbnail_width  += amount;
			appState.workingAnimation.thumbnail_height += amount;
			if (appState.workingAnimation.thumbnail_width  < minimum_thumbnail)
				appState.workingAnimation.thumbnail_width  = minimum_thumbnail;
			if (appState.workingAnimation.thumbnail_height < minimum_thumbnail)
				appState.workingAnimation.thumbnail_height = minimum_thumbnail;
		} else {
			appState.cameras.sceneCamera.zoom += amount * (shiftDown ? shift_scroll_modifier : scroll_modifier);
			if (appState.cameras.sceneCamera.zoom < minimum_zoom) {
				appState.cameras.sceneCamera.zoom = minimum_zoom;
			}
		}
		return false;
	}
}
