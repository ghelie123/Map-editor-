package me.simon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import me.simon.dialog.NewLevelDialog;
import me.simon.dialog.ResizeDialog;
import me.simon.gui.LevelView;
import me.simon.gui.TilePanel;


public final class Editor extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final int UNIT = 50;
	public static final String VERSION = "1.1";
	
	private static Editor instance = new Editor();
	
	private HashMap<String, BufferedImage> images;
	private Level level;
	private Tile previewTile;
	private TilePanel tilePanel;
	private LevelView levelView;
	private JFileChooser fc;
	private Point playerPosition;
	
	public static boolean saved;

	public static void main(String[] args) {
	}
	
	private Editor() {
		super("JCE Editor v" + VERSION);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,500);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		images = new HashMap<String, BufferedImage>();
		level = new Level(10, 10);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// Menu bar
		JMenuBar menuBar = new JMenuBar();
		
		// File Menu
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem newItem = new JMenuItem("New");
		newItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				newLevel();
			}
			
		});
		JMenuItem openItem = new JMenuItem("Open...");
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openLevel();
			}
		});
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
			
		});
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		saveAsItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
			
		});
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.addSeparator();
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		
		// Edit Menu
		JMenu editMenu = new JMenu("Edit");
		
		JMenuItem resizeItem = new JMenuItem("Resize...");
		resizeItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ResizeDialog();
			}
			
		});
		editMenu.add(resizeItem);
		
		final JCheckBoxMenuItem showGrid = new JCheckBoxMenuItem("Grid");
		showGrid.setSelected(true);
		showGrid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				levelView.setGrid(showGrid.isSelected());
				levelView.repaint();
			}
			
		});
		editMenu.addSeparator();
		editMenu.add(showGrid);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		setJMenuBar(menuBar);
		

		//Load player and icon
		try {
			BufferedImage image = ImageIO.read(getClass().getResource("res/player.png"));
			BufferedImage icon = ImageIO.read(getClass().getResource("res/icon.png"));
			images.put("player", image);
			setIconImage(icon);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		// Load tiles
		loadTiles("res/snow_level.png", "snow", 32, 32, new int[0]);
		loadTiles("res/water_level.png", "water", 32, 32, new int[0]);
		loadTiles("res/lava_level.png", "lava", 32, 32, new int[0]);
		loadTiles("res/cave_level.png", "cave", 32, 32, new int[0]);

		tilePanel = new TilePanel(this);
		tilePanel.setBorder(new EmptyBorder(10,10,10,10));
		add(tilePanel, BorderLayout.WEST);
		
		
		levelView = new LevelView(this);
		JScrollPane scrollPane = new JScrollPane(levelView);
		
		add(scrollPane, BorderLayout.CENTER);
		
		fc = new JFileChooser() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void approveSelection() {
				File file = getSelectedFile();
				
				if (file.exists() && getDialogType() == JFileChooser.SAVE_DIALOG) {
					Toolkit.getDefaultToolkit().beep();
					int value = JOptionPane.showConfirmDialog(null, "The file " + file.getName() + " already exists.  Do you wish to replace it?", "Confirm Overwrite", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if (value == JOptionPane.YES_OPTION) {
						super.approveSelection();
						return;
					}
					else if (value == JOptionPane.CANCEL_OPTION) {
						cancelSelection();
						return;
					}
					else {
						return;
					}
				}
				if (!file.exists() && getDialogType() == JFileChooser.OPEN_DIALOG) {
					return;
				}
				
				super.approveSelection();
			}
			
		};
		fc.setFileFilter(new LevelFileFilter());
		fc.setAcceptAllFileFilterUsed(false);
		
		playerPosition = new Point(0,0);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void loadTiles(String path, String name, int width, int height, int[] ignoreIndex) {
		int index = 0;
		int count = 0;

		BufferedImage sheet = null;
		
		try {
			sheet = ImageIO.read(getClass().getResource(path));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		int rows = sheet.getHeight() / height;
		int columns = sheet.getWidth() / width;
		
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {

				// Check ignore
				boolean ignore = false;
				for (int i = 0; i < ignoreIndex.length; i++) {
					if (ignoreIndex[i] == index) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					images.put(name + "_" + count, sheet.getSubimage(column * width, row * height, width, height));
					count++;
				}
				index++;
			}
		}
	}
	
	public void newLevel() {
		new NewLevelDialog();
	}
	
	public void openLevel() {
		int value = fc.showOpenDialog(null);
		
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			
			if (isValidFile(file)) {
				try {
					Level level = readFile(file);
					this.level = level;
					DefaultListModel<String> model = tilePanel.getPlacedTileListModel();
					ArrayList<Tile> tiles = level.getTiles();
					model.clear();
					
					for (int i = 0; i < tiles.size(); i++) {
						model.addElement(tiles.get(i).toString());
					}
					levelView.setDimension(new Dimension(level.getWidth() * UNIT, level.getHeight() * UNIT));
				} catch (FileNotFoundException e) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "An error has occured when trying to read from the file.  The file may be corrupted or may not be the correct type.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "An error has occured when trying to read from the file.  The file may be corrupted or may not be the correct type.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null, "An error has occured when trying to read from the file.  The file may be corrupted or may not be the correct type.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void save() {
		File file = level.getFile();
		if (file == null) {
			saveAs();
		}
		else {
			writeToFile(file);
			saved = true;
		}
	}
	
	public void saveAs() {
		int value = fc.showSaveDialog(null);
		
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			
			if (file.exists()) {
				if (isValidFile(file)) {
					writeToFile(file);
					saved = true;
					level.setFile(file);
				}
				else {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "An error has occured when trying to write to the file.  The file may be corrupted or may not be the correct type.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				if (file.getName().endsWith(".LEVEL")) {
					String newName = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 5);
					file = new File(newName + "level");
				} 
				else if (!file.getName().endsWith(".level")) {
					file = new File(file.getAbsolutePath() + ".level");
				}
				saved = true;
				writeToFile(file);
				level.setFile(file);
			}

		}
	}
	
	public static Editor getInstance() {
		return instance;
	}
	
	public HashMap<String, BufferedImage> getImages() {
		return images;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Tile getPreviewTile() {
		return previewTile;
	}
	
	public void setPreviewTile(Tile previewTile) {
		this.previewTile = previewTile;
	}
	
	public TilePanel getTilePanel() {
		return tilePanel;
	}
	
	public LevelView getLevelView() {
		return levelView;
	}
	
	public void writeToFile(File file) {
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
			output.writeChars("LEVEL");
			output.writeInt(level.getWidth());
			output.writeInt(level.getHeight());
			
			ArrayList<Tile> tiles = level.getTiles();
			output.writeInt(tiles.size());
			
			for (int i = 0; i < tiles.size(); i++) {
				Tile tile = tiles.get(i);
				output.writeInt(tile.getXPos() / UNIT);
				output.writeInt(tile.getYPos() / UNIT);
				output.writeInt(tile.getWidth() / UNIT);
				output.writeInt(tile.getHeight() / UNIT);
				output.writeInt(tile.getImagePath().length());
				output.writeChars(tile.getImagePath());
			}
			output.writeInt(playerPosition.x / UNIT);
			output.writeInt(playerPosition.y / UNIT);
			
			output.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isValidFile(File f) {
		try {
			DataInputStream input = new DataInputStream(new FileInputStream(f));
			String string = "";
			string += input.readChar();
			string += input.readChar();
			string += input.readChar();
			string += input.readChar();
			string += input.readChar();
			input.close();
			
			return (string.equals("LEVEL"));
		} 
		catch (FileNotFoundException e) {
			return false;
		} 
		catch (IOException e) {
			return false;
		}

	}
	
	public Level readFile(File file) throws FileNotFoundException, IOException {
		DataInputStream input = new DataInputStream(new FileInputStream(file));
		input.readChar();
		input.readChar();
		input.readChar();
		input.readChar();
		input.readChar();
		int width = input.readInt();
		int height = input.readInt();
		int numObjects = input.readInt();
		ArrayList<Tile> tiles = new ArrayList<Tile>();

		
		for (int i = 0; i < numObjects; i++) {
			int x = input.readInt() * UNIT;
			int y = input.readInt() * UNIT;
			int w = input.readInt() * UNIT;
			int h = input.readInt() * UNIT;
			String string = "";
			int sw = input.readInt();
			for (int j = 0; j < sw; j++) {
				string += input.readChar();
			}
			tiles.add(new Tile(x,y,w,h,string));
		}
		Level level = new Level(width, height, tiles);
		level.setFile(file);
		playerPosition = new Point(input.readInt() * UNIT, input.readInt() * UNIT);
		input.close();
		return level;
	}
	
	public Point getPlayerPosition() {
		return playerPosition;
	}
	
	public void setPlayerPosition(Point playerPosition) {
		this.playerPosition = playerPosition;
	}

}
