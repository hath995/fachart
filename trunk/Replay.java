package FAChart;

import java.util.Hashtable;
import java.util.Vector;

public class Replay
{
	public String ReplayPatchFileId;
	public String ReplayVersionId;
	
	public long GameModsSize;
	public Hashtable GameMods;
	
	public long LuaScenarioInfoSize;
	public Hashtable LuaScenarioInfo;
	
	public long NumSources;
	public String [][] CommandSource;
	
	public long CheatsEnabled;
	
	public long NumArmies;
	public Hashtable[] Armies;
	
	public long RandomSeed;
	
	public long GameTime;
	public float[]PlayerGameTimes;
	public Vector [] APMS;
	public Vector [] MicroAPM;
	public Link buildorder;
	public int [][] ActionsList;
        public int []ActionsTotal;
	
	public long[] PlayerLastTurn;
	//public Hashtable EngineerBuildOrder;
	
	public Replay()
	{
		
		ReplayPatchFileId=null;
		ReplayVersionId=null;
		
		GameModsSize = 0;
		GameMods = null;
		LuaScenarioInfoSize = 0;
		LuaScenarioInfo = null;
		NumSources = 0;
		CommandSource = null;
		
		CheatsEnabled = 0;
		NumArmies = 0;
		Armies = null;
		
		RandomSeed = 0;
		GameTime = 0;
		APMS = null;
		MicroAPM = null;
		buildorder = null;
		ActionsList = null;
                ActionsTotal=null;
		PlayerGameTimes = null;
		//EngineerBuildOrder = null;
		
	}
	
}
