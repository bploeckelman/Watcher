package lando.systems.watcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

	ImageButton playPauseBtn;
	ImageButton nextFrameBtn;
	ImageButton prevFrameBtn;

	Window statusWindow;
	Label watchDirLbl;
	Label animDurationLbl;
	Label frameDurationLbl;

	Texture playUp, playDown;
	Texture pauseUp, pauseDown;
	Texture nextFrameUp, nextFrameDown;
	Texture prevFrameUp, prevFrameDown;

	final float margin_x = 5;
	final float margin_y = 5;
	final float button_width = 72;
	final float button_height = 32;


	public UserInterface(AppState state) {
		appState = state;
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new ScreenViewport());

		playUp = new Texture(Gdx.files.internal("play-up.png"));
		playDown = new Texture(Gdx.files.internal("play-down.png"));
		pauseUp = new Texture(Gdx.files.internal("pause-up.png"));
		pauseDown = new Texture(Gdx.files.internal("pause-down.png"));
		nextFrameUp = new Texture(Gdx.files.internal("next-up.png"));
		nextFrameDown = new Texture(Gdx.files.internal("next-down.png"));
		prevFrameUp = new Texture(Gdx.files.internal("prev-up.png"));
		prevFrameDown = new Texture(Gdx.files.internal("prev-down.png"));

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
		stage.getBatch().begin();
		appState.workingAnimation.renderUI(stage.getBatch());
		stage.getBatch().end();
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		quitBtn.setPosition(stage.getWidth() - button_width - margin_x, margin_y);
		statusWindow.setSize(stage.getWidth(), statusWindow.getHeight());
		statusWindow.setPosition(0, stage.getHeight());
	}

	public void dispose() {
		prevFrameDown.dispose();
		prevFrameUp.dispose();
		nextFrameDown.dispose();
		nextFrameUp.dispose();
		pauseDown.dispose();
		pauseUp.dispose();
		playDown.dispose();
		playUp.dispose();
		stage.dispose();
	}


	private void initializeWidgets() {
		// Buttons
		// --------------------------------------------------------------------
		loadAnimBtn = new TextButton("Load", skin);
		loadAnimBtn.setPosition(margin_x, margin_y);
		loadAnimBtn.setSize(button_width, button_height);
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

		clearAnimBtn = new TextButton("Clear", skin);
		clearAnimBtn.setPosition(margin_x + button_width + margin_x, margin_y);
		clearAnimBtn.setSize(button_width, button_height);
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

		quitBtn = new TextButton("Quit", skin);
		quitBtn.setPosition(stage.getWidth() - button_width - margin_x, margin_y);
		quitBtn.setSize(button_width, button_height);
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

		float left = 2 * (margin_x + button_width);
		playPauseBtn = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(pauseUp)),
				new TextureRegionDrawable(new TextureRegion(pauseDown)),
				new TextureRegionDrawable(new TextureRegion(playDown)));
		playPauseBtn.setPosition(left + margin_x + button_height + margin_x, margin_y);
		playPauseBtn.setChecked(true);
		playPauseBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				appState.workingAnimation.paused = !playPauseBtn.isChecked();
			}
		});

		nextFrameBtn = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(nextFrameUp)),
				new TextureRegionDrawable(new TextureRegion(nextFrameDown)));
		nextFrameBtn.setPosition(left + margin_x + button_height + margin_x + button_height + margin_x, margin_y);
		nextFrameBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (appState.workingAnimation.paused) {
					appState.workingAnimation.animTimer += WorkingAnimation.framerate;
				}
			}
		});

		prevFrameBtn = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(prevFrameUp)),
				new TextureRegionDrawable(new TextureRegion(prevFrameDown)));
		prevFrameBtn.setPosition(left + margin_x, margin_y);
		prevFrameBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (appState.workingAnimation.paused) {
					appState.workingAnimation.animTimer -= WorkingAnimation.framerate;
				}
			}
		});
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
		statusWindow.padLeft(margin_x);
		statusWindow.left();
		// --------------------------------------------------------------------

		// Add to stage
		// --------------------------------------------------------------------
		stage.addActor(loadAnimBtn);
		stage.addActor(clearAnimBtn);
		stage.addActor(quitBtn);
		stage.addActor(statusWindow);
		stage.addActor(playPauseBtn);
		stage.addActor(nextFrameBtn);
		stage.addActor(prevFrameBtn);
		// --------------------------------------------------------------------
	}

}
