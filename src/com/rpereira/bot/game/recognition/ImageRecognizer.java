package com.rpereira.bot.game.recognition;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.rpereira.bot.game.objects.GameObject;

public abstract class ImageRecognizer {

	private ArrayList<GameObject> gameObjects;
	private BufferedImage image;

	public ImageRecognizer() {
		this.gameObjects = new ArrayList<GameObject>();
		this.image = null;
	}

	/** add a game object */
	public final void addGameObject(GameObject gameObject) {
		this.gameObjects.add(gameObject);
	}

	/** set the image to recognize */
	public final void setImage(BufferedImage bufferedImage) {
		this.image = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
		this.image.setData(bufferedImage.getRaster());
		this.gameObjects.clear();
	}

	/** do the image recognition */
	public abstract void recognize();

	/** get the image recognized */
	public final BufferedImage getImage() {
		return (this.image);
	}

	public void plot() {
		this.plot(this.image);
	}

	/** show the recognized objects */
	public final void plot(BufferedImage image) {

		for (GameObject gameObject : this.gameObjects) {
			int minx = gameObject.getBox().getMinX();
			int maxx = gameObject.getBox().getMaxX();
			int miny = gameObject.getBox().getMinY();
			int maxy = gameObject.getBox().getMaxY();

			
			for (int x = minx; x < maxx; x++) {
				image.setRGB(x, miny, 0xFF0000FF);
				image.setRGB(x, maxy, 0xFF0000FF);
			}

			for (int y = miny; y < maxy; y++) {
				image.setRGB(minx, y, 0xFF0000FF);
				image.setRGB(maxx, y, 0xFF0000FF);
			}
		}

		// Use a label to display the image
		JFrame frame = new JFrame();

		JLabel lblimage = new JLabel(new ImageIcon(image));
		frame.getContentPane().add(lblimage, BorderLayout.CENTER);
		frame.setSize(image.getWidth(), image.getHeight() + 20);
		frame.setVisible(true);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(lblimage);
		// add more components here
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

	public void cleanGameObjects() {
		this.gameObjects.clear();
	}
}
