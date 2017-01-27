package me.simon.gui;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.event.MouseInputListener;

import me.simon.Editor;
import me.simon.Tile;


public class LevelView extends JPanel implements Scrollable, MouseInputListener {

	private static final long serialVersionUID = 1L;
	
	private Dimension dimension;
	
	private boolean mouseInside;
	
	private int selectedIndex;
	private boolean showGrid;
	private boolean showNumbers = true;
	
	private Point mouseLocation;
	
	public LevelView(Editor editor) {
		dimension = new Dimension(editor.getLevel().getWidth() * Editor.UNIT, editor.getLevel().getHeight() * Editor.UNIT);
		setPreferredSize(dimension);
		addMouseListener(this);
		addMouseMotionListener(this);
		selectedIndex = -1;
		showGrid = true;
		mouseLocation = new Point(0,0);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		ArrayList<Tile> tiles = Editor.getInstance().getLevel().getTiles();
		for (int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			g2d.drawImage(tile.getImage(), tile.getXPos(), tile.getYPos(), tile.getWidth(), tile.getHeight(), null);
		}
		
		// Draw Player
		Point playerPos = Editor.getInstance().getPlayerPosition();
		g2d.drawImage(Editor.getInstance().getImages().get("player"), playerPos.x, playerPos.y, Editor.UNIT, Editor.UNIT * 2, null);
		
		// Draw mouse preview
		if (mouseInside) {
			Tile tile = Editor.getInstance().getPreviewTile();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.drawImage(tile.getImage(), tile.getXPos(), tile.getYPos(), tile.getWidth(), tile.getHeight(), null);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
		
		if (showGrid) {
			// Draw grid
			int i = 0;
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
			for (int x = 0; x < getWidth(); x += Editor.UNIT) {
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawLine(x, 0, x, getHeight());
				if (showNumbers) {
					g2d.setColor(Color.WHITE);
					g2d.drawString(String.valueOf(i++), x + 2, 12);
				}
			}
			i = 0;
			for (int y = 0; y < getHeight(); y += Editor.UNIT) {
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawLine(0, y, getWidth(), y);
				if (showNumbers) {
					g2d.setColor(Color.WHITE);
					g2d.drawString(String.valueOf(i++), 2, y + 12);
				}
			}
		}
		
		if (selectedIndex != -1) {
			g2d.setStroke(new BasicStroke(5));
			g2d.setColor(new Color(0x6600CC));
			Tile tile = tiles.get(selectedIndex);
			g2d.drawRect(tile.getXPos(), tile.getYPos(), tile.getWidth(), tile.getHeight());
		}
		
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return dimension;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 128;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 128;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON2) {
			Point pos = new Point(snapToGrid(mouseLocation.x), snapToGrid(mouseLocation.y));
			Editor.getInstance().setPlayerPosition(pos);
			Editor.saved = false;
			repaint();
		}
		if (e.getButton() == MouseEvent.BUTTON1) {
			Tile tile = Editor.getInstance().getPreviewTile();
			Editor.getInstance().getLevel().addTile(new Tile(tile.getXPos(), tile.getYPos(), tile.getWidth(), tile.getHeight(), tile.getImagePath()));
			repaint();
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			ArrayList<Tile> tiles = Editor.getInstance().getLevel().getTiles();
			for (int i = 0; i < tiles.size(); i++) {
				if (snapToGrid(mouseLocation.x) == tiles.get(i).getXPos() && snapToGrid(mouseLocation.y) == tiles.get(i).getYPos()) {
					Editor.getInstance().getLevel().removeTile(i);
					repaint();
					break;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseInside = true;
		repaint();
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseInside = false;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Editor.getInstance().getPreviewTile().setXPos(snapToGrid(e.getX()));
		Editor.getInstance().getPreviewTile().setYPos(snapToGrid(e.getY()));
		mouseLocation = e.getPoint();
		Editor.getInstance().getTilePanel().getXCoordLabel().setText("x: " + (snapToGrid(e.getX()) / Editor.UNIT));
		Editor.getInstance().getTilePanel().getYCoordLabel().setText("y: " + (snapToGrid(e.getY()) / Editor.UNIT));
		repaint();
	}
	
	private int snapToGrid(int value) {
		return (value - (value % Editor.UNIT));
	}
	
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	public void setGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
	
	public void setNumbers(boolean showNumbers) {
		this.showNumbers = showNumbers;
	}
	
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
		setPreferredSize(dimension);
		revalidate();
		repaint();
	}
	
}
