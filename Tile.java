package me.simon;
import java.awt.image.BufferedImage;


public class Tile {
	
	private int xPos;
	private int yPos;
	
	private int width;
	private int height;
	
	private String image;
	
	public Tile(int xPos, int yPos, int width, int height, String image) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.image = image;
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BufferedImage getImage() {
		return Editor.getInstance().getImages().get(image);
	}
	
	public String getImagePath() {
		return image;
	}

	public void setXPos(int xPos) {
		this.xPos = xPos;
	}

	public void setYPos(int yPos) {
		this.yPos = yPos;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	@Override
	public String toString() {
		return image + " " + xPos / Editor.UNIT + " " + yPos / Editor.UNIT + " " + width / Editor.UNIT + " " + height / Editor.UNIT;
		
	}
	

}
