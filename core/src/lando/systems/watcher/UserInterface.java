package lando.systems.watcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Brian Ploeckelman created on 8/11/2014.
 */
public class UserInterface {

	Skin skin;
	Stage stage;
	AppState appState;

	TextButton loadAnimBtn;
	TextButton clearAnimBtn;
	TextButton quitBtn;


	public UserInterface(AppState state) {
		appState = state;
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new ScreenViewport());
		initializeWidgets();
	}


	public void update(float delta) {
		stage.act(delta);
	}

	public void render() {
		stage.draw();
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		quitBtn.setPosition(stage.getWidth() - 72 - 5, 5);
	}

	public void dispose() {
		stage.dispose();
	}


	private void initializeWidgets() {
		loadAnimBtn = new TextButton("Load", skin);
		loadAnimBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				appState.updateWatchDirectory();
			}
		});
		loadAnimBtn.setPosition(5, 5);
		loadAnimBtn.setSize(72, 32);

		clearAnimBtn = new TextButton("Clear", skin);
		clearAnimBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				appState.clearAnimation();
			}
		});
		clearAnimBtn.setPosition(5 + 72 + 5, 5);
		clearAnimBtn.setSize(72, 32);

		quitBtn = new TextButton("Quit", skin);
		quitBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
			}
		});
		quitBtn.setPosition(stage.getWidth() - 72 - 5, 5);
		quitBtn.setSize(72, 32);

		stage.addActor(loadAnimBtn);
		stage.addActor(clearAnimBtn);
		stage.addActor(quitBtn);
	}

}
