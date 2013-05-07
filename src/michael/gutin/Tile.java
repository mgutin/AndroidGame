package michael.gutin;

public class Tile {

	public Unit unit = null;
	public Building building = null;
	public Terrain terrain = null;
	
	public Tile()
	{
		
	}
	
	public Tile(int index)
	{
		terrain = new Terrain(index);
	}


	
	public Tile(Tile other)
	{
		this.terrain = other.terrain;
		this.building = other.building;
		this.unit = other.unit;
	}
	
	public String toString()
	{
		String output = "Unit: (";
		if(unit == null)
			output = output +"null)";
		else
			output= output + unit +")";
		output = output+ "\nBuilding: (";
		if(building == null)
			output= output + "null)";
		else	
			output = output + building + ")";
		return  output;
	}
}
