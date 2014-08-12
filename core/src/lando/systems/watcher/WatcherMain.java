package lando.systems.watcher;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class WatcherMain extends ApplicationAdapter {

	SpriteBatch batch;
	AppState appState;

	@Override
	public void create () {
		batch = new SpriteBatch();
		appState = new AppState();

		InputMultiplexer mux = new InputMultiplexer();
		mux.addProcessor(new InputHandler(appState));
		mux.addProcessor(appState.ui.stage);
		Gdx.input.setInputProcessor(mux);
	}

	@Override
	public void dispose() {
		batch.dispose();
		appState.dispose();
	}

	@Override
	public void resize(int width, int height) {
		appState.resize(width, height);
	}

	@Override
	public void render () {
		appState.update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		sceneRender();
		uiRender();
	}

	private void sceneRender() {
		float whw = Gdx.graphics.getWidth() / 2f;
		float whh = Gdx.graphics.getHeight() / 2f;
		float thw = appState.workingAnimation.keyframe.getRegionWidth() / 2f;
		float thh = appState.workingAnimation.keyframe.getRegionHeight() / 2f;

		int vw = (int) appState.cameras.sceneCamera.viewportWidth;
		int vh = (int) appState.cameras.sceneCamera.viewportHeight;
		Gdx.gl20.glViewport(0, 0, vw, vh);

		batch.setProjectionMatrix(appState.cameras.sceneCamera.combined);
		batch.begin();
		batch.draw(appState.workingAnimation.keyframe, whw - thw, whh - thh);
		batch.end();
	}

	private void uiRender() {
		final float margin_x = 5;
		final float line_height = AppState.font.getLineHeight();

		int vw = (int) appState.cameras.hudCamera.viewportWidth;
		int vh = (int) appState.cameras.hudCamera.viewportHeight;
		Gdx.gl20.glViewport(0, 0, vw, vh);

		batch.setProjectionMatrix(appState.cameras.hudCamera.combined);
		batch.begin();

		AppState.font.setColor(Color.WHITE);
//		AppState.font.draw(batch, "working directory: " + WorkingDirectory.watchPath.toString(), margin_x, Gdx.graphics.getHeight() - 5);
		AppState.font.draw(batch, "anim  duration: " + String.format("%02.4f sec", WorkingAnimation.animation.getAnimationDuration()), margin_x, Gdx.graphics.getHeight() - 25);
		AppState.font.draw(batch, "frame duration: " + String.format("%02.4f sec", WorkingAnimation.framerate), margin_x, Gdx.graphics.getHeight() - 45);

		AppState.font.draw(batch, "[mouse scroll] zoom animation in/out", 2 * (margin_x + 72), 45 + line_height);
		AppState.font.draw(batch, "[shift+scroll] zoom faster  [ctrl+scroll] zoom thumbnails", 2 * (margin_x + 72), 25 + line_height);
		AppState.font.draw(batch, "[left/right] [up/down] change frame duration", 2 * (margin_x + 72), 5 + line_height);

		appState.workingAnimation.renderUI(batch);

		batch.end();


		appState.ui.render();
	}

}
