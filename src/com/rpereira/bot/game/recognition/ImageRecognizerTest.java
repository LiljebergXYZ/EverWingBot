package com.rpereira.bot.game.recognition;

import java.awt.image.BufferedImage;
import java.util.Stack;

import com.rpereira.bot.game.objects.GameObject;

public class ImageRecognizerTest extends ImageRecognizer {

	/** the gradient color of the image (derivative of color) */
	private BufferedImage gradient;

	@Override
	public void recognize() {
		super.cleanGameObjects();
		this.updateGradient();
		this.parseGradient();
	}

	class GameObjectPixelNode {
		int x, y;

		GameObjectPixelNode(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/** flood fill the gradient to find hitboxes */
	private void parseGradient() {
		int w = this.gradient.getWidth();
		int h = this.gradient.getHeight();
		boolean[][] visited = new boolean[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {

				// if already visited
				if (visited[x][y]) {
					continue;
				}

				// if black pixel, set visited and continue ;
				if (this.gradient.getRGB(x, y) == 0) {
					visited[x][y] = true;
					continue;
				}

				// else start the recursivity, create a new game object
				GameObject gameObject = new GameObject() {
				};
				// game object AABB bounds, {minx, miny, maxx, maxy}
				int[] bounds = { x, y, x, y };

				// flood filling queue
				Stack<GameObjectPixelNode> queue = new Stack<GameObjectPixelNode>();
				queue.push(new GameObjectPixelNode(x, y));

				// flood fill the queue
				while (!queue.isEmpty()) {
					// get the next node to visit
					GameObjectPixelNode node = queue.pop();

					// mark it as visited
					visited[node.x][node.y] = true;

					// flood fill this node
					this.floodFillGameObjectBoundingBox(queue, w, h, bounds, visited, node.x + 1, node.y);
					this.floodFillGameObjectBoundingBox(queue, w, h, bounds, visited, node.x - 1, node.y);
					this.floodFillGameObjectBoundingBox(queue, w, h, bounds, visited, node.x, node.y + 1);
					this.floodFillGameObjectBoundingBox(queue, w, h, bounds, visited, node.x, node.y - 1);
				}

				// set the bounding box result
				gameObject.getBox().set(bounds[0], bounds[1], bounds[2], bounds[3]);

				// add the game object
				super.addGameObject(gameObject);

				int minx = gameObject.getBox().getMinX();
				int maxx = gameObject.getBox().getMaxX();
				int miny = gameObject.getBox().getMinY();
				int maxy = gameObject.getBox().getMaxY();

				//TODO fix me
				System.out.println("game object added: " + minx + " : " + miny + " : " + maxx + " : " + maxy + " : " + w
						+ " : " + h);
			}
		}
	}

	private void floodFillGameObjectBoundingBox(Stack<GameObjectPixelNode> queue, int w, int h, int[] bounds,
			boolean[][] visited, int x, int y) {

		// if out of bounds or alreadt visited, stop the recursivity
		if (x < 0 || x >= w || y < 0 || y >= h || visited[x][y] || this.gradient.getRGB(x, y) == 0) {
			return;
		}

		// check bounds to have min and max coordinates of game object AABB
		if (x < bounds[0]) {
			bounds[0] = x;
		} else if (x > bounds[2]) {
			bounds[2] = x;
		}

		if (y < bounds[1]) {
			bounds[1] = y;
		} else if (y > bounds[3]) {
			bounds[3] = y;
		}

		queue.add(new GameObjectPixelNode(x, y));
	}

	/** calculate the gradient image */
	private void updateGradient() {
		BufferedImage image = super.getImage();
		this.gradient = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

		for (int x = 0; x < this.gradient.getWidth(); x++) {
			for (int y = 0; y < this.gradient.getHeight(); y++) {
				int color = this.getGradient(image, x, y);
				this.gradient.setRGB(x, y, color);
			}
		}
	}

	@Override
	public void plot() {
		super.plot();
		this.plot(this.gradient);
	}

	/** get the gradient for the given image at the given coordinates */
	private int getGradient(BufferedImage image, int x, int y) {

		if (x <= 0 || x >= this.gradient.getWidth() - 1 || y <= 0 || y >= this.gradient.getHeight() - 1) {
			return (0);
		}

		int cx1 = image.getRGB(x - 1, y);
		int cx2 = image.getRGB(x + 1, y);
		int cy1 = image.getRGB(x, y - 1);
		int cy2 = image.getRGB(x, y + 1);

		int dxr = abs(getR(cx2) - getR(cx1));
		int dxg = abs(getG(cx2) - getG(cx1));
		int dxb = abs(getB(cx2) - getB(cx1));

		int dyr = abs(getR(cy2) - getR(cy1));
		int dyg = abs(getG(cy2) - getG(cy1));
		int dyb = abs(getB(cy2) - getB(cy1));

		float dr = (dxr + dyr) * 0.5f;
		float dg = (dxg + dyg) * 0.5f;
		float db = (dxb + dyb) * 0.5f;

		float f = max(0.0f, min(dr + dg + db, 255.0f)) / 255.0f;
		int u = (int) (255.0f * f * f);
		return (u < 50 ? 0 : intColor(u, u, u));
	}

	private static int interpolate(int c1, int c2, float f) {
		f = max(min(f, 1.0f), 0.0f);
		int r = (int) (f * getR(c1) + (1 - f) * getR(c2));
		int g = (int) (f * getG(c1) + (1 - f) * getG(c2));
		int b = (int) (f * getB(c1) + (1 - f) * getB(c2));
		return (intColor(r, g, b));
	}

	private static int intColor(int r, int g, int b) {
		return (r << 16 | g << 8 | b << 0);
	}

	private static int getR(int color) {
		return ((color & 0xFF0000) >> 16);
	}

	private static int getG(int color) {
		return ((color & 0x00FF00) >> 8);
	}

	private static int getB(int color) {
		return ((color & 0x0000FF) >> 0);
	}

	private static int abs(int x) {
		return (x < 0 ? -x : x);
	}

	private static int max(int x, int inf) {
		return (x > inf ? x : inf);
	}

	private static int min(int x, int sup) {
		return (x < sup ? x : sup);
	}

	private static float max(float x, float inf) {
		return (x > inf ? x : inf);
	}

	private static float min(float x, float sup) {
		return (x < sup ? x : sup);
	}
}
