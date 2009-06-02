package FAChart;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;


public class Header
{
	static int index = 0;
	
	enum EScrLuaType 
	{ 
	    LUA_TYPE_NUMBER, 
	    LUA_TYPE_STRING, 
	    LUA_TYPE_NIL, 
	    LUA_TYPE_BOOL, 
	    LUA_TYPE_TABLE_BEGIN, 
	    LUA_TYPE_TABLE_END, 
	    LUA_TYPE_MAX//(0xff) 
	} 
	
	public static String returnNextString(FileInputStream areplay) throws IOException
	{
		byte [] inputWord = new byte[1];
		areplay.read(inputWord);
		String current_option = "";
		while(inputWord[0] != 0)
		{
			current_option+=(char)inputWord[0];
			//System.out.println((char)inputWord[0]);
			areplay.read(inputWord);
		}
		return current_option;
	}
	
	public static String returnNextString(byte[] inputString) throws IOException
	{
		int index = 0; 
		String current_string = "";
		while(inputString[index] != 0)
		{
			current_string+=(char)inputString[index];
			//System.out.println((char)inputWord[0]);
			//areplay.read(inputWord);
			index++;
		}
		return current_string;
	}
	
	static public Object parseLuaTable(byte [] input/*, Integer index, EScrLuaType inState*/)
	{
		
		Object result = null; 
		
		int type = (int) input[index];
		index++;
		//System.out.println(index);
		switch(type)
		{	
		case 0: //LUA_TYPE_NUMBER
			Long  number = new Long(ReplayReader.unsignedInt(input, index, 4));
			index +=4;
			result = number;
			//System.out.println("number");
			break;
		case 1: //LUA_TYPE_STRING
			String current_string = "";
			while(input[index] != 0)
			{
				current_string+=(char)input[index];
				index++;
			}
			index++;
			result = current_string;
			//System.out.println("string");
			break;
		case 2: //LUA_TYPE_NIL
			index++;
			//System.out.println("nill");
			break;
		case 3: //LUA_TYPE_BOOL 
			Boolean value;
			if(input[index] == 0)
			{
				value = false;
			}else{
				value = true;
			}
			result = value;
			index++;
			//System.out.println("Boolean");
			break;
		case 4: //LUA_TYPE_TABLE_BEGIN
			Hashtable tableLevel = new Hashtable();
			//System.out.println("table_begin");
			while(input[index] != 5)
			{
				Object key = parseLuaTable(input);
				Object v = parseLuaTable(input);
				tableLevel.put(key, v);
			}	
			index++;
			result = tableLevel;
			break;
			
		case 5: //LUA_TYPE_TABLE_END 
			//System.out.println("Table_end");
			break;
		case 0xff: //LUA_TYPE_MAX
			//System.out.println("MAX");
			break;
		}
		return result; 
	}
	
	static String setReplayPatchFileId(FileInputStream thereplay) throws IOException
	{
		byte [] inputWord = new byte[1];
		String replayFilePatchId = Header.returnNextString(thereplay);
		thereplay.skip(3);
		return replayFilePatchId;
	}
	
	static String setReplayVersionId(FileInputStream thereplay) throws IOException
	{
		String replayVersionId = Header.returnNextString(thereplay);
		thereplay.skip(4);
		return replayVersionId;
	}
	
	static long setGameModsSize(FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[4];
		thereplay.read(inputWord);
		long gameModsSize = ReplayReader.unsignedInt(inputWord,0,4);
		return gameModsSize;
	}
	
	static Hashtable setGameMods(long size, FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[(int)size];
		thereplay.read(inputWord);
		Hashtable gameMods = (Hashtable)Header.parseLuaTable(inputWord);
                Header.index = 0;
		return gameMods;
	}
	
	static long setLuaScenarioInfoSize(FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[4];
		thereplay.read(inputWord);
		long LSIS = ReplayReader.unsignedInt(inputWord,0,4);
		return LSIS;
	}
	
	static Hashtable setLuaScenarioInfo(long size, FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[(int)size];
		thereplay.read(inputWord);
		Hashtable LuaScenarioInfo = (Hashtable)Header.parseLuaTable(inputWord);
                Header.index = 0;
		return LuaScenarioInfo;
	}
	
	static long setNumSources(FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[1];
		thereplay.read(inputWord);
		long numSources = ReplayReader.unsignedInt(inputWord,0,1);
		return numSources;
	}
	
	static String[][] setCommandSource(long numSources, FileInputStream thereplay) throws IOException
	{
		String [][] players = new String[(int)numSources][2];
		byte[] inputWord = new byte[4];
		
		for(int i = 0; i < numSources; i++)
		{
			players[i][0] = Header.returnNextString(thereplay);
			thereplay.read(inputWord);
			players[i][1] = String.valueOf(ReplayReader.unsignedInt(inputWord,0,4));
		}
		return players;
	}
	
	static long setCheatsEnabled(FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[1];
		thereplay.read(inputWord);
		long cheats = ReplayReader.unsignedInt(inputWord,0,1);
		return cheats;
	}
	
	static long setNumArmies(FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[1];
		thereplay.read(inputWord);
		long NumArmies = ReplayReader.unsignedInt(inputWord,0,1);
		return NumArmies;
	}
	
	static Hashtable[] setArmies(long numArmies, FileInputStream thereplay) throws IOException
	{
		Hashtable[] player = new Hashtable[(int)numArmies];
		byte[] inputWord;
		for(int i = 0; i < numArmies; i++)
		{
			inputWord = new byte[4];
			thereplay.read(inputWord);
			long playerDataSize = ReplayReader.unsignedInt(inputWord,0,4);
			inputWord = new byte[(int)playerDataSize];
			thereplay.read(inputWord);
			index=0;
			player[i] = (Hashtable)Header.parseLuaTable(inputWord);
                        Header.index = 0;
			inputWord = new byte[1];
			thereplay.read(inputWord);
			long playerCommandSource = ReplayReader.unsignedInt(inputWord,0,1);
			player[i].put("CommandSource", playerCommandSource);
			if(playerCommandSource != 0xff)
				thereplay.read(inputWord);//skips FF

		}
		return player;
	}
	
	static long setRandomSeed(FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[4];
		thereplay.read(inputWord);
		long randomSeed = ReplayReader.unsignedInt(inputWord,0,4);
		return randomSeed;
	}

}
