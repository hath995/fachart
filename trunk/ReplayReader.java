/**
 *
 * @author Aaron Elligsen
 * 
 */
package FAChart;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import java.nio.channels.FileChannel;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
/*release features *CPM/
		*MacroMicroCPM/
		*BuildOrder by time
		BuildOrder by engineer
		*CPM order type distribution
		unit distribution?
*/
public class ReplayReader
{
	
	byte[] thereplay;
	int endOfFile;
	
	public final int ONEBYTE    = 1; //8 bits
	public final int TWOBYTES   = 2; //16 bits
	public final int THREEBYTES = 3; //24 bits
	public final int FOURBYTES  = 4; //32 bits
	
        
	public ReplayReader(byte[] replaybytes, int file_length)
	{
		thereplay = replaybytes;
		endOfFile = file_length;
	}
	
        /**
	 * As Java does not support unsigned integers it was necessary to create
	 * a function to convert the bytes Java read in.
	 *
	 * @param words The java io for accessing raw bits returns in byte arrays
	 * @param index This index is where an integer should start if dealing 
	 * with large arrays of various data.
	 * @param numBytes The number of 8 bit bytes to restore the integer (1, 2, and 4 are most common)
	 * @return long int containing an unsigned int
	 */
	public static long unsignedInt(byte []  words, int index, int numBytes) //converts little endian unsigned int to int
	{
		long value = 0; 
		int byteStep = 0;
		for(int i = 0; i < numBytes; i++)
		{
			int temp = (int) words[index+i] & 0xFF;
			
			value = value | (temp << byteStep);
			byteStep+=8;
			
		}
		return value;
	}
	
	
	

	/**
	 * Analyze is the meat and potatoes of this program. It started out as a
	 * very procedural function. I have begun breaking it apart and making it
	 * more class oriented. However, the analysis portion of the program
	 * is best handled by a rather large switch statement. Mining the data as
	 * the 
	 *
	 * Please keep in mind THIS IS A BEST GUESS at understanding a file format
	 * produced by a company who has not released said format's full description.
	 * Right now it is full of magic numbers and things done just because it 
	 * gets the data out correctly. 
	 *
	 * @param aReplay Takes the replay as a File
	 * @param parent The analyze function may need to pass message back to 
	 * the parent frame in case of errors/exceptions
	 */
	public Replay Analyze(byte[] thereplay, Hashtable unitTable)
	{
		
		Replay replayData = new Replay(unitTable);
	
		///Initializing resource for Build orders

			byte [] inputWord = new byte[1];
			
			
			/////////////////Header
			replayData.analyzeHeader(thereplay, endOfFile);
			
                        ////////////////Everything else
			replayData.setGameTime(thereplay);
                        replayData.setMicroAPM(thereplay);
			
                        replayData.setActionList(thereplay);
                        replayData.setAPMS_actionTotal(thereplay);
                        replayData.setBuildorder(thereplay);
			
			
			return replayData;
		
		
	}

}



