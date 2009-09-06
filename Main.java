/**
 *
 * @author Aaron Elligsen
 * 
 */

package FAChart;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
//import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import java.awt.Dimension;

/**
 *
 * @author admin
 */
public class Main {

    /**
     * Main method which creates the base frame for the Analyzer. Replays can be 
     * opened from this frame as well as a global close. It provides version info too
     *.
     * @param args the command line arguments containing nothing or full path to 
     * one or several replays
     */
    public static void main(String[] args) {
        try
        {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch (Exception e)
        {
        System.out.println("Unable to load Windows look and feel");
        }
   
        
        JFrame frame = new JFrame("FAchart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar;
        JMenu menu;

        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem menuItem;
        menuItem = new JMenuItem("Open Replays",
                                 KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Loads one or more replay");
        menuItem.addActionListener(new loadListener(frame));
        menu.add(menuItem);
        menu.addSeparator();
        JMenuItem quitMenuItem = new JMenuItem("Quit FAChart",
                                 KeyEvent.VK_Q);
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        quitMenuItem.addActionListener(new exitListener());
        menu.add(quitMenuItem);

        frame.setJMenuBar(menuBar);
        JPanel centralPane = new JPanel();
        centralPane.setLayout(new BoxLayout(centralPane,BoxLayout.Y_AXIS));
        centralPane.setPreferredSize(new Dimension(400,100));
        centralPane.setMaximumSize(new Dimension(400,100));
        JLabel version = new JLabel("Version 1.3");
        JLabel useage = new JLabel("To analyze replay(s) click on File->Open Replays (Ctrl+O for short). ");
		JLabel author = new JLabel("Author: Hath995");
		JLabel thing = new JLabel("\"Supreme Commander\" and \"Supreme Commander Forged Alliance\" are registered");
		JLabel thing2 = new JLabel("trademarks of Gas Power Games Corp all rights reserved.");
        centralPane.add(version);
        centralPane.add(useage);
		centralPane.add(author);
		centralPane.add(thing);
		centralPane.add(thing2);
        frame.getContentPane().add(centralPane);
        
       
        if(args.length > 0)
        {
            File []replayFiles = new File[args.length];
            for(int i = 0; i < args.length;i++)
            {
            	
            	String fp = Main.unEscapeSpaceString(args[i]);
                replayFiles[i] = new File(fp);
                        
                JDialog temp = new JDialog(frame,replayFiles[i].getName(),false);
				temp.setPreferredSize(new Dimension(700,600));
				temp.setMaximumSize(new Dimension(700,600));
				try{
					temp.getContentPane().add(new FACTabbedPane(replayFiles[i], frame));
				}catch(NoClassDefFoundError e) {
					JDialog noLibrary = new JDialog(frame, "Missing library", true);
					noLibrary.setPreferredSize(new Dimension(150,50));
					noLibrary.setResizable(false);
					JLabel missing = new JLabel("Chart library is missing.");
					noLibrary.getContentPane().add(missing);
					noLibrary.pack();
					noLibrary.setVisible(true);
				}
                temp.pack();
                temp.setVisible(true);
            }
        }


        //JTabbedPane centralPane = new FACTabbedPane();
        //frame.getContentPane().add(centralPane);
        frame.pack(); 
        frame.setVisible(true);
        
        

    }
    
    /**
     * Replaces any spaces in path names with *
     * @param String Full replay path
     * @return escaped path string
     */
    static public String unEscapeSpaceString(String fp){
    	StringBuffer theFP = new StringBuffer(fp);
    	while(theFP.lastIndexOf("*")!=-1)
    	{
    		theFP.setCharAt(theFP.lastIndexOf("*"), ' ');
    	}
    	
    	return theFP.toString();
    }

}
