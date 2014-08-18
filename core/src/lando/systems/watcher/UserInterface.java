package lando.systems.watcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
	TextButton settingsBtn;
	TextButton quitBtn;

	ImageButton playPauseBtn;
	ImageButton nextFrameBtn;
	ImageButton prevFrameBtn;

	Window statusWindow;
	Label watchDirLbl;
	Label animDurationLbl;
	Label frameDurationLbl;

	Window settingsWindow;
	Slider bgRedSlider;
	Slider bgGreenSlider;
	Slider bgBlueSlider;
	SelectBox<String> animPlayModeSelect;
	ImageButton fasterBtn;
	ImageButton slowerBtn;

	Texture playUp, playDown;
	Texture pauseUp, pauseDown;
	Texture nextFrameUp, nextFrameDown;
	Texture prevFrameUp, prevFrameDown;
	Texture plusUp, plusDown;
	Texture minusUp, minusDown;

	Color background;
	PlayMode animPlayMode;

	final float margin_x = 5;
	final float margin_y = 5;
	final float button_width = 72;
	final float button_height = 32;
	final float settings_window_width = 150;


	public UserInterface(AppState state) {
		appState = state;
		skin  = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new ScreenViewport());

		playUp        = new Texture(Gdx.files.internal("play-up.png"));
		playDown      = new Texture(Gdx.files.internal("play-down.png"));
		pauseUp       = new Texture(Gdx.files.internal("pause-up.png"));
		pauseDown     = new Texture(Gdx.files.internal("pause-down.png"));
		nextFrameUp   = new Texture(Gdx.files.internal("next-up.png"));
		nextFrameDown = new Texture(Gdx.files.internal("next-down.png"));
		prevFrameUp   = new Texture(Gdx.files.internal("prev-up.png"));
		prevFrameDown = new Texture(Gdx.files.internal("prev-down.png"));
		plusUp        = new Texture(Gdx.files.internal("plus-up.png"));
		plusDown      = new Texture(Gdx.files.internal("plus-down.png"));
		minusUp       = new Texture(Gdx.files.internal("minus-up.png"));
		minusDown     = new Texture(Gdx.files.internal("minus-down.png"));

		background = new Color(0.1f, 0.1f, 0.1f, 1);
		animPlayMode = PlayMode.LOOP;

		initializeWidgets();
	}


	public void update(float delta) {
		stage.act(delta);

		watchDirLbl.setText(WorkingDirectory.watchPath.getFileName().toAbsolutePath().toString());

		animDurationLbl.setText("Animation duration (sec) : "
				+ String.format("%02.4f", WorkingAnimation.animation.getAnimationDuration()));
		frameDurationLbl.setText("Frame duration     (sec) : "
				+ String.format("%02.4f", WorkingAnimation.framerate));

		if (fasterBtn.isPressed()) {
			WorkingAnimation.framerate -= WorkingAnimation.frame_step_big;
			if (WorkingAnimation.framerate <= WorkingAnimation.frame_rate_min)
				WorkingAnimation.framerate  = WorkingAnimation.frame_rate_min;
		}
		if (slowerBtn.isPressed()) {
			WorkingAnimation.framerate += WorkingAnimation.frame_step_big;
			// Clamp the framerate to a maximum value?
		}
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
		settingsBtn.setPosition(stage.getWidth() - 2 * button_width - 2 * margin_x, margin_y);

		statusWindow.setSize(stage.getWidth(), statusWindow.getHeight());
		statusWindow.setPosition(0, stage.getHeight());

		settingsWindow.setSize(settings_window_width, stage.getHeight() - quitBtn.getHeight() - statusWindow.getHeight() - 2 * margin_y);
		settingsWindow.setPosition(stage.getWidth(), quitBtn.getHeight() + 2 * margin_y);
	}

	public void dispose() {
		minusDown.dispose();
		minusUp.dispose();
		plusDown.dispose();
		plusUp.dispose();
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
		initializeButtons();
		initializeStatusWindow();
		initializeSettingsWindow();
	}

	private void initializeButtons() {
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

		settingsBtn = new TextButton("Settings", skin);
		settingsBtn.setPosition(stage.getWidth() - 2 * button_width - 2 * margin_x, margin_y);
		settingsBtn.setSize(button_width, button_height);
		settingsBtn.addListener(new InputListener() {
			boolean settingsWindowVisible = true;
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (settingsWindowVisible) {
					settingsWindowVisible= false;
					settingsWindow.setKeepWithinStage(false);
					settingsWindow.setPosition(stage.getWidth(), quitBtn.getHeight() + 2 * margin_y);
				} else {
					settingsWindowVisible = true;
					settingsWindow.setKeepWithinStage(true);
					settingsWindow.setPosition(stage.getWidth(), quitBtn.getHeight() + 2 * margin_y);
				}
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

		stage.addActor(loadAnimBtn);
		stage.addActor(clearAnimBtn);
		stage.addActor(playPauseBtn);
		stage.addActor(nextFrameBtn);
		stage.addActor(prevFrameBtn);
		stage.addActor(settingsBtn);
		stage.addActor(quitBtn);
	}

	private void initializeStatusWindow() {
		watchDirLbl = new Label(WorkingDirectory.default_watch_dir, skin);
		animDurationLbl = new Label("Animation duration (sec) : 0.0000", skin);
		frameDurationLbl = new Label("Frame duration     (sec) : 0.0000", skin);

		statusWindow = new Window("Status", skin);
		statusWindow.row();
		statusWindow.add(watchDirLbl)     .width(stage.getWidth()).align(Align.left);
		statusWindow.row();
		statusWindow.add(animDurationLbl) .width(stage.getWidth()).align(Align.left);
		statusWindow.row();
		statusWindow.add(frameDurationLbl).width(stage.getWidth()).align(Align.left);
		statusWindow.pack();
		statusWindow.setPosition(0, stage.getHeight());
		statusWindow.setTitleAlignment(Align.left);
		statusWindow.padLeft(margin_x);
		statusWindow.left();
		// --------------------------------------------------------------------

		stage.addActor(statusWindow);
	}

	private void initializeSettingsWindow() {
		Label backgroundColorLabel = new Label("Background Color", skin);
		Label bgColorR = new Label("Red", skin);
		Label bgColorG = new Label("Green", skin);
		Label bgColorB = new Label("Blue", skin);

		bgRedSlider   = new Slider(0, 1, 0.025f, false, skin);
		bgGreenSlider = new Slider(0, 1, 0.025f, false, skin);
		bgBlueSlider  = new Slider(0, 1, 0.025f, false, skin);

		bgRedSlider  .setValue(background.r);
		bgGreenSlider.setValue(background.g);
		bgBlueSlider .setValue(background.b);

		bgRedSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				background.r = bgRedSlider.getValue();
			}
		});
		bgGreenSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				background.g = bgGreenSlider.getValue();
			}
		});
		bgBlueSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				background.b = bgBlueSlider.getValue();
			}
		});

		Label animModeSelectLabel = new Label("Animation Mode", skin);
		animPlayModeSelect = new SelectBox<String>(skin);
		animPlayModeSelect.setItems("Normal", "PingPong", "Reverse");
		animPlayModeSelect.setSelected("Normal");
		animPlayModeSelect.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				final String selectedMode = animPlayModeSelect.getSelected();
				if      (selectedMode.equals("Normal"))   animPlayMode = PlayMode.LOOP;
				else if (selectedMode.equals("PingPong")) animPlayMode = PlayMode.LOOP_PINGPONG;
				else if (selectedMode.equals("Reverse"))  animPlayMode = PlayMode.LOOP_REVERSED;
				appState.workingAnimation.changePlayMode(animPlayMode);
			}
		});

		Label animSpeedLabel = new Label("Animation Speed", skin);
		fasterBtn = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(plusUp)),
				new TextureRegionDrawable(new TextureRegion(plusDown)));
		fasterBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				// Polling handled in update()
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				WorkingAnimation.refresh();
			}
		});
		slowerBtn = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(minusUp)),
				new TextureRegionDrawable(new TextureRegion(minusDown)));
		slowerBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				// Polling handled in update()
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				WorkingAnimation.refresh();
			}
		});

		settingsWindow = new Window("Settings", skin);
		settingsWindow.row();
		settingsWindow.add(backgroundColorLabel).colspan(2).width(settings_window_width).padLeft(margin_x);
		settingsWindow.row().padRight(margin_x);
		settingsWindow.add(bgColorR)      .colspan(2).width(settings_window_width - 2*margin_x).align(Align.left)  .padLeft(margin_x).padRight(margin_x);
		settingsWindow.row().padRight(margin_x);
		settingsWindow.add(bgRedSlider)   .colspan(2).width(settings_window_width - 2*margin_x).align(Align.center).padLeft(margin_x).padRight(margin_x);
		settingsWindow.row().padRight(margin_x);
		settingsWindow.add(bgColorG)      .colspan(2).width(settings_window_width - 2*margin_x).align(Align.left)  .padLeft(margin_x).padRight(margin_x);
		settingsWindow.row().padRight(margin_x);
		settingsWindow.add(bgGreenSlider) .colspan(2).width(settings_window_width - 2*margin_x).align(Align.center).padLeft(margin_x).padRight(margin_x);
		settingsWindow.row().padRight(margin_x);
		settingsWindow.add(bgColorB)      .colspan(2).width(settings_window_width - 2*margin_x).align(Align.left)  .padLeft(margin_x).padRight(margin_x);
		settingsWindow.row().padRight(margin_x);
		settingsWindow.add(bgBlueSlider)  .colspan(2).width(settings_window_width - 2*margin_x).align(Align.center).padLeft(margin_x).padRight(margin_x);
		settingsWindow.row();
		settingsWindow.add(new Label(" ", skin)).colspan(2).expandX();
		settingsWindow.row();
		settingsWindow.add(animModeSelectLabel).colspan(2).width(settings_window_width).padLeft(margin_x);
		settingsWindow.row().padRight(margin_x);
		settingsWindow.add(animPlayModeSelect).colspan(2).width(settings_window_width - 2*margin_x).align(Align.center).padLeft(margin_x).padRight(margin_x);
		settingsWindow.row();
		settingsWindow.add(new Label(" ", skin)).colspan(2).expandX();
		settingsWindow.row();
		settingsWindow.add(animSpeedLabel).colspan(2).width(settings_window_width).padLeft(margin_x);
		settingsWindow.row();
		settingsWindow.add(slowerBtn).align(Align.center).colspan(1).padLeft(margin_x);
		settingsWindow.add(fasterBtn).align(Align.center).colspan(1).padLeft(margin_x);
		settingsWindow.pack();
		settingsWindow.setSize(settings_window_width, stage.getHeight() - quitBtn.getHeight() - statusWindow.getHeight() - 2 * margin_y);
		settingsWindow.setPosition(stage.getWidth(), quitBtn.getHeight() + 2 * margin_y);
		settingsWindow.top();
		// --------------------------------------------------------------------

		stage.addActor(settingsWindow);
	}

}
