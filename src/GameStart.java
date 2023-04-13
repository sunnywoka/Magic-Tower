import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class GameStart extends JFrame implements ActionListener{
	JMenuItem help;
	GamePanel maze = new GamePanel();
	
	public GameStart(){
		super("Nook Inc.'s first game");//Set up game window frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(400,500);
		
		JMenuBar menubar = new JMenuBar();//Set up menu bar
		this.setJMenuBar(menubar);
		
		JMenu helpMenu = new JMenu("Player Guide");//Set up one menu button
		menubar.add(helpMenu);
		
		help = new JMenuItem("Player Guide");
		help.addActionListener(this);
		helpMenu.add(help);
		
		add(maze);//Add main game panel into frame
		this.addKeyListener(maze);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		JComponent sourse = (JComponent)event.getSource();
		//Provide the function for player reading game rules
		if(sourse == help) {
        	JDialog information = new JDialog();
        	information.setTitle("Playing Guide");
        	information.setBounds(
        			new Rectangle(//a new dialog for displaying the rules
        					(int)this.getBounds().getX()+50,
        					(int)this.getBounds().getY()+50,
        					400,500));
        	JTextArea rules = new JTextArea();
        	information.add(rules, BorderLayout.NORTH);
        	Scanner scanner;
            try{//copying rule from README.txt
                scanner = new Scanner(new BufferedReader(new FileReader("README.txt")));
                String item;
                try{
                    while(scanner.hasNextLine()){
                        item = scanner.nextLine();
                        rules.append(String.format("%s%n", item));
                    }
                } finally {
                        scanner.close();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
        	information.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        	information.setVisible(true);
        }
	}
	
	public static void main(String args[]) { new GameStart();}
}