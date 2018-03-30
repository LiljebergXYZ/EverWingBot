package com.rpereira.bot.game;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.toIntExact;

import org.jnativehook.keyboard.NativeKeyEvent;

import com.sun.jna.Platform;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.rpereira.bot.game.objects.GameObject;
import com.rpereira.bot.game.objects.Player;
import com.rpereira.bot.game.recognition.ImageRecognizer;
import com.rpereira.bot.game.recognition.ImageRecognizerTest;

public class Game {

	// screen resolution
	public static final Dimension SCREEN_DIM = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int SCREEN_WIDTH = SCREEN_DIM.width;
	public static final int SCREEN_HEIGHT = SCREEN_DIM.height;
	public static final String WINDOW_NAME = "Google Chrome";

	// TODO : check if these coordinates are the same on each screen

	// top left corner of the game coordinate
	public static final int X_TOP_LEFT = 123;
	public static final int Y_TOP_LEFT = 127;

	// bottom right corner of the game coordinate
	public static final int X_BOT_RIGHT = 598;
	public static final int Y_BOT_RIGHT = 972;

	public static final int GAME_WIDTH = 475;
	public static final int GAME_HEIGHT = 845;

	public static final int SLOT_X = GAME_WIDTH / 5;
	public static final int SLOT_Y = SLOT_X;

	/** game attributes */

	private Robot bot; // java robot to handle mouse events
	private Player player; // the player
	private ImageRecognizer recognizer;
	private BufferedImage screen; // the screen
	private BufferedImage game; // the game
	private ArrayList<GameObject> missiles; // unused

	public ArrayList<Long> windowPos; // window position

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
		if(this.screen == null) return 0;
		return (this.screen.getRGB(x, y));
	}

	/** is running */
	public boolean isRunning() {
		boolean running = !KeyboardEvents.isPressed(NativeKeyEvent.VC_ESCAPE);
		return (running);
	}

	/** to be called when a new game starts */
	public final void restart() {
		this.click((int) ((this.windowPos.get(0) + X_TOP_LEFT) + GAME_WIDTH * 0.5f), (int) ((this.windowPos.get(1) + Y_TOP_LEFT) + GAME_HEIGHT * 0.7f));
		this.player.respawn();
	}

	/** to be called to end score screen after the game is lost */
	public final void end() {
		this.click((int) ((this.windowPos.get(0) + X_TOP_LEFT) + GAME_WIDTH * 0.25f), (int) ((this.windowPos.get(1) + Y_TOP_LEFT) + GAME_HEIGHT * 0.85f));
	}

	private String getFocusedProgram() {
		if(Platform.isMac()) {
			final String programFocused = "tell application \"System Events\"\n" +
					"\tname of application processes whose frontmost is true\n" +
					"end";

			try {
				ScriptEngineManager engineManager = new ScriptEngineManager();
        		ScriptEngine appleScript = engineManager.getEngineByName("AppleScriptEngine");
				Object eval = appleScript.eval(programFocused);
				ArrayList<String> arrayList = (ArrayList)eval;
				String result = (String)arrayList.get(0);

				return result;
			} catch(ScriptException se) {
				return null;
			}
		}
		return null;
	}

	private ArrayList<Long> getwindowPosition() {
		if(Platform.isMac()) {
			final String coordinates = "tell application \"System Events\"\n" +
			"set the props to get the properties of the front window of application process \"" + WINDOW_NAME + "\"\n" +
			"set theSBounds to {size, position} of props\n" +
			"end tell";

			try {
				ScriptEngineManager engineManager = new ScriptEngineManager();
        		ScriptEngine appleScript = engineManager.getEngineByName("AppleScriptEngine");
				Object eval = appleScript.eval(coordinates);
				ArrayList<ArrayList<Long>> arrayList = (ArrayList)eval;
				ArrayList<Long> size = (ArrayList<Long>)arrayList.get(0);
				ArrayList<Long> pos = (ArrayList<Long>)arrayList.get(1);
				if(size.get(0) != 720 || size.get(1) != 1024) {
					this.setProgramSize(pos);
				}
				return pos;
			} catch(ScriptException se) {
				return null;
			}
		}
		return null;
	}

	private boolean setProgramSize(ArrayList<Long> pos) {
		if(Platform.isMac()) {
			final String coordinates = "set xAxis to " + pos.get(0) + "\n" +
			"set yAxis to " + pos.get(1) + "\n" +
			"tell application \"" + WINDOW_NAME + "\"\n" +
			"set bounds of front window to {xAxis, yAxis, 720 + xAxis, 1024 + yAxis}\n" +
			"end tell";

			try {
				ScriptEngineManager engineManager = new ScriptEngineManager();
        		ScriptEngine appleScript = engineManager.getEngineByName("AppleScriptEngine");
				appleScript.eval(coordinates);
				return true;
			} catch(ScriptException se) {
				return false;
			}
		}
		return false;
	}

	/**
	 * check pixels to know if the game is focused (in facebook and launched)
	 */
	// TODO :
	public boolean isFocused() {
		if(this.getFocusedProgram().equals(WINDOW_NAME)) {
			if(this.windowPos == null) {
				this.windowPos = this.getwindowPosition();
			}
			return true;
			//return (this.getScreenPixel(27, 12) == -12033373 && this.getScreenPixel(97, 277) == -15263977);
		}
		this.windowPos = null;
		return false;
	}

	/** check pixels to know if the game is in the main menu */
	// TODO :
	public boolean isInMainMenu() {
		return (this.getScreenPixel(145, 132) == -3183376 &&
		this.getScreenPixel(147, 821) == -197893);
		//return (this.getScreenPixel(638, 170) == -1150245 && this.getScreenPixel(938, 136) == -1);
	}

	/** check pixels to know if the game is lost */
	// TODO :
	public final boolean isLost() {
		return (this.getScreenPixel(177, 840) == -14835229 &&
		this.getScreenPixel(415, 840) == -11610873);
		//return (this.getScreenPixel(640, 720) == -16601115 && this.getScreenPixel(835, 720) == -10889472);
	}

	/** called on each frame */
	public void updateScreen() {
		// get screenshot
		if(this.windowPos != null) {
			this.screen = this.bot.createScreenCapture(new Rectangle(toIntExact(this.windowPos.get(0)), toIntExact(this.windowPos.get(1)), 720, 1024));
		}
	}

	/** update the game (when in game) */
	public void update() {
		if(this.screen == null) return;
		this.game = this.screen.getSubimage(X_TOP_LEFT, Y_TOP_LEFT, GAME_WIDTH, GAME_HEIGHT);
		this.recognizer.recognize(this.game);
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
