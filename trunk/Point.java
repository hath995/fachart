/**
 *
 * @author Aaron Elligsen
 * 
 */

package FAChart;



public class Point
{
	public long x;
	public int y;
	
	public Point(long a, int  b)
	{
		x=a;
		y=b;
	}
	public String toString()
	{
		return "series.add("+x+","+y+");";	
	}
}
