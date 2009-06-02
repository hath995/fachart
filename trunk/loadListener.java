/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FAChart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.PrintWriter;
import java.util.Scanner;
import java.lang.StringBuffer;
import java.awt.Dimension;


/**
 *
 * @author admin
 */
public class loadListener implements ActionListener {
    JFrame Parent;
    public loadListener(JFrame p) {
        Parent = p;
    }

    public void actionPerformed(ActionEvent e) {
         JFileChooser fc = new JFileChooser();
         fc.setMultiSelectionEnabled(true);
         fc.setAcceptAllFileFilterUsed(false);
         fc.setFileFilter(new ReplayFilter());
         
         try{
        	 Scanner scan = new Scanner(new File("configSave"));
        	 String previousFileDir = scan.nextLine();
        	 //System.out.println(previousFileDir);
        	 fc.setCurrentDirectory(new File(previousFileDir));
         }catch(FileNotFoundException d) {
        	
         }
         
         
         int returnVal = fc.showOpenDialog(Parent);
         
         if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file[] = fc.getSelectedFiles();
                
                try{
                	PrintWriter print = new PrintWriter("configSave");
                	String fileDir = file[0].getAbsolutePath();
                	print.println(fileDir.substring(0, fileDir.lastIndexOf('\\')+1));
                	print.close();
                }catch(FileNotFoundException b) {
                	
                }
                
                
                
                for(int i = 0; i < file.length; i++)
                {
                JDialog temp = new JDialog(Parent,file[i].getName(),false);
				temp.setMaximumSize(new Dimension(700,600));
				temp.setPreferredSize(new Dimension(700,600));
				try{ 
                temp.getContentPane().add(new FACTabbedPane(file[i],Parent));
                }catch(NoClassDefFoundError b) {
					JDialog noLibrary = new JDialog(Parent, "Missing library", true);
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
               
         } else {
               
         }
    }
    
    
    
}
