package me.simon.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.simon.Editor;
import me.simon.Level;

public class NewLevelDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JTextField widthField;
	private JTextField heightField;

	public NewLevelDialog() {
		setTitle("New Level");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel panel1 = new JPanel();
		panel1.add(new JLabel("Width: "));
		widthField = new JTextField(5);
		panel1.add(widthField);
		
		panel1.add(new JLabel("Height: "));
		heightField = new JTextField(5);
		panel1.add(heightField);
		
		add(panel1);
		
		
		JPanel panel2 = new JPanel();
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int width = 0;
				int height = 0;
				try {
					width = Integer.parseInt(widthField.getText());
					height = Integer.parseInt(heightField.getText());
				}
				catch (NumberFormatException ex) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}
				
				Level level = new Level(width, height);
				Editor.getInstance().setLevel(level);
				Editor.getInstance().getLevelView().setDimension(new Dimension(level.getWidth() * Editor.UNIT, level.getHeight() * Editor.UNIT));
				Editor.getInstance().getTilePanel().getPlacedTileListModel().clear();
				dispose();
			}
			
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
		
		panel2.add(okButton);
		panel2.add(cancelButton);

		add(panel2);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
