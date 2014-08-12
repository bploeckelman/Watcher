package lando.systems.watcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
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

	Window statusWindow;
	Label watchDirLbl;
	Label animDurationLbl;
	Label frameDurationLbl;


	public UserInterface(AppState state) {
		appState = state;
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new ScreenViewport());
		initializeWidgets();
	}


	public void update(float delta) {
		stage.act(delta);
		watchDirLbl.setText(WorkingDirectory.watchPath.getFileName().toAbsolutePath().toString());
		animDurationLbl.setText("Animation duration (sec) : "
				+ String.format("%02.4f", WorkingAnimation.animation.getAnimationDuration()));
		frameDurationLbl.setText("Frame duration     (sec) : "
				+ String.format("%02.4f", WorkingAnimation.framerate));
	}

	public void render() {
		stage.draw();
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		quitBtn.setPosition(stage.getWidth() - 72 - 5, 5);
		statusWindow.setSize(stage.getWidth(), statusWindow.getHeight());
		statusWindow.setPosition(0, stage.getHeight());
	}

	public void dispose() {
		stage.dispose();
	}


	private void initializeWidgets() {
		// Buttons
		// --------------------------------------------------------------------
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
		// --------------------------------------------------------------------

		// Status window
		// --------------------------------------------------------------------
		watchDirLbl = new Label(WorkingDirectory.default_watch_dir, skin);
		animDurationLbl = new Label("Animation duration (sec) : 0.0000", skin);
		frameDurationLbl = new Label("Frame duration     (sec) : 0.0000", skin);

		statusWindow = new Window("Status", skin);
		statusWindow.row(); statusWindow.add(watchDirLbl)     .width(stage.getWidth()).align(Align.left);
		statusWindow.row(); statusWindow.add(animDurationLbl) .width(stage.getWidth()).align(Align.left);
		statusWindow.row(); statusWindow.add(frameDurationLbl).width(stage.getWidth()).align(Align.left);
		statusWindow.pack();
		statusWindow.setPosition(0, stage.getHeight());
		statusWindow.setTitleAlignment(Align.left);
		statusWindow.padLeft(5);
		statusWindow.left();
		// --------------------------------------------------------------------

		// Add to stage
		// --------------------------------------------------------------------
		stage.addActor(loadAnimBtn);
		stage.addActor(clearAnimBtn);
		stage.addActor(quitBtn);
		stage.addActor(statusWindow);
		// --------------------------------------------------------------------
	}

}
