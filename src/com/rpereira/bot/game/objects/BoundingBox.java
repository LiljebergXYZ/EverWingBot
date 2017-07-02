package com.rpereira.bot.game.objects;

public class BoundingBox {

	// top left coordinates
	public int x;
	public int y;

	// sizes
	public int width;
	public int height;

	public BoundingBox() {

	}

	public void set(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
