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
}
