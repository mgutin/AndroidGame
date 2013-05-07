package michael.gutin;

public class TouchArea {
	Coordinate topLeft;
	Coordinate bottomRight;
	int priority;
	
	TouchArea(Coordinate topLeft, Coordinate bottomRight, int priority)
	{
		this.topLeft=topLeft;
		this.bottomRight=bottomRight;
		this.priority=priority;
	}
	
	TouchArea(int x1, int y1, int x2, int y2, int priority)
	{
		topLeft = new Coordinate(x1,y1);
		bottomRight = new Coordinate(x2,y2);
		this.priority = priority;
	}
	
	public boolean isTouched(float x, float y)
	{
		if(x>=topLeft.x && x<=bottomRight.x && y>topLeft.y && y<bottomRight.y)
			return true;
		return false;
	}
}
