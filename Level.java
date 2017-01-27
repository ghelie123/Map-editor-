package me.simon;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;


public class Level {

	private int width;
	private int height;
	
	private ArrayList<Tile> tiles;
	
	private File file;
	
	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		tiles = new ArrayList<Tile>();
	}
	
	public Level(int width, int height, ArrayList<Tile> tiles) {
		this.width = width;
		this.height = height;
		this.tiles = tiles;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setWidth(int width) {
		this.width = width;
		Editor.saved = false;
	}
	
	public void setHeight(int height) {
		this.height = height;
		Editor.saved = false;
	}
	
	public void addTile(Tile tile) {
		tiles.add(tile);
		Editor.getInstance().getTilePanel().getPlacedTileListModel().addElement(tile.toString());
		Editor.saved = false;
	}
	
	public void removeTile(int index) {
		tiles.remove(index);
		Editor.getInstance().getTilePanel().getPlacedTileListModel().removeElementAt(index);
		Editor.saved = false;
	}
	
	public ArrayList<Tile> getTiles() {
		return tiles;
	}
	
	public ArrayList<Integer> getOffscreenTiles(Dimension dimension) {
		ArrayList<Integer> offscreen = new ArrayList<Integer>();
		Rectangle newPanelBounds = new Rectangle(dimension);
		for (int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			if (!newPanelBounds.contains(new Point(tile.getXPos(), tile.getYPos()))) {
				offscreen.add(i);
			}
		}
		
		return offscreen;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
}
