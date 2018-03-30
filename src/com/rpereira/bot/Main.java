package com.rpereira.bot;

import java.awt.AWTException;

import com.rpereira.bot.game.Game;

public class Main {

	public static void main(String[] args) throws AWTException, InterruptedException {

		System.out.println("Starting bot");

		Game game = new Game();

		while (game.isRunning()) {

			game.updateScreen();
			if (game.isFocused()) {
				if (game.isLost()) {
					game.end();
				} else if (game.isInMainMenu()) {
					game.restart();
				} else {
					game.update();
				}
			}
			Thread.sleep(100);
		}
		System.out.println("Stopping bot");
		game.stop();
		System.out.println("Bot stopped.");

	}
}
