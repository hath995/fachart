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
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import java.nio.channels.FileChannel;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
/*release features *CPM/
		MacroMicroCPM/
		*BuildOrder by time
		BuildOrder by engineer
		*CPM order type distribution
		*unit distribution?
*/
public class ReplayReader
{
	
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
	
	public static long setGameTime(FileInputStream thereplay) throws IOException
	{
		byte[] inputWord = new byte[1];

		long tick = 0;
		while(thereplay.read(inputWord) != -1)
		{
			int message_op = (int)ReplayReader.unsignedInt(inputWord, 0, 1);
			int message_length = 0;
			inputWord = new byte[2];
			thereplay.read(inputWord);
			message_length = (int)ReplayReader.unsignedInt(inputWord, 0, 2);
			if(message_op == 0)
			{
				tick++;
			}
			inputWord = new byte[message_length-3];
			thereplay.read(inputWord);
			inputWord = new byte[1];
		}
		return tick;
	}
	

	public static Replay Analyze(File aReplay, JFrame parent)
	{
		
		Replay replayData = new Replay();
		Scanner scan;
		FileInputStream thereplay=null;
		Hashtable<String, String> unitTable=null;
		try{
			unitTable = new Hashtable<String, String>();
			scan = new Scanner(new File("unitDB.txt"));
                        //System.out.println("Are we here?");
			while(scan.hasNext())
			{

				unitTable.put(scan.next(),scan.nextLine());
			}
			
			
			
		}catch(FileNotFoundException e) {
			JDialog unitDb = new JDialog(parent, "Congfig file missing", true);
			unitDb.setPreferredSize(new Dimension(100,50));
			unitDb.setResizable(false);
			JLabel missing = new JLabel("unitDB.txt is missing.");
			unitDb.getContentPane().add(missing);
			unitDb.pack();
			unitDb.setVisible(true);
		}catch(IOException e) {
			
			
		}
		
		try{
			thereplay = new FileInputStream(aReplay);
		}catch(FileNotFoundException e) {
			JDialog noFile = new JDialog(parent, "Replay not found.", true);
			noFile.setPreferredSize(new Dimension(200,50));
			noFile.setResizable(false);
			JLabel missing = new JLabel("Replay is not found");
			noFile.getContentPane().add(missing);
			noFile.pack();
			noFile.setVisible(true);
			
		}
                        //System.out.println("Or here?");
			//PrintWriter theoutput = new PrintWriter("output.txt");
			//String thename = args[0].substring(0,args[0].indexOf('.'));
			byte [] inputWord = new byte[1];
			
			
			/////////////////Header
			try{
			replayData.ReplayPatchFileId = Header.setReplayPatchFileId(thereplay);
			replayData.ReplayVersionId = Header.setReplayVersionId(thereplay);
			replayData.GameModsSize = Header.setGameModsSize(thereplay);
			replayData.GameMods = Header.setGameMods(replayData.GameModsSize, thereplay);
			replayData.LuaScenarioInfoSize = Header.setLuaScenarioInfoSize(thereplay);
			replayData.LuaScenarioInfo = Header.setLuaScenarioInfo(replayData.LuaScenarioInfoSize, thereplay);
			replayData.NumSources = Header.setNumSources(thereplay);
			replayData.CommandSource = Header.setCommandSource(replayData.NumSources, thereplay);
			replayData.CheatsEnabled = Header.setCheatsEnabled(thereplay);
			replayData.NumArmies = Header.setNumArmies(thereplay);
			replayData.Armies = Header.setArmies(replayData.NumArmies, thereplay);
			replayData.RandomSeed = Header.setRandomSeed(thereplay);
			}catch(IOException a){
				
			}
			
			FileChannel myChannel = thereplay.getChannel();
			Long endOfHeaderPos = new Long(0);
			try{
				endOfHeaderPos = myChannel.position();
				replayData.GameTime = ReplayReader.setGameTime(thereplay);
				myChannel.position(endOfHeaderPos);
			}catch(IOException a){
				
			}
			//theoutput.println("GT: "+replayData.GameTime);
			/////////////////
			//PrintWriter BOoutput = new PrintWriter(thename+".bo");
			inputWord = new byte[1];
			long tick = 0;
			int [] actions = new int[(int)replayData.NumSources];
			int [] difActions = new int[(int)replayData.NumSources];
			
			int [] microActions = new int[(int)replayData.NumSources];
			int [] microDifActions = new int[(int)replayData.NumSources];
                        
            Vector[] actions2 = new Vector[(int)replayData.NumSources];
			Vector[] actions2micro = new Vector[(int)replayData.NumSources];
                        for(int i = 0; i<(int)replayData.NumSources; i++){ actions2[i] = new Vector();}
                        for(int i = 0; i<(int)replayData.NumSources; i++){ actions2micro[i] = new Vector();}
			int[] localTotal = new int[(int)replayData.NumSources];
			int[] localTotalMicro = new int[(int)replayData.NumSources];
			
			int playerturn = 0;
			int [][] pactionslist = new int[(int)replayData.NumSources][40];
			int BOtime = 3600;
			long currentAction = 0;
			int currentPlayer = 0;
			long [] playerLastTurn = new long[(int)replayData.NumSources];
			Vector [] APMS = new Vector[(int)replayData.NumSources];
			Vector [] MAPMS = new Vector[(int)replayData.NumSources];
			for(int i = 0; i<(int)replayData.NumSources; i++){ APMS[i] = new Vector();}
			for(int i = 0; i<(int)replayData.NumSources; i++){ MAPMS[i] = new Vector();}
			long lastTick = 0;
			Hashtable[] engineerBo = new Hashtable[(int)replayData.NumSources];
			for(int i = 0; i<(int)replayData.NumSources; i++){engineerBo[i] = new Hashtable();}
			
			Link buildorder = new Link(replayData.CommandSource[0][0]+"'s build order\n", 0, 0,Long.MAX_VALUE,null);
			for(int i = 1; i < (int)replayData.NumSources;i++)
			{
				buildorder.add(new Link(replayData.CommandSource[i][0]+"'s build order\n", i, 0,Long.MAX_VALUE,null));
			}
			
			try{ 
			while(thereplay.read(inputWord) != -1)
			{
				
				//replay command stream has the following format:
				/*
				// Format of command stream: 
				// 
				// repeat { 
				//   uint8 - message typecode (ECmdStreamOp) 
				//   uint16 - length of message (including header)  
				//   ... - op specific data 
				// } 
				// note 1: message is written little endian style so the number 0xFFAA is written in the file 0xAA 0xFF
				
				*/
                                int cpmTimeIntervalSeconds = 60;
                                int cpmTimeIntervalTicks = cpmTimeIntervalSeconds * 10;
                                int cpmMultiplier = 60/cpmTimeIntervalSeconds;
                                
                                int pointSampleIntervalTicks = 30;
                                
                                
				if(tick >= (600-(cpmTimeIntervalTicks/2)) && lastTick != tick) 
				{
					for(int i = 0; i <replayData.NumSources; i++)
					{
						int deltaActions = actions[i]-difActions[i];
						int microDeltaActions = microActions[i]-microDifActions[i];
						difActions[i] = actions[i];
						microDifActions[i] = microActions[i];
						actions2[i].add(new Integer(deltaActions));
						actions2micro[i].add(new Integer(microDeltaActions));
						localTotal[i] += deltaActions;
						localTotalMicro[i] += microDeltaActions;
						if(tick > (600+(cpmTimeIntervalTicks/2)))
						{
							localTotal[i] -= (Integer)actions2[i].remove(0);
							localTotalMicro[i] -= (Integer)actions2micro[i].remove(0);
                                                        if(tick%pointSampleIntervalTicks==0)
                                                        {
                                                            int floatError =(cpmTimeIntervalTicks/2);
                                                            Point apm = new Point(tick-floatError, localTotal[i]*cpmMultiplier);
                                                            APMS[i].add(apm);
                                                            Point mapm = new Point(tick-floatError, localTotalMicro[i]*cpmMultiplier);
                                                            MAPMS[i].add(mapm);
                                                        }
						}
					}
                                        lastTick = tick;
				}
                                
                                
				/*if(tick%cpmTimeIntervalTicks==0 && lastTick != tick && tick != 0)
				{
					for(int i = 0; i <replayData.NumSources; i++)
					{
						if(actions[i] != 0)
						{
						int deltaActions = actions[i]-difActions[i];
						Point apm = new Point(tick, deltaActions*cpmMultiplier);
						APMS[i].add(apm);
						int microDeltaActions = microActions[i]-microDifActions[i];
						Point mapm = new Point(tick, microDeltaActions*cpmMultiplier);
						MAPMS[i].add(mapm);
						}
						difActions[i] = actions[i];
						microDifActions[i] = microActions[i];
					}
					lastTick = tick;
				}*/
				
				
				int typecode = (int)inputWord[0];
				inputWord = new byte[2];
				try {
				thereplay.read(inputWord);
				}catch(IOException a){
					
				}
				int message_length = (int)ReplayReader.unsignedInt(inputWord,0,2)-3;
				inputWord = new byte[1];
				
				byte [] messageWord = new byte[1];
				if(message_length >= 0)
				{
					messageWord = new byte[message_length];
					try{
						thereplay.read(messageWord);
					}catch(IOException a){
						
					}
				}else{
					//theoutput.println("NIerror"); //negative index	
				}
				switch(typecode)
				{
				case 0: /*theoutput.println("CMDST_Advance");*/ tick++; break;
				case 1: /*theoutput.println("CMDST_SetCommandSource");*/ playerturn = (int)messageWord[0]; break;
				case 2: /*theoutput.println("CMDST_CommandSourceTerminated");*/ break;	
				case 3: /*theoutput.println("CMDST_VerifyChecksum");*/ break;
				case 4: /*theoutput.println("CMDST_RequestPause");*/ break;	
				case 5: /*theoutput.println("CMDST_Resume");*/ break;
				case 6: /*theoutput.println("CMDST_SingleStep");*/ break;	
				case 7: /*theoutput.println("CMDST_CreateUnit");*/ break;
				case 8: /*theoutput.println("CMDST_CreateProp");*/ break;	
				case 9: /*theoutput.println("CMDST_DestroyEntity");*/ break;
				case 10: /*theoutput.println("CMDST_WarpEntity");*/ break;
				case 11: /*theoutput.println("CMDST_ProcessInfoPair "); */
					if(currentAction != tick || currentPlayer != playerturn) {actions[playerturn]++; currentAction = tick;
					currentPlayer = playerturn; playerLastTurn[playerturn]=tick;}
					break;
				case 12: /* theoutput.println("Player: " + playerturn +" CMDST_IssueCommand " + tick);*/ 
					if(currentAction != tick || currentPlayer != playerturn) 
					{
						actions[playerturn]++;
						
					}
					 playerLastTurn[playerturn]=tick;
					int numUnits = (int)ReplayReader.unsignedInt(messageWord, 0, 4);
					int index=4;
					long [] entIds = new long[numUnits];
					for(int b = 0; b < numUnits; b++)
					{
						entIds[b] = ReplayReader.unsignedInt(messageWord,index,4);
						index+=4;
					}
					long commandId = ReplayReader.unsignedInt(messageWord, index, 4);
					index+=8; //move past commandID, and a 32 bit -1
					int commandType = (int)ReplayReader.unsignedInt(messageWord, index, 1);
					if(currentAction != tick || currentPlayer != playerturn) {
					pactionslist[playerturn][commandType]++;
					currentAction = tick;
					currentPlayer = playerturn;
					switch(commandType)
						{
						
							case 5: /*theoutput.print("UNITCOMMAND_BuildSiloTactical ");*/break; 
							case 6: /*theoutput.print("UNITCOMMAND_BuildSiloNuke ");*/ break;
							case 7: /*theoutput.print("UNITCOMMAND_BuildFactory ");*/ break;
							case 8: /*theoutput.print("UNITCOMMAND_BuildMobile ");*/break; 
							case 9: /*theoutput.print("UNITCOMMAND_BuildAssist ");*/break;
							case 15: /*theoutput.print("UNITCOMMAND_Guard ");*/ break;
							case 16: /*theoutput.print("UNITCOMMAND_Patrol ");*/break;
							case 19: /*theoutput.print("UNITCOMMAND_Reclaim "); */break;
							case 20: /*theoutput.print("UNITCOMMAND_Repair "); */break;
							case 21: /*theoutput.print("UNITCOMMAND_Capture ");*/ break;
							case 27: /*theoutput.print("UNITCOMMAND_Upgrade ");*/ break;
							case 28: /*theoutput.print("UNITCOMMAND_Script ");*/ break;
							case 29: /*theoutput.print("UNITCOMMAND_AssistCommander ");*/ break;
							case 32: /*theoutput.print("UNITCOMMAND_Sacrifice "); */break;
							case 33: /*theoutput.print("UNITCOMMAND_Pause "); */break;
							case 38: /*theoutput.print("UNITCOMMAND_SpecialAction ");*/ break;
							default: microActions[playerturn]++;
						}
					}
					index+=5; //move past commandType and a 32 bit -1
					int STITarget = (int)ReplayReader.unsignedInt(messageWord, index, 1);
					
					
					if(commandType == 7 || commandType == 8 || commandType == 27)
					{
						if(STITarget == 0)
						{
							index+=6;
						}else if(STITarget == 2) {
							index+=1+3*4+1+4;//move past stitarget, 3 floats, a zero,a -1 32bit int
						}
						
						char[] unitBluePrint = new char[7];
						for(int i = 0; i < 7; i++)
						{
							unitBluePrint[i] = (char)ReplayReader.unsignedInt(messageWord, index+i, 1);
						}
						String unitBP = new String(unitBluePrint);
						index+=7; //skip the blueprint
						String workers="";
						if(commandType == 8)
						{
							workers=" built by ";
							for(int i = 0; i < numUnits; i++)
							{
								if(engineerBo[playerturn].containsKey(entIds[i]))
								{
									workers += (String)engineerBo[playerturn].get(entIds[i])+" ";
								}else{
									if(engineerBo[playerturn].size() == 0)
									{
										engineerBo[playerturn].put(entIds[i],replayData.CommandSource[playerturn][0] +"'s ACU");
										workers += (String)engineerBo[playerturn].get(entIds[i])+" ";
									}else{
										engineerBo[playerturn].put(entIds[i],"Engineer " + engineerBo[playerturn].size());
										workers += (String)engineerBo[playerturn].get(entIds[i])+" ";
									}
								}
							}
						}
						if(tick <= BOtime)
						{
							buildorder.add(new Link(unitTable.get(unitBP)+workers,playerturn,tick, commandId, entIds));
						}
						
						
					
					
					
					}
                                        if(commandType == 19)
                                        {
                                                String workers="";
                                                for(int i = 0; i < numUnits; i++)
                                                {
                                                        if(engineerBo[playerturn].containsKey(entIds[i]))
                                                        {
                                                                workers += (String)engineerBo[playerturn].get(entIds[i])+" ";
                                                        }else{
                                                                if(engineerBo[playerturn].size() == 0)
                                                                {
                                                                        engineerBo[playerturn].put(entIds[i],replayData.CommandSource[playerturn][0] +"'s ACU");
                                                                        workers += (String)engineerBo[playerturn].get(entIds[i])+" ";
                                                                }else{
                                                                        engineerBo[playerturn].put(entIds[i],"Engineer " + engineerBo[playerturn].size());
                                                                        workers += (String)engineerBo[playerturn].get(entIds[i])+" ";
                                                                }
                                                        }
                                                }
                                                if(tick <= BOtime)
						{
							buildorder.add(new Link("Reclaiming by "+workers,playerturn,tick, commandId, entIds));
						}
						
                                        }
					break;
				case 13: /*theoutput.println("CMDST_IssueFactoryCommand"); */actions[playerturn]++;playerLastTurn[playerturn]=tick; break;
				case 14: /*theoutput.println("CMDST_IncreaseCommandCount");*/ break;
				case 15: /*theoutput.println("CMDST_DecreaseCommandCount");*/
					if(tick <= BOtime)
					{
						buildorder.remove(ReplayReader.unsignedInt(messageWord, 0, 4));
					}
					break;
				case 16: /*theoutput.println("CMDST_SetCommandTarget");*/ break;
				case 17: /*theoutput.println("CMDST_SetCommandType"); */break;
				case 18: /*theoutput.println("CMDST_SetCommandCells");*/ break;
				case 19: /*theoutput.println("CMDST_RemoveCommandFromQueue ");*/
					if(tick <= BOtime)
					{
						buildorder.remove(ReplayReader.unsignedInt(messageWord, 0, 4));
					}
					if(currentAction != tick || currentPlayer != playerturn) {actions[playerturn]++; currentAction = tick;
					currentPlayer = playerturn; playerLastTurn[playerturn]=tick;} break;
				case 20: /*theoutput.println("CMDST_DebugCommand");*/ break;
				case 21: /*theoutput.println("CMDST_ExecuteLuaInSim");*/ break;
				case 22: /*theoutput.println("CMDST_LuaSimCallback"); */
					if(currentAction != tick || currentPlayer != playerturn) {actions[playerturn]++; currentAction = tick;
					currentPlayer = playerturn; playerLastTurn[playerturn]=tick;}break;
				case 23: /*theoutput.println("CMDST_EndGame");*/ break;
				default: /*theoutput.println("nw"+inputWord[0]); */break;
				}
				
			}
			}catch(IOException a){
				
			}
			
			float []gametime = new float[(int)replayData.NumSources];
			//theoutput.println("Game tick count: " + tick);
			for(int i = 0; i < replayData.NumSources; i++) 
			{
				if(playerLastTurn[i] > 0)
				{
					int tickseconds = (int)playerLastTurn[i]/10;
					gametime[i] = tickseconds/60;
				}
			}
			replayData.PlayerGameTimes = gametime;
			replayData.APMS = APMS;
			replayData.MicroAPM = MAPMS;
			replayData.PlayerLastTurn = playerLastTurn;
			replayData.ActionsList = pactionslist;
                        replayData.buildorder = buildorder;
                        replayData.ActionsTotal = actions;
			
			/*for(int i = 0; i < replayData.NumSources; i++)
			{
				if(actions[i] != 0)
				{
					theoutput.println("Player " + replayData.CommandSource[i][0] + " Actions total: " + actions[i] +" gametime: " + playerLastTurn[i]);
					float aveapm = actions[i]/gametime[i];
					theoutput.println("Player " + i + " Average APM: " + aveapm);
					for(int j = 0; j < 40; j++)
					{
						if(pactionslist[i][j] != 0)
						{
							theoutput.println(j + " count: " + pactionslist[i][j]);	
						}
					}
					for(int j = 0; j < APMS[i].size(); j++)
					{
						Point temp = (Point)APMS[i].get(j);	
						theoutput.println(temp.toString());
					}

				}
			}*/
			
			/*for(int i = 0; i < replayData.NumSources; i++)
			{
				for (Enumeration e = engineerBo[i].keys() ; e.hasMoreElements() ;) {
					Link temp = (Link)engineerBo[i].get(e.nextElement());
					temp.writeBO(BOoutput);

				}
			}*/

			//theoutput.println("");
			//buildorder.writeBO(BOoutput);
			//BOoutput.close();
            try{            
            	thereplay.close();
            }catch(IOException e) {
            	
            }
			//theoutput.close();
			return replayData;
		
		
	}

}


/*
0 UNITCOMMAND_None 
1 UNITCOMMAND_Stop micro
2 UNITCOMMAND_Move micro
3 UNITCOMMAND_Dive micro
4 UNITCOMMAND_FormMove micro
5 UNITCOMMAND_BuildSiloTactical macro
6 UNITCOMMAND_BuildSiloNuke macro
7 UNITCOMMAND_BuildFactory macro
8 UNITCOMMAND_BuildMobile macro
9 UNITCOMMAND_BuildAssist macro
10 UNITCOMMAND_Attack micro
11 UNITCOMMAND_FormAttack micro
12 UNITCOMMAND_Nuke micro
13 UNITCOMMAND_Tactical micro
14 UNITCOMMAND_Teleport micro
15 UNITCOMMAND_Guard 
16 UNITCOMMAND_Patrol 
17 UNITCOMMAND_Ferry micro
18 UNITCOMMAND_FormPatrol micro
19  UNITCOMMAND_Reclaim macro
20 UNITCOMMAND_Repair macro
21 UNITCOMMAND_Capture macro
22 UNITCOMMAND_TransportLoadUnits micro
23 UNITCOMMAND_TransportReverseLoadUnits micro
24 UNITCOMMAND_TransportUnloadUnits micro
25 UNITCOMMAND_TransportUnloadSpecificUnits micro
26 UNITCOMMAND_DetachFromTransport micro
27 UNITCOMMAND_Upgrade macro
28 UNITCOMMAND_Script macro
29 UNITCOMMAND_AssistCommander micro
30 UNITCOMMAND_KillSelf micro
31 UNITCOMMAND_DestroySelf micro
32 UNITCOMMAND_Sacrifice macro
33 UNITCOMMAND_Pause macro
34 UNITCOMMAND_OverCharge micro
35 UNITCOMMAND_AggressiveMove micro
36 UNITCOMMAND_FormAggressiveMove micro
37 UNITCOMMAND_AssistMove micro
38 UNITCOMMAND_SpecialAction macro
39 UNITCOMMAND_Dock micro

// Format of CmdData: 
// 
// CmdId - id 
// uint8 - command type (EUnitCommandType) 
// STITarget - target 
// int32 - formation index or -1 
// if formation index != -1 
// { 
//   Quaternionf - formation orientation 
//   float - formation scale 
// } 
// string - blueprint ID or the empty string for no blueprint 
// ListOfCells - cells 
// int32 - count 

// Format of STITarget: 
// 
// uint8 - target type (ESTITargetType) 
// if target type == STITARGET_Entity { 
//   EntId - entity id 
// } 
// if target type == STITARGET_Position { 
//   Vector3f - position 
// } 


// Format of ListOfCells: 
// 
// uint32 - num cells 
// repeat num cells times { 
//   int16 - x 
//   int16 - z 
// } 

// Format of EntIdSet: 
// 
// uint32 - number of entity ids this may be gone
// repeat number of entity ids times { 
//   EndId - entity id 
// }


enum ECmdStreamOp 
	{ 
	    CMDST_Advance, 
	    // uint32 - number of beats to advance. 
	
	    CMDST_SetCommandSource, 
	    // uint8 - command source 
	
	    CMDST_CommandSourceTerminated, 
	    // no args. 
	
	    CMDST_VerifyChecksum, 
	    // MD5Digest - checksum 
	    // uint32 - beat number 
	
	    CMDST_RequestPause, 
	    CMDST_Resume, 
	    CMDST_SingleStep, 
	    // All with no additional data. 
	
	    CMDST_CreateUnit, 
	    // uint8 - army index 
	    // string - blueprint ID 
	    // float - x 
	    // float - z 
	    // float - heading 
	
	    CMDST_CreateProp, 
	    // string - blueprint ID 
	    // Vector3f - location 
	
	    CMDST_DestroyEntity, 
	    // EntId - entity 
	
	    CMDST_WarpEntity, 
	    // EntId - entity 
	    // VTransform - new transform 
	    
	    CMDST_ProcessInfoPair, 
	    // EntId - entity 
	    // string - arg1 
	    // string - arg2 
	
	    CMDST_IssueCommand, 
	    // uint32 - num units 
	    // EntIdSet - units 
	    // CmdData - command data 
	    // uint8 - clear queue flag 
	
	    CMDST_IssueFactoryCommand, 
	    // uint32 - num factories 
	    // EntIdSet - factories 
	    // CmdData - command data 
	    // uint8 - clear queue flag 
	
	    CMDST_IncreaseCommandCount, 
	    // CmdId - command id 
	    // int32 - count delta 
	
	    CMDST_DecreaseCommandCount, 
	    // CmdId - command id 
	    // int32 - count delta 
	
	    CMDST_SetCommandTarget, 
	    // CmdId - command id 
	    // STITarget - target 
	
	    CMDST_SetCommandType, 
	    // CmdId - command id 
	    // EUnitCommandType - type 
	
	
	    CMDST_SetCommandCells, 
	    // CmdId - command id 
	    // ListOfCells - list of cells 
	    // Vector3f - pos 
	
	    CMDST_RemoveCommandFromQueue, 
	    // CmdId - command id 
	    // EntId - unit 
	
	    CMDST_DebugCommand, 
	    // string -- the debug command string 
	    // Vector3f -- mouse pos (in world coords) 
	    // uint8 -- focus army index 
	    // EntIdSet -- selection 
	
	    CMDST_ExecuteLuaInSim, 
	    // string -- the lua string to evaluate in the sim state 
	
	    CMDST_LuaSimCallback, 
	    // string - callback function name 
	    // LuaObject - table of function arguments 
	
	    CMDST_EndGame, 
	    // no args. 
	}
*/
