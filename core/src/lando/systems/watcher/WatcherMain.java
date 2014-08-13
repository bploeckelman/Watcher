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
		appState.ui.render();
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
}
