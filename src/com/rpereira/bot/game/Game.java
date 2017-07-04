package com.rpereira.bot.game;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jnativehook.keyboard.NativeKeyEvent;

import com.rpereira.bot.game.objects.GameObject;
import com.rpereira.bot.game.objects.Player;
import com.rpereira.bot.game.recognition.ImageRecognizer;
import com.rpereira.bot.game.recognition.ImageRecognizerTest;

public class Game {

	// screen resolution
	public static final Dimension SCREEN_DIM = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int SCREEN_WIDTH = SCREEN_DIM.width;
	public static final int SCREEN_HEIGHT = SCREEN_DIM.height;

	// TODO : check if these coordinates are the same on each screen

	// top left corner of the game coordinate
	public static final int X_TOP_LEFT = (int) (SCREEN_WIDTH * 0.37375f);
	public static final int Y_TOP_LEFT = (int) (SCREEN_HEIGHT * 0.12666666f);

	// bottom right corner of the game coordinate
	public static final int X_BOT_RIGHT = (int) (SCREEN_WIDTH * 0.61875f);
	public static final int Y_BOT_RIGHT = (int) (SCREEN_HEIGHT * 0.9066666f);

	public static final int GAME_WIDTH = X_BOT_RIGHT - X_TOP_LEFT;
	public static final int GAME_HEIGHT = Y_BOT_RIGHT - Y_TOP_LEFT;

	public static final int SLOT_X = GAME_WIDTH / 5;
	public static final int SLOT_Y = SLOT_X;

	/** game attributes */

	private Robot bot; // java robot to handle mouse events
	private Player player; // the player
	private ImageRecognizer recognizer;
	private BufferedImage screen; // the screen
	private BufferedImage game; // the game
	private ArrayList<GameObject> missiles; // unused

	public Game() throws AWTException {
		this.player = new Player();
		this.recognizer = new ImageRecognizerTest();
		this.bot = new Robot();
		this.missiles = new ArrayList<GameObject>();
		KeyboardEvents.init(); // init keyboard events
	}

	public void click(int x, int y) {
		this.moveMouse(x, y);
		this.pressMouse();
		this.unpressMouse();
	}

	public void pressMouse() {
		this.bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.bot.delay(1);
	}

	public void unpressMouse() {
		this.bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		this.bot.delay(1);
	}

	public void moveMouse(int x, int y) {
		this.bot.mouseMove(x, y);
		this.bot.delay(1);
	}

	/** get a pixel of the screen */
	public final int getScreenPixel(int x, int y) {
		return (this.screen.getRGB(x, y));
	}

	/** is running */
	public boolean isRunning() {
		boolean running = !KeyboardEvents.isPressed(NativeKeyEvent.VC_ESCAPE);
		return (running);
	}

	/** to be called when a new game starts */
	public final void restart() {
		this.click((int) (X_TOP_LEFT + GAME_WIDTH * 0.5f), (int) (Y_TOP_LEFT + GAME_HEIGHT * 0.7f));
		this.player.respawn();
	}

	/** to be called to end score screen after the game is lost */
	public final void end() {
		this.click((int) (X_TOP_LEFT + GAME_WIDTH * 0.25f), (int) (Y_TOP_LEFT + GAME_HEIGHT * 0.85f));
	}

	/**
	 * check pixels to know if the game is focused (in facebook and launched)
	 */
	// TODO :
	public boolean isFocused() {
		return (this.getScreenPixel(27, 12) == -12033373 && this.getScreenPixel(97, 277) == -15263977);
	}

	/** check pixels to know if the game is in the main menu */
	// TODO :
	public boolean isInMainMenu() {
		return (this.getScreenPixel(638, 170) == -1150245 && this.getScreenPixel(938, 136) == -1);
	}

	/** check pixels to know if the game is lost */
	// TODO :
	public final boolean isLost() {
		return (this.getScreenPixel(640, 720) == -16601115 && this.getScreenPixel(835, 720) == -10889472);
	}

	/** called on each frame */
	public void updateScreen() {
		// get screenshot
		this.screen = this.bot.createScreenCapture(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT));
	}

	/** update the game (when in game) */
	public void update() {
		this.game = this.screen.getSubimage(X_TOP_LEFT, Y_TOP_LEFT, GAME_WIDTH, GAME_HEIGHT);
		this.recognizer.setImage(this.game);
		this.recognizer.recognize();
		this.player.update(this);
	}

	public ArrayList<GameObject> getGameObjects() {
		return (this.missiles);
	}

	public void stop() {
		KeyboardEvents.deinit();
		this.recognizer.plot();
	}
}
