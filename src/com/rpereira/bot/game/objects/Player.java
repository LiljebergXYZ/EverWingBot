package com.rpereira.bot.game.objects;

import com.rpereira.bot.game.Game;

public class Player extends GameObject {

	private static final int X_MID = (int) (Game.X_TOP_LEFT + Game.GAME_WIDTH * 0.5f);
	private static final int Y_MID = (int) (Game.Y_TOP_LEFT + Game.GAME_HEIGHT * 0.85f);

	public Player() {
		super();
		this.respawn();
	}

	public void respawn() {
		super.getBox().set(X_MID, Y_MID, Game.SLOT_X, Game.SLOT_Y);
	}

	public void update(Game game) {
		int nextX = this.findNextSafePosition(game);

		if (nextX == super.getBox().getMinX()) {
			return;
		}

		// select character
		game.moveMouse(this.getBox().getCenterX(), this.getBox().getCenterY());
		game.pressMouse();

		// move it to next safe position
		this.getBox().setMinX(this.findNextSafePosition(game));
		game.moveMouse(this.getBox().getCenterX(), this.getBox().getCenterY());

		// unpress
		game.unpressMouse();
	}

	public int findNextSafePosition(Game game) {
		if (true) {
			return (this.getBox().getMinX());
		}
		long t = System.currentTimeMillis();
		if (t % 5000 < 200)
			return (X_MID);
		if (t % 200 < 100)
			return ((int) (X_MID - Game.GAME_WIDTH * 0.4f));
		return ((int) (X_MID + Game.GAME_WIDTH * 0.4f));
	}
}
