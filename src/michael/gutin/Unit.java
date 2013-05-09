package michael.gutin;

import java.util.ArrayList;

import android.util.Log;

public class Unit extends TileEntity {
	
	public int type;
	public int team;
	public float move;
	public int moveType;
	public float[] attack;
	public float hp;
	public float maxhp;
	public float def;
	public int defType;
	public boolean hasActed;
	public int minAttackRange;
	public int maxAttackRange;
	public int price;
	public int[] choices = {1,0};
	public boolean haste=false;
	
	/**
	 * Actions
	 */
	public static int WAIT = 0;
	public static int ATTACK = 1;
	public static int ATTACK2 = 2;
	public static int COUNTERATTACK = 3;
	public static int CANCEL=4;
	public static int SELECTED = 10;

	/**
	 * Armor Types
	 */
	public static int INFANTRY=0;
	public static int ARMOR=1;
	public static int LIGHTARMOR=2;
	
	/**
	 * Move Types
	 */
	public static int FOOT=0;
	public static int TANK=1;
	public static int WHEEL=2;
	
	/**
	 * Static Units
	 */
	public static Unit SOLDIER_UNIT = new Unit(GameView.DUDE, -1);
	public static Unit TANK_UNIT = new Unit(GameView.TANK, -1);
	public static Unit RECON_UNIT = new Unit(GameView.RECON, -1);

	
	public void Set(float move, int moveType, float[] attack, float hp, float def, int defType, int minAttackRange, int maxAttackRange, int price)
	{
		this.move = move;
		this.moveType = moveType;
		this.attack = attack;
		this.hp = hp;
		this.maxhp=hp;
		this.def = def;
		this.defType = defType;
		this.minAttackRange = minAttackRange;
		this.maxAttackRange = maxAttackRange;
		this.price = price;
	}
	
	public int getSprite(int frame)
	{
		if(team == 1 && type ==305 && !hasActed){
			return type+2*team+1+frame%2;
		}
		return type+2*team+1; 
	}
	
	public int getExhaustSprite()
	{
		return type-1;
	}
	
	public int[] getChoices(GameView field)
	{
		ArrayList<Integer> choices = new ArrayList<Integer>();
		
		
		
		if(!field.selectedAttack.isEmpty())
			choices.add(ATTACK);
		choices.add(WAIT);
		choices.add(CANCEL);

		int[] output = new int[choices.size()];
		for(int i=0;i<choices.size();i++)
			output[i]=choices.get(i).intValue();
		
		return output;
	}
	
	
    @SuppressWarnings("static-access")
	public void Wait(GameView field)
    {
    	if(field.cursor == null || field.tileAt(field.cursor) == null || field.tileAt(field.cursor).unit == null){
    		Log.e("Null variable", "Cursor or Cursor Tile or Cursor Tile Unit is null in Wait");
    	}
    	field.tileAt(field.cursor).unit.hasActed = true;
    	field.finishAction();
    	field.action = field.NOTHING;
    }
    
    /**
     * Which action a unit performs is no longer given explicitly, instead it must be derived from the game state
     * @param field
     */
    public void performAction(GameView field){
    	if(field == null || field.cursor == null){
    		Log.e("Null variable", "Perform action has null field or cursor");
    	}
    	if(field.tileAt(field.cursor).unit == this){
    		Wait(field);
    	}else if(field.contains(field.selectedAttack, field.cursor)){
    		attack(field, false, field.selected, field.cursor);
    	}else{
    		field.denyPressed();
    	}
    }
    

	
	@Deprecated
	public void performAction(int index, GameView field)
	{
		if(index == WAIT) Wait(field);
		else if(index == ATTACK) attackchoice(field);
		else if(index == ATTACK2) attack(field,false,field.selected,field.cursor);
		else if(index == COUNTERATTACK)	attack(field,true, field.cursor,field.selected);
		else if(index == CANCEL) field.denyPressed();
	}
	
    public void attackchoice(GameView field)
    {
    	//field.field.limitCursor(field.selectedAttack);
    	field.action = ATTACK2;
    }
    
    public void attack(GameView field, boolean counter, Coordinate att, Coordinate tar)
    {
    	Tile attacker = field.tileAt(att);
    	Tile target = field.tileAt(tar);
    	if(!field.cursor.equals(tar)){
    		field.denyPressed();
    		return;
    	}
    	
    	damage(attacker, target);
    	
    	if(target.unit.isDead()) field.destroyUnit(tar);
    	else if(!counter && field.isInAttackRange(tar, att))	attack(field,true, field.cursor,field.selected);
    	
    	if(!counter)
    	{
    		attacker.unit.hasActed = true;
    		field.finishAction();
    	}
    	
    }
	
	public int attackhp()
	{
		if((int)hp == hp)
			return (int)hp;  
		return (int)hp +1;
	}
	
	public void damage(Tile attacker, Tile target)
	{
		target.unit.hp-=0.75*attacker.unit.attack[target.unit.defType]/(target.unit.def*target.terrain.def)*attacker.unit.attackhp();
	}
	
	public boolean isDead()
	{
		if(hp<=0)
			return true;
		return false;
	}
	
	public Unit(int type, int team)
	{
		this.type = type;
		this.team = team;
		if(type == GameView.DUDE)
		{
			float[] attack = {4,1,2};
			Set(2,FOOT,attack,9,4,INFANTRY,1,2, 100);
			return;
		}
		if(type == GameView.TANK)
		{
			float[] attack = {5,4,7};
			Set(5,TANK,attack,5,4,ARMOR,1,1,1000);
			return;
		}
		if(type == GameView.RECON)
		{
			float[] attack = {7,2,5};
			Set(7,WHEEL,attack,6,5,LIGHTARMOR,1,1,500);
			haste = true;
			return;
		}
		System.out.println("Invalid unit type");
	}
	
	public String toString()
	{
		return "Type: " + type+ " Team: " + team + " Hp: " + hp;
	}
	
	public static Unit getUnit(int type)
	{
		if(type == GameView.DUDE){
			return SOLDIER_UNIT;
		}
		if(type == GameView.TANK){
			return TANK_UNIT;
		}
		if(type == GameView.RECON){
			return RECON_UNIT;
		}
		System.out.println("getUnit cannot find unit");
		return null;
	}
	

}
