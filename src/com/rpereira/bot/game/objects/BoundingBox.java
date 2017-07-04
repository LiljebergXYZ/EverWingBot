package com.rpereira.bot.game.objects;

public class BoundingBox {

	// top left coordinates
	private int minx;
	private int miny;

	// bottom right coordinates
	private int maxx;
	private int maxy;

	// center
	private int centerx;
	private int centery;

	// width, height
	private int width;
	private int height;

	public BoundingBox() {

	}

	public final void set(int minx, int miny, int maxx, int maxy) {
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;
		this.centerx = (int) ((this.maxx + this.minx) * 0.5f);
		this.centery = (int) ((this.maxy + this.miny) * 0.5f);
		this.width = this.maxx - this.minx;
		this.height = this.maxy - this.miny;
	}

	public final void setMinX(int minx) {
		this.set(minx, this.miny, this.maxx, this.maxy);
	}

	public final void setMinY(int miny) {
		this.set(this.minx, miny, this.maxx, this.maxy);
	}

	public final void setMaxX(int maxx) {
		this.set(this.minx, this.miny, maxx, this.maxy);
	}

	public final void setMaxY(int maxy) {
		this.set(this.minx, this.miny, this.maxx, maxy);
	}

	public final int getMinX() {
		return (this.minx);
	}

	public final int getMinY() {
		return (this.miny);
	}

	public final int getMaxX() {
		return (this.maxx);
	}

	public final int getMaxY() {
		return (this.maxy);
	}

	public final int getCenterX() {
		return (this.centerx);
	}

	public final int getCenterY() {
		return (this.centery);
	}

	public final int getWidth() {
		return (this.width);
	}

	public final int getHeight() {
		return (this.height);
	}
}
