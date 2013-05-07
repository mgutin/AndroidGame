package michael.gutin;

public class Terrain extends TileEntity{
	
	public int type;
	public float[] moveCost;
	public float def;
	
	
	
	public void Set(float[] moveCost, float def)
	{
		this.moveCost = moveCost;
		this.def = def;
	}
	
	public Terrain(int index)
	{
		this.type = index;
		if(index==100)
		{
			float[] move = {1,1,1};
			Set(move,1);
			return;
		}
		if(index==101)
		{
			float[] move = {2,10,5};
			Set(move,1.2f);
			return;
		}
		System.out.println("Invalid terrain type");
	}
}
