package com.rpereira.bot.game;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/** handle keyboard events, register keys by adding them into the enum */
public class KeyboardEvents implements NativeKeyListener {

	// it looks like NATIVE_KEY_LAST is wrong, so i set a big
	// number....0111123456789ab
	// private static final boolean[] PRESSED_MAP = new
	// boolean[NativeKeyEvent.NATIVE_KEY_LAST];
	private static final boolean[] PRESSED_MAP = new boolean[60000];
	private static boolean INIT = false;

	public final static boolean isPressed(int key) {
		return (PRESSED_MAP[key]);
	}

	public void nativeKeyPressed(NativeKeyEvent e) {
		PRESSED_MAP[e.getKeyCode()] = true;
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		PRESSED_MAP[e.getKeyCode()] = false;
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
	}

	public final static void init() {
		if (INIT) {
			return;
		}
		INIT = true;

		// Get the logger for "org.jnativehook" and set the level to warning.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);

		// Don't forget to disable the parent handlers.
		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(new KeyboardEvents());
	}

	public static void deinit() {
		if (!INIT) {
			return;
		}
		INIT = false;
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
}
