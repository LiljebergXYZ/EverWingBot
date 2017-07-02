package com.rpereira.bot.game.objects;

/** represent a game object */
public abstract class GameObject {

	private BoundingBox box;

	public GameObject() {
		this.box = new BoundingBox();
	}

	public BoundingBox getBox() {
		return (this.box);
	}

	public int getX() {
		return (this.box.x);
	}

	public int getY() {
		return (this.box.y);
	}

	public void setX(int x) {
		this.box.x = x;
	}

	public void setY(int y) {
		this.box.y = y;
	}
}
