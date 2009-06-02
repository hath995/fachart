package FAChart;

/*Aaron Elligsen*/

public class Link
{
	public Link next;
	public Link previous;
	public String theObject;
	public long timestamp;
	public int player;
	public long commandId;
	public long []entityIds;

	public Link(String obj,int pn, long ts, long ci, long[] eis)
	{
		next 	 = null;
		previous = null;
		theObject= obj;
		player 	 = pn;
		timestamp= ts;
		commandId= ci;
		entityIds= eis;
	}
	
	public void add(Link cevent)
	{
		Link temp = this;
		while(cevent.player > temp.player && temp.next != null)
		{
			temp = temp.next;
		}while(cevent.timestamp > temp.player && temp.next != null && temp.next.player == cevent.player) {
			temp = temp.next;	
		}
		if(temp.next != null)
		{
			cevent.next = temp.next;
			cevent.next.previous = cevent;
		}
		cevent.previous = temp;
		temp.next = cevent;
	}
	
	public void remove(long ci)
	{
		Link temp = this;
		while(temp.commandId != ci && temp.next != null)
		{
			temp = temp.next;	
		}
		if(temp.commandId == ci)
		{
			if(temp.next != null)
			{
				temp.next.previous = temp.previous;
				temp.previous.next = temp.next;
				
			}else{
				temp.previous.next = null;
			}
		}
		
	}
	
	public String writeBO()
	{
                String theBo="Player/Timestamp/Game Time/Action\n";
		Link temp = this;
		while(temp.next != null)
		{
			int timemins = (int)Math.floor(temp.timestamp/600);
			int timesecs = (int)Math.floor((temp.timestamp/10)-timemins*60);
			theBo+=temp.player + " " + temp.timestamp + " " + timemins+ ":" +timesecs+ " " + temp.theObject+"\n";
			temp = temp.next;
		}
                return theBo;
	}

}