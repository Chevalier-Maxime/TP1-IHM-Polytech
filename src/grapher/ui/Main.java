package grapher.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import grapher.fc.Function;
import grapher.fc.FunctionFactory;


public class Main extends JFrame implements ListSelectionListener, ActionListener {
	
	public JList<Function> list = new JList<Function>();
	public Grapher grapher;

	public JToolBar tool = new JToolBar();
	public JSplitPane split2 ;
	public JSplitPane split ;

	public JButton plus = null;
	public JButton moins = null;
	
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItemAdd, menuItemDel;
	
	
	public Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//menu
		//Where the GUI is created:
		

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		menu = new JMenu("Expression");
		menuBar.add(menu);

		//a group of JMenuItems
		menuItemAdd = new JMenuItem("Add...");
		menuItemAdd.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItemAdd.addActionListener(this);
		menu.add(menuItemAdd);
		
		menuItemDel = new JMenuItem("Del...");
		menuItemDel.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_2, ActionEvent.ALT_MASK));
		menuItemDel.addActionListener(this);
		menu.add(menuItemDel);
		
		this.setJMenuBar(menuBar);
		
		//fin
		list.addListSelectionListener(this);
		grapher = new Grapher();
		list.setModel(grapher.functions);

		split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,list,tool);
		split2.setResizeWeight(1);
		split2.setDividerSize(0);
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,split2,grapher);
		split.setDividerSize(0);
		
		tool.setRollover(true);
		tool.setFloatable(false);
		
		plus = new JButton("+");
		plus.setActionCommand("+");
		moins = new JButton("-");
		moins.setActionCommand("-");

		plus.addActionListener(this);
		moins.addActionListener(this);

		
		tool.add(plus);
		tool.add(moins);
		
		for(String expression : expressions) {
			grapher.add(expression);
		}
		
	//	add(grapher);
		add(split);
		
		pack();
	}

	public static void main(String[] argv) {
		final String[] expressions = argv;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				new Main("grapher", expressions).setVisible(true); 
			}
		});
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Je suis dans valueChanged");
		grapher.setGras(list.getSelectedValue());
		grapher.repaint();
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if((arg0.getActionCommand()== "+")||(arg0.getSource() == menuItemAdd)){
			String inputValue = JOptionPane.showInputDialog("Nouvelle expression");
			if (inputValue != null) {
				grapher.add(inputValue);
			}
			
		}
		if((arg0.getActionCommand()== "-")||(arg0.getSource() == menuItemDel)) 
		{
			if(list.getSelectedValue() == null)
			{
				JOptionPane.showMessageDialog(null, "Il faut sélectionner la fonction que vous voulez supprimer", "Attention", JOptionPane.ERROR_MESSAGE); 
			}
			else {
	
				int answer = JOptionPane.showConfirmDialog(null,
						"Are you sure you wanna delete this function : "+list.getSelectedValue()+" ?","Confirmation" , JOptionPane.YES_NO_OPTION);
	
				if(answer != JOptionPane.NO_OPTION)
					grapher.functions.remove(list.getSelectedIndex());
			}
		}
		
		
	}
}
