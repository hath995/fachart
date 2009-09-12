/**
 *
 * @author Aaron Elligsen
 * 
 */

package FAChart;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 *
 * @author admin
 */
public class loadListener implements ActionListener {
    JFrame Parent;
    Hashtable<String, String> uTable;
    public loadListener(JFrame p,  Hashtable<String, String> unitTable) {
        Parent = p;
        uTable = unitTable;
    }

    public void actionPerformed(ActionEvent e) {
         JFileChooser fc = new JFileChooser();
         fc.setMultiSelectionEnabled(true);
         fc.setAcceptAllFileFilterUsed(false);
         fc.setFileFilter(new ReplayFilter());
         
         try{
        	 Scanner scan = new Scanner(new File("configSave"));
        	 String previousFileDir = scan.nextLine();
        	 fc.setCurrentDirectory(new File(previousFileDir));
         }catch(FileNotFoundException d) {
        	 System.err.println("Error: Could not open previous file location");
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
                	System.err.println("Error: Could not save file location");
                }

                for(int i = 0; i < file.length; i++)
                {
	                JDialog temp = new JDialog(Parent,file[i].getName(),false);
					temp.setMaximumSize(new Dimension(700,600));
					temp.setPreferredSize(new Dimension(700,600));
					FileInputStream thereplay=null;
					try
					{
						thereplay = new FileInputStream(file[i]);
						try
						{
							int fileSize = thereplay.available();
                                                        System.out.println(fileSize);
							byte[] replaybytes = new byte[fileSize];
							thereplay.read(replaybytes);
						    try
						    { 
						    	temp.getContentPane().add(new FACTabbedPane(replaybytes,uTable, fileSize));
						    }catch(NoClassDefFoundError b) {
						    	JDialog noLibrary = new JDialog(Parent, "Missing library", true);
						    	noLibrary.setPreferredSize(new Dimension(150,50));
						    	noLibrary.setResizable(false);
						    	JLabel missing = new JLabel("Chart library is missing.");
						    	noLibrary.getContentPane().add(missing);
						    	noLibrary.pack();
						    	noLibrary.setVisible(true);
						    }	
						}catch(IOException t){
							JDialog noData = new JDialog(Parent, "Replay:"+file[i].getName()+" data inaccessible", true);
							noData.setPreferredSize(new Dimension(150,50));
							noData.setResizable(false);
							JLabel missing = new JLabel("Replay data could not be read.");
							noData.getContentPane().add(missing);
							noData.pack();
							noData.setVisible(true);
						}
					}catch(FileNotFoundException g) {
						JDialog noFile = new JDialog(Parent, "Replay:"+file[i].getName()+" not found.", true);
						noFile.setPreferredSize(new Dimension(200,50));
						noFile.setResizable(false);
						JLabel missing = new JLabel("Replay:"+file[i].getName()+" is not found");
						noFile.getContentPane().add(missing);
						noFile.pack();
						noFile.setVisible(true);
					}
	                temp.pack();
	                temp.setVisible(true);
                }
               
         } else {
               //Open command canceled by user, hence do nothing.
         }
    }
    
    
    
}
