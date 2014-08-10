package lando.systems.watcher;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class WatcherMain extends ApplicationAdapter {

	BitmapFont font;
	SpriteBatch batch;
	AppState appState;

	@Override
	public void create () {
		font = new BitmapFont();
		batch = new SpriteBatch();
		appState = new AppState();

		Gdx.input.setInputProcessor(new InputHandler(appState));
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
		int vw = (int) appState.cameras.hudCamera.viewportWidth;
		int vh = (int) appState.cameras.hudCamera.viewportHeight;
		Gdx.gl20.glViewport(0, 0, vw, vh);

		batch.setProjectionMatrix(appState.cameras.hudCamera.combined);
		batch.begin();

		font.setColor(Color.WHITE);
		font.draw(batch, "working directory: " + WorkingDirectory.watchPath.toString(), 5, Gdx.graphics.getHeight() - 5);
		font.draw(batch, "anim  duration: " + String.format("%02.4f sec", WorkingAnimation.animation.getAnimationDuration()), 5, Gdx.graphics.getHeight() - 25);
		font.draw(batch, "frame duration: " + String.format("%02.4f sec", WorkingAnimation.framerate), 5, Gdx.graphics.getHeight() - 45);

		font.draw(batch, "[space] change directory", 5, 45 + font.getLineHeight());
		font.draw(batch, "[backspace] clear current animation", 5, 25 + font.getLineHeight());
		font.draw(batch, "[left/right] [up/down] change frame duration", 5, 5 + font.getLineHeight());

		appState.workingAnimation.renderUI(batch);

		batch.end();
	}

}
