package michael.gutin;

import java.util.LinkedList;

public class Building extends TileEntity{
	
	public static final int BUILD =1;
	public static final int NOTHING =0;
	
	public int type;
	public int team;
	public float def;
	public int actionType;
	
	public static int[] FACTORY_BUILD_LIST = {GameView.DUDE, GameView.TANK, GameView.RECON};

	public static int[] getBuildList(int type){
		if(type == GameView.FACTORY)
			return FACTORY_BUILD_LIST;
		System.out.println("cannot find build list");
		return null;
	}
	
	public Building(int type,int team)
	{
		this.type = type;
		this.team = team;
		if(type==-1)//This is here because for some reason in Java constructors of extended classes have to call their base class constructor (super dumb)
			return;
		
		if(type==200) 
		{
			Set(1.5f, BUILD);
			return;
		}
		if(type==204){
			Set(1.2f,NOTHING);
			return;
		}
		System.out.println("Invalid building type");
	}
	
	public int getSprite()
	{
		return type+team+2;
	}
	
	public void performAction(GameView field)
	{
		if(actionType==NOTHING)	return;
		
		if(actionType==BUILD)	field.action = field.BUILD;
	}
	
	
	public boolean tryPurchase(int selection, GameView field)
	{
		Unit unit = Unit.getUnit(getBuildList(type)[selection]);
		if(unit.price>field.player[field.currentTurn].funds)
			return false;
		else
		{
			field.player[field.currentTurn].funds-=unit.price;
			field.createTileEntity(GameView.UNIT, unit.type, unit.team, field.selected);
			if(!field.tileAt(field.selected).unit.haste)	field.tileAt(field.selected).unit.hasActed = true;
			
			field.action = GameView.NOTHING;
			return true;
		}
		
	}
	
	public void Set(float def, int actionType)
	{
		this.def = def;
		this.actionType = actionType;
	}
	
	public String toString()
	{
		return "Type: " + type+ " Team: " + team;
	}
}
