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

	AppState appState;
	boolean shiftDown;

	public InputHandler(AppState appState) {
		this.appState = appState;
		this.shiftDown = false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftDown = true;
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
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		appState.cameras.sceneCamera.zoom += amount * (shiftDown ? shift_scroll_modifier : scroll_modifier);
		if (appState.cameras.sceneCamera.zoom < 0.01f) {
			appState.cameras.sceneCamera.zoom = 0.01f;
		}
		return false;
	}
}
