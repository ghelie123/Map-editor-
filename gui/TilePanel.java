package me.simon.gui;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import me.simon.Editor;
import me.simon.Tile;


public class TilePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private TilePreview tilePreview;
	private JList<String> placedTiles;
	private DefaultListModel<String> model;
	
	private JLabel xCoord;
	private JLabel yCoord;
	
	public TilePanel(Editor editor) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		// Create combo box
		String[] names = new String[editor.getImages().size() - 1];
		int index = 0;
		for (String name : editor.getImages().keySet()) {
			if (!name.equals("player")) {
				names[index] = name;
				index++;
			}
		}
		
		
		JPanel panel1 = new JPanel();
		panel1.add(new JLabel("Tile: "));
		JComboBox<String> tileSelect = new JComboBox<String>(names);
		tileSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> source = (JComboBox<String>) e.getSource();
				tilePreview.setImage(Editor.getInstance().getImages().get(source.getSelectedItem()));
				tilePreview.repaint();
				Editor.getInstance().getPreviewTile().setImage((String) source.getSelectedItem());
			}
			
		});
		panel1.add(tileSelect);
		
		add(panel1);
		
		tilePreview = new TilePreview(editor.getImages().get(tileSelect.getSelectedItem()));
		add(tilePreview);
		
		JPanel panel2 = new JPanel();
		panel2.add(new JLabel("X: "));
		panel2.add(new JTextField(5));
		panel2.add(new JLabel("Y: "));
		panel2.add(new JTextField(5));
		
		add(panel2);
		
		JPanel panel3 = new JPanel();
		panel3.add(new JLabel("WIDTH: "));
		panel3.add(new JTextField(5));
		panel3.add(new JLabel("HEIGHT: "));
		panel3.add(new JTextField(5));
		
		add(panel3);
		
		model = new DefaultListModel<String>();
		placedTiles = new JList<String>(model);
		placedTiles.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		placedTiles.setOpaque(true);
		placedTiles.setBackground(Color.WHITE);
		placedTiles.setFixedCellHeight(25);
		placedTiles.setFixedCellWidth(200);
		placedTiles.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					Editor.getInstance().getLevelView().setSelectedIndex(placedTiles.getSelectedIndex());
					Editor.getInstance().getLevelView().repaint();
				}
			}
			
		});
		placedTiles.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					int index = placedTiles.getSelectedIndex();
					if (index != -1) {
						Editor.getInstance().getLevel().removeTile(index);
						model.remove(index);
						Editor.getInstance().getLevelView().repaint();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
		});
		JScrollPane scrollPane = new JScrollPane(placedTiles);
		add(scrollPane);
		
		JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20,20));
		xCoord = new JLabel("x: 0");
		yCoord = new JLabel("y: 0");
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
		xCoord.setFont(font);
		yCoord.setFont(font);
		panel4.add(xCoord);
		panel4.add(yCoord);
		add(panel4);
		
		
		add(Box.createVerticalGlue());
		
		editor.setPreviewTile(new Tile(0,0,Editor.UNIT,Editor.UNIT, (String) tileSelect.getSelectedItem()));
	}
	
	public JList<String> getPlacedTileList() {
		return placedTiles;
	}
	
	public DefaultListModel<String> getPlacedTileListModel() {
		return model;
	}
	
	public JLabel getXCoordLabel() {
		return xCoord;
	}

	public JLabel getYCoordLabel() {
		return yCoord;
	}
}
