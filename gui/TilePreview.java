package me.simon.gui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;


public class TilePreview extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage image;
	
	public TilePreview(BufferedImage image) {
		this.image = image;
		Dimension dim = new Dimension(128,128);
		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, 128, 128, null);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
